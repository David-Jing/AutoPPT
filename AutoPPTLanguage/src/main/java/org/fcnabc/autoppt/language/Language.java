package org.fcnabc.autoppt.language;

public enum Language {
    ENGLISH("English");

    private final String displayString;

    Language(String displayString) {
        this.displayString = displayString;
    }

    public String getDisplayString() {
        return displayString;
    }
}
