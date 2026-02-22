package org.fcnabc.autoppt.verses;

import java.io.IOException;
import java.util.List;

import org.fcnabc.autoppt.verses.models.BibleVerse;
import org.fcnabc.autoppt.verses.models.BibleVerseCoordinates;
import org.fcnabc.autoppt.language.Language;

public interface VerseProvider {
    List<BibleVerse> getVerse(BibleVerseCoordinates cord) throws IOException, InterruptedException, IllegalArgumentException;

    List<BibleVerse> getVerses(BibleVerseCoordinates startCord, BibleVerseCoordinates endCord) throws IOException, InterruptedException, IllegalArgumentException;
    
    Language getLanguage();
}
