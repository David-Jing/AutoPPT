package org.fcnabc.autoppt.io.model;

import com.google.gson.Gson;

public record AppConfig(
    String GoogleCredentialKey,
    String ESVApiKey,
    String HymnStoreGoogleFileId
) {
    private static final Gson GSON = new Gson();
    
    @SuppressWarnings("null")
    public static AppConfig fromJson(String json) throws RuntimeException {
        try {
            AppConfig config = GSON.fromJson(json, AppConfig.class);
            if (!isValid(config)) {
                throw new IllegalArgumentException("Missing required app configuration values");
            }
            return config;
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse app config JSON", e);
        }
    }

    public String toJson() {
        return GSON.toJson(this);
    }

    private static boolean isValid(AppConfig config) {
        return config.GoogleCredentialKey != null &&
               config.ESVApiKey != null &&
               config.HymnStoreGoogleFileId != null;
    }
}