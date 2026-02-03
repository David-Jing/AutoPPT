package org.fcnabc.autoppt.verses.models;

public record BibleVerseCoordinates(
    BibleBook book,
    int chapter,
    int verseNumber
) {
    public String getDisplayString() {
        return "%s %d:%d".formatted(
            book.getDisplayString(),
            chapter,
            verseNumber
        );
    }
}
