package org.fcnabc.autoppt.hymns;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.nio.file.Path;
import java.io.IOException;
import java.lang.reflect.Type;
import lombok.extern.slf4j.Slf4j;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.google.api.client.util.DateTime;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import org.fcnabc.autoppt.google.GoogleDrive;
import org.fcnabc.autoppt.google.models.DriveMimeType;
import org.fcnabc.autoppt.io.CacheStore;
import org.fcnabc.autoppt.hymns.models.HymnMetadata;

@Slf4j
@Singleton
public class HymnStore {
    private static final String HYMN_CACHE_TIMESTAMPS_FILE = "hymnCacheTimestamps.json";

    private CacheStore cacheStore;
    private GoogleDrive googleDrive;
    private String hymnTimestampFileID;
    private Map<String, HymnMetadata> hymnCacheTimestampsCloud;
    private Map<String, HymnMetadata> hymnCacheTimestampsLocal;

    @Inject
    public HymnStore(CacheStore cacheStore, GoogleDrive googleDrive, @Named("HYMN_TIMESTAMP_FILE_ID") String hymnTimestampFileID) throws IOException {
        this.cacheStore = cacheStore;
        this.googleDrive = googleDrive;
        this.hymnTimestampFileID = hymnTimestampFileID;

        refreshCacheTimestamps();
        syncCacheTimestamps();
    }

    public Set<String> getAvailableHymns() {
        return hymnCacheTimestampsLocal.keySet();
    }

    public String getHymn(String hymnName) throws IOException {
        HymnMetadata cache = hymnCacheTimestampsLocal.get(hymnName);
        if (cache == null) {
            throw new IOException("Hymn not found in local cache: " + hymnName);
        }
        return cacheStore.getCache(cache.fileName());
    }

    public void setHymn(String hymnName, DateTime timestamp, String content) throws IOException {
        if (hymnCacheTimestampsLocal.containsKey(hymnName)) {
            HymnMetadata metadata = hymnCacheTimestampsLocal.get(hymnName);
            if (timestamp.getValue() <= metadata.lastUpdated().getValue()) {
                log.warn("Attempted to set hymn '{}' with an older or equal timestamp. Operation ignored.", hymnName);
                return;
            }
            log.info("Updating existing hymn '{}' with newer timestamp: {} -> {}", hymnName, metadata.lastUpdated(), timestamp);
            updateHymnCache(metadata, content);
        } else {
            createNewHymnCache(hymnName, timestamp, content);
        }
    }

    // --------------------------------------------------------------------

    private void updateHymnCache(HymnMetadata currMetadata, String content) throws IOException {
        // Update local timestamp and cache file
        HymnMetadata newMetadata = new HymnMetadata(currMetadata.hymnName(), currMetadata.lastUpdated(), currMetadata.fileName(), currMetadata.fileId());
        hymnCacheTimestampsLocal.put(currMetadata.hymnName(), newMetadata);
        cacheStore.setCache(currMetadata.fileName(), content);

        // Update Google Drive timestamp and content file
        try {
            googleDrive.updateFile(currMetadata.fileId(), DriveMimeType.PLAIN_TEXT, cacheStore.getCacheDirectory().resolve(currMetadata.fileName()).toFile());
        } catch (IOException e) {
            log.error("Failed to update hymn file on Google Drive for '{}': {}", currMetadata.hymnName(), e.getMessage());
            throw e;
        }
        try {
            googleDrive.updateFile(hymnTimestampFileID, DriveMimeType.PLAIN_TEXT, cacheStore.getCacheDirectory().resolve(HYMN_CACHE_TIMESTAMPS_FILE).toFile());
        } catch (IOException e) {
            log.error("Failed to update cloud hymn cache timestamps file: {}", e.getMessage());
            throw e;
        }
    }

