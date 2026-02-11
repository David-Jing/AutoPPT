package org.fcnabc.autoppt.hymns;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

public class HymnModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(HymnProvider.class).in(Singleton.class);
    }
}
