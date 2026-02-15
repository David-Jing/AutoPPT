package org.fcnabc.autoppt.verses;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.name.Named;
import java.net.http.HttpClient;

import org.fcnabc.autoppt.io.KeyStore;
import org.fcnabc.autoppt.io.model.Key;
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
    @Named("ESV_API_KEY")
    @Singleton
    String provideEsvApiKey(KeyStore getKeys) {
        return getKeys.getKey(Key.ESV_API_KEY);
    }

    @Provides 
    @Singleton
    HttpClient provideHttpClient() {
        return HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
    }
}