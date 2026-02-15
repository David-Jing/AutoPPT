package org.fcnabc.autoppt.hymns.models;

import com.google.api.client.util.DateTime;

public record HymnMetadata(
    String hymnName,
    DateTime lastUpdated,
    String fileName,
    String fileId
) {
}
