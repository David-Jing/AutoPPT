package org.fcnabc.autoppt.hymns.models;

import java.util.List;

public record Hymn(
    String title,
    List<String> verses
) {
    public int verseCount() {
        return verses.size();
    }
}
