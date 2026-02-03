package org.fcnabc.autoppt.verses.models;

public record BibleVerse(
    BibleVerseCoordinates coordinates,
    String text
) {
    public String getDisplayString() {
        return "%s %s".formatted(
            coordinates.getDisplayString(),
            text.length() > 30 ? text.substring(0, 27) + "..." : text
        );
    }
}
