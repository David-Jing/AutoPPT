package org.fcnabc.autoppt.hymns;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

import org.fcnabc.autoppt.io.FileStore;

public class HymnModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(HymnStore.class).in(Singleton.class);
        bind(FileStore.class).in(Singleton.class);
    }
}
