package org.fcnabc.autoppt.google.models;

public enum ParagraphAlignment {
    START("START"),
    END("END"),
    CENTER("CENTER"),
    JUSTIFIED("JUSTIFIED");

    private final String alignment;

    ParagraphAlignment(String alignment) {
        this.alignment = alignment;
    }

    public String getAlignment() {
        return alignment;
    }
}
