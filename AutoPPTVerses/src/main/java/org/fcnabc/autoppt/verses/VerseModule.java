package org.fcnabc.autoppt.verses;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.name.Names;
import java.net.http.HttpClient;

import org.fcnabc.autoppt.language.Language;
import org.fcnabc.autoppt.verses.providers.EnglishVerseProvider;

public class VerseModule extends AbstractModule {
    
    @Override 
    protected void configure() {
        String esvApiKey = System.getenv("ESV_API_KEY");
        if (esvApiKey == null || esvApiKey.isBlank()) {
            throw new IllegalStateException("CRITICAL: ESV_API_KEY environment variable is missing.");
        }
        bindConstant().annotatedWith(Names.named("ESV_API_KEY")).to(esvApiKey);
        
        @SuppressWarnings("null")
        MapBinder<Language, VerseProvider> providerBinder = MapBinder.newMapBinder(binder(), Language.class, VerseProvider.class);
        providerBinder.addBinding(Language.ENGLISH).to(EnglishVerseProvider.class);
    }

    @Provides 
    @Singleton
    HttpClient provideHttpClient() {
        return HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
    }
}