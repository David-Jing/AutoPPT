package org.fcnabc.autoppt.hymns;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import org.fcnabc.autoppt.io.FileStore;
import org.fcnabc.autoppt.io.AppConfigStore;

public class HymnModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(HymnStore.class).in(Singleton.class);
        bind(FileStore.class).in(Singleton.class);
    }

    @Provides
    @Named("HYMN_TIMESTAMP_FILE_ID")
    @Singleton
    String provideHymnTimestampFileID(AppConfigStore appConfigStore) {
        return appConfigStore.getAppConfig().HymnStoreGoogleFileId();
    }
}
