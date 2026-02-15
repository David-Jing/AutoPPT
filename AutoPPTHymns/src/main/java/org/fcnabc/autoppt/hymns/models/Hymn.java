package org.fcnabc.autoppt.hymns.models;

import java.util.List;
import com.google.api.client.util.DateTime;

public record Hymn(
    HymnMetadata metadata,
    List<String> verses
) {
    public int verseCount() {
        return verses.size();
    }

    public String hymnName() {
        return metadata.hymnName();
    }

    public DateTime lastUpdated() {
        return metadata.lastUpdated();
    }

    public String fileName() {
        return metadata.fileName();
    }

    public String fileId() {
        return metadata.fileId();
    }
}
