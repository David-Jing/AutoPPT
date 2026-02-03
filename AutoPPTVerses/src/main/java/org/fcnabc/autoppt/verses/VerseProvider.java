package org.fcnabc.autoppt.verses;

import java.io.IOException;
import java.util.List;

import org.fcnabc.autoppt.verses.models.BibleVerse;
import org.fcnabc.autoppt.verses.models.BibleVerseCoordinates;
import org.fcnabc.autoppt.language.Language;

public interface VerseProvider {
    /**
     * Fetches a verse based on coordinates.
     * Returns Optional in case the verse doesn't exist.
     */
    List<BibleVerse> getVerse(BibleVerseCoordinates cord) throws IOException, InterruptedException, IllegalArgumentException;

    /**
     * Fetches a list of verses between the given start and end coordinates (inclusive).
     */
    List<BibleVerse> getVerses(BibleVerseCoordinates startCord, BibleVerseCoordinates endCord) throws IOException, InterruptedException, IllegalArgumentException;
    
    // Identifies the language of this provider
    Language getLanguage();
}
