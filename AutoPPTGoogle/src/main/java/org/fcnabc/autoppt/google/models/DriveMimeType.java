package org.fcnabc.autoppt.google.models;

public enum DriveMimeType {
    PLAIN_TEXT("text/plain"),
    JSON("application/json"),
    CSV("text/csv"),
    HTML("text/html"),
    PDF("application/pdf"),
    JPEG("image/jpeg"),
    PNG("image/png"),
    VIDEO_MP4("video/mp4"),
    AUDIO_MP3("audio/mpeg");

    private final String mimeType;

    DriveMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getMimeType() {
        return mimeType;
    }
}
