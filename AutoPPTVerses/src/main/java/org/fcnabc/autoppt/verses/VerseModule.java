package org.fcnabc.autoppt.verses;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.multibindings.MapBinder;
import java.net.http.HttpClient;

import org.fcnabc.autoppt.language.Language;
import org.fcnabc.autoppt.verses.providers.EnglishVerseProvider;

public class VerseModule extends AbstractModule {
    
    @Override 
    protected void configure() {
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
