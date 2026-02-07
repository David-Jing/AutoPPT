package org.fcnabc.autoppt.google.models;

public enum ParagraphAlignment {
    START("START"),
    END("END"),
    CENTER("CENTER"),
    JUSTIFIED("JUSTIFIED");

    private final String displayString;

    ParagraphAlignment(String displayString) {
        this.displayString = displayString;
    }

    public String getDisplayString() {
        return displayString;
    }
}
