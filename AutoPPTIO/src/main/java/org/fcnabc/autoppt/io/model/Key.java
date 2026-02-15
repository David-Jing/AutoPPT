package org.fcnabc.autoppt.io.model;

public enum Key {
    GOOGLE_CREDENTIALS("google-credentials.json"),
    ESV_API_KEY("esv-api-key.txt"),
    HYMN_TIMESTAMP_FILE_ID("hymn-timestamp-file-id.txt");

    private final String keyFile;

    Key(String keyFile) {
        this.keyFile = keyFile;
    }

    public String getKeyFile() {
        return keyFile;
    }
}
