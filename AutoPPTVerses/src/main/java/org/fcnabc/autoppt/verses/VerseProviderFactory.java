package org.fcnabc.autoppt.verses;

import org.fcnabc.autoppt.verses.providers.*;
import org.fcnabc.autoppt.language.Language;

public class VerseProviderFactory {
    public static VerseProvider getProvider(Language languageCode) {
        return switch (languageCode) {
            case ENGLISH -> new EnglishVerseProvider();
        };
    }
}
