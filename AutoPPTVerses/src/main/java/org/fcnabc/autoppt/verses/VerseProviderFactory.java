package org.fcnabc.autoppt.verses;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.Map;

import org.fcnabc.autoppt.language.Language;

@Singleton
public class VerseProviderFactory {
    private final Map<Language, VerseProvider> providers;

    @Inject
    public VerseProviderFactory(Map<Language, VerseProvider> providers) {
        this.providers = providers;
    }

    public VerseProvider getProvider(Language languageCode) {
        VerseProvider provider = providers.get(languageCode);
        if (provider == null) {
            throw new IllegalArgumentException("No provider configured for: " + languageCode);
        }
        return provider;
    }
}