    private void createNewHymnCache(String hymnName, DateTime timestamp, String content) throws IOException {
        String fileName = "hymn_" + hymnName.replaceAll("\\s+", "_").toLowerCase() + "_" + timestamp.getValue() + ".txt";
        Path localFilePath = cacheStore.getCacheDirectory().resolve(fileName);
        cacheStore.setCache(fileName, content);

        String fileId;
        try {
            fileId = googleDrive.uploadFile(fileName, DriveMimeType.PLAIN_TEXT, localFilePath.toFile());
        } catch (IOException e) {
            log.error("Failed to upload new hymn file to Google Drive for '{}': {}", hymnName, e.getMessage());
            throw e;
        }

        HymnMetadata newMetadata = new HymnMetadata(hymnName, timestamp, fileName, fileId);
        hymnCacheTimestampsLocal.put(hymnName, newMetadata);

        try {
            googleDrive.updateFile(hymnTimestampFileID, DriveMimeType.PLAIN_TEXT, cacheStore.getCacheDirectory().resolve(HYMN_CACHE_TIMESTAMPS_FILE).toFile());
        } catch (IOException e) {
            log.error("Failed to update cloud hymn cache timestamps file after creating new hymn '{}': {}", hymnName, e.getMessage());
            throw e;
        }
    }

    private Map<String, HymnMetadata> mapJsontoHymnCacheMap(String jsonString) {
        try {
            Gson gson = new Gson();
            Type type = new TypeToken<Map<String, HymnMetadata>>() {}.getType();
            return gson.fromJson(jsonString, type);
        } catch (JsonSyntaxException e) {
            log.error("Failed to parse JSON string into HymnCache map: {}", e.getMessage());
            return new HashMap<>();
        }
    }

    private void refreshCacheTimestamps() {
        if (!googleDrive.fileExists(hymnTimestampFileID)) {
            log.error("Hymn timestamp file not found in Google Drive with ID: {}", hymnTimestampFileID);
            hymnCacheTimestampsCloud = new HashMap<>();
        } else {
            try {
                String jsonStringLive = googleDrive.downloadFile(hymnTimestampFileID);
                hymnCacheTimestampsCloud = mapJsontoHymnCacheMap(jsonStringLive);
            } catch (IOException e) {
                log.error("Failed to download hymn timestamp file from Google Drive: {}", e.getMessage());
                hymnCacheTimestampsCloud = new HashMap<>();
            }
        }

        if (!cacheStore.cacheExists(HYMN_CACHE_TIMESTAMPS_FILE)) {
            log.warn("Local hymn cache timestamps file not found: {}", HYMN_CACHE_TIMESTAMPS_FILE);
            hymnCacheTimestampsLocal = new HashMap<>();
        } else {
            try {
                String jsonStringLocal = cacheStore.getCache(HYMN_CACHE_TIMESTAMPS_FILE);
                hymnCacheTimestampsLocal = mapJsontoHymnCacheMap(jsonStringLocal);
            } catch (IOException e) {
                log.error("Failed to read local hymn cache timestamps file: {}", e.getMessage());
                hymnCacheTimestampsLocal = new HashMap<>();
            }
        }
    }

    private void syncCacheTimestamps() {
        for (String hymnName : hymnCacheTimestampsCloud.keySet()) {
            HymnMetadata liveCache = hymnCacheTimestampsCloud.get(hymnName);
            HymnMetadata localCache = hymnCacheTimestampsLocal.get(hymnName);

            if (localCache == null || liveCache.lastUpdated().getValue() > localCache.lastUpdated().getValue()) {
                log.info("Updating local cache timestamp for {}: {} -> {}", hymnName, localCache, liveCache);

                Path localCachePath = cacheStore.getCacheDirectory().resolve(liveCache.fileName());
                try {
                    googleDrive.downloadFile(liveCache.fileId(), localCachePath);
                } catch (Exception e) {
                    log.error("Failed to download hymn file for {}: {}", hymnName, e.getMessage());
                    continue;
                }
                hymnCacheTimestampsLocal.put(hymnName, liveCache);
            }
        }
    }
}
