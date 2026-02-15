package org.fcnabc.autoppt.hymns;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import org.fcnabc.autoppt.io.CacheStore;
import org.fcnabc.autoppt.io.KeyStore;
import org.fcnabc.autoppt.io.model.Key;

public class HymnModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(HymnStore.class).in(Singleton.class);
        bind(CacheStore.class).in(Singleton.class);
    }

    @Provides
    @Named("HYMN_TIMESTAMP_FILE_ID")
    @Singleton
    String provideHymnTimestampFileID(KeyStore getKeys) {
        return getKeys.getKey(Key.HYMN_TIMESTAMP_FILE_ID);
    }
}
