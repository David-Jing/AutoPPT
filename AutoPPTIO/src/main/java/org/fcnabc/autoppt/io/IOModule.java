package org.fcnabc.autoppt.io;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import java.nio.file.Path;

public class IOModule extends AbstractModule {
    private static final String FOLDER_NAME = "AutoPPT";
    private static final String KEY_FOLDER_NAME = "keys";
    private static final String CACHE_FOLDER_NAME = "cache";

    @Provides
    @Singleton
    @Named("KeyDirectory")
    Path provideKeyDirectory() {
        return Path.of(System.getProperty("user.home"), "Documents", FOLDER_NAME, KEY_FOLDER_NAME);
    }

    @Provides
    @Singleton
    @Named("CacheDirectory")
    Path provideCacheDirectory() {
        return Path.of(System.getProperty("user.home"), "Documents", FOLDER_NAME, CACHE_FOLDER_NAME);
    }
}
