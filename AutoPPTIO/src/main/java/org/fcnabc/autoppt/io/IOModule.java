package org.fcnabc.autoppt.io;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import org.fcnabc.autoppt.io.model.AppConfig;

import java.nio.file.Path;

public class IOModule extends AbstractModule {
    private static final String FOLDER_NAME = "AutoPPT";

    @Provides
    @Singleton
    @Named("AppDirectory")
    Path provideAppDirectory() {
        return Path.of(System.getProperty("user.home"), "Documents", FOLDER_NAME);
    }

    @Provides
    @Singleton
    AppConfig provideAppConfig(AppConfigStore appConfigStore) {
        return appConfigStore.getAppConfig();
    }
}
