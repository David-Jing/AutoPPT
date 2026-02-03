package org.fcnabc.autoppt.verses.providers;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

import org.json.JSONObject;
import org.json.JSONException;
import org.fcnabc.autoppt.verses.models.BibleBook;
import org.fcnabc.autoppt.verses.models.BibleVerse;
import org.fcnabc.autoppt.verses.models.BibleVerseCoordinates;
import org.fcnabc.autoppt.verses.VerseProvider;
import org.fcnabc.autoppt.language.Language;

@Slf4j
public class EnglishVerseProvider implements VerseProvider {

    private static final String ESV_API_URL = "https://api.esv.org/v3/passage/text/";
    private String ESV_API_KEY;

    public EnglishVerseProvider() throws IllegalStateException {
        this.ESV_API_KEY = System.getenv("ESV_API_KEY");
        if (this.ESV_API_KEY == null) {
            throw new IllegalStateException("ESV API key not set in environment variables.");
        }
    }

    @Override
    public List<BibleVerse> getVerse(BibleVerseCoordinates cord) throws IOException, InterruptedException, IllegalArgumentException {
        log.info("Fetching English verse for coordinates: {}", cord.getDisplayString());
        
        String query = cord.getDisplayString();
        JSONObject response = getESVQueryResponse(query);
        return parseRawBibleVerseString(cord.book(), cord.chapter(), response.getString("passage"));
    }

    @Override
    public List<BibleVerse> getVerses(BibleVerseCoordinates startCord, BibleVerseCoordinates endCord) throws IOException, InterruptedException, IllegalArgumentException {
        log.info("Fetching English verses from {} to {}", startCord.getDisplayString(), endCord.getDisplayString());

        if (startCord.book() != endCord.book()) {
            throw new IllegalArgumentException("Start and end coordinates must be in the same book.");
        }

        List<BibleVerse> verses = new ArrayList<>();
        for (int chapter = startCord.chapter(); chapter <= endCord.chapter(); chapter++) {
            String verseStart = (chapter == startCord.chapter()) ? String.valueOf(startCord.verseNumber()) : "1";
            String verseEnd = (chapter == endCord.chapter()) ? String.valueOf(endCord.verseNumber()) : Integer.MAX_VALUE + "";

            String query = String.format("%s %d:%s-%s", startCord.book().getDisplayString(), chapter, verseStart, verseEnd);
            log.info("Querying ESV API with: {}", query);

            JSONObject response = getESVQueryResponse(query);
            List<BibleVerse> chapterVerses = parseRawBibleVerseString(startCord.book(), chapter, response.getString("passage"));

            for (BibleVerse verse : chapterVerses) {
                int verseNum = verse.coordinates().verseNumber();
                if ((chapter == startCord.chapter() && verseNum < startCord.verseNumber()) ||
                    (chapter == endCord.chapter() && verseNum > endCord.verseNumber())) {
                    log.warn("ESV API returned out-of-bounds verse: {}", verse.coordinates().getDisplayString());
                    continue;
                }
                verses.add(verse);
            }
        }

        return verses;
    }

    @Override
    public Language getLanguage() {
        return Language.ENGLISH;
    }

    // --------------------------------------------------------------------

    private JSONObject getESVQueryResponse(String query) throws IOException, InterruptedException {
        Map<String, String> params = Map.of(
            "q", query,
            "include-headings", "false",
            "include-footnotes", "false",
            "include-verse-numbers", "true",
            "include-short-copyright", "false",
            "include-passage-references", "false"
        );

        String queryString = params.entrySet().stream()
            .map(entry -> URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8) + "=" +
                          URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8))
            .collect(Collectors.joining("&"));

        String fullUrl = ESV_API_URL + "?" + queryString;
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(fullUrl))
            .header("Authorization", "Token " + ESV_API_KEY)
            .GET()
            .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            try {
                return new JSONObject(response.body());
            } catch (JSONException e) {
                throw new IOException("Failed to parse JSON response from ESV API", e);
            }
        }
        throw new IOException("Failed to fetch verse from ESV API: " + response.statusCode());
    }

    private List<BibleVerse> parseRawBibleVerseString(BibleBook book, int Chapter, String text) {
        // Parses verses from raw ESV text format: "[1]Verse one text[2]Verse two text"
        // Assumes all verses belong to a specified book and chapter

        List<BibleVerse> verses = new ArrayList<>();

        String[] rawVerses = text.trim().split("(?=\\[\\d+\\])");
        for (String verse : rawVerses) {
            int verseNumberEnd = verse.indexOf(']');
            if (verseNumberEnd == -1) continue;

            int verseNumber = Integer.parseInt(verse.substring(1, verseNumberEnd));
            String verseText = verse.substring(verseNumberEnd + 1).trim();

            BibleVerseCoordinates coordinates = new BibleVerseCoordinates(book, Chapter, verseNumber);
            verses.add(new BibleVerse(coordinates, verseText));
        }

        return verses;
    }
}
