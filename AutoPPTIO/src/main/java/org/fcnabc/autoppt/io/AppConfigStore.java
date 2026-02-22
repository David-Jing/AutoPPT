package org.fcnabc.autoppt.io;

import java.nio.file.Path;
import java.io.IOException;
import java.nio.file.Files;
import lombok.extern.slf4j.Slf4j;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import org.fcnabc.autoppt.io.model.AppConfig;

@Slf4j
@Singleton
public class AppConfigStore {
    private final String APP_CONFIG_FILE = "AppConfig.json";
    private final Path APP_DIRECTORY;

    private AppConfig appConfig;

    @Inject
    public AppConfigStore(@Named("AppDirectory") Path appDirectory) throws RuntimeException {
        this.APP_DIRECTORY = appDirectory;
        if (!APP_DIRECTORY.toFile().exists() && !APP_DIRECTORY.toFile().mkdirs()) {
            throw new RuntimeException("Failed to create directory for app config: " + APP_DIRECTORY);
        }

        Path appConfigPath = APP_DIRECTORY.resolve(APP_CONFIG_FILE);
        if (!Files.exists(appConfigPath)) {
            log.warn("App config file not found.");
        } else {
            try {
                String json = Files.readString(appConfigPath);
                setAppConfig(json);
            } catch (IOException e) {
                throw new RuntimeException("Failed to read app config file: " + appConfigPath, e);
            }
        }
    }
    
    public void setAppConfig(String json) throws RuntimeException {
        appConfig = AppConfig.fromJson(json);
    }

    public void saveAppConfigToFile() throws IOException, RuntimeException {
        validateAppConfig();
        Path appConfigPath = APP_DIRECTORY.resolve(APP_CONFIG_FILE);
        Files.writeString(appConfigPath, appConfig.toJson());
    }

    public AppConfig getAppConfig() throws RuntimeException {
        validateAppConfig();
        return appConfig;
    }

    private void validateAppConfig() throws RuntimeException {
        if (appConfig == null) {
            throw new RuntimeException("App configuration not found. Please set the app configuration first.");
        }
    }
}
