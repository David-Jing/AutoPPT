package org.fcnabc.autoppt.io;

import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.io.IOException;
import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

@Singleton
public class CacheStore {
    private final Path CACHE_DIRECTORY;

    @Inject
    public CacheStore(@Named("CacheDirectory") Path cacheDirectory) throws IOException {
        this.CACHE_DIRECTORY = cacheDirectory;
        if (!CACHE_DIRECTORY.toFile().exists() && !CACHE_DIRECTORY.toFile().mkdirs()) {
            throw new IOException("Failed to create directory for cache: " + CACHE_DIRECTORY);
        }
    }

    public Path getCacheDirectory() {
        return CACHE_DIRECTORY;
    }

    public List<String> listCache() throws IOException {
        try (Stream<Path> paths = Files.list(CACHE_DIRECTORY)) {
            return paths.filter(Files::isRegularFile)
                        .map(Path::getFileName)
                        .map(Path::toString)
                        .collect(Collectors.toList());
        } catch (IOException e) {
            throw new IOException("Failed to list cache files in directory: " + CACHE_DIRECTORY, e);
        }
    }

    public void setCache(String fileName, String content) throws IOException {
        Path filePath = CACHE_DIRECTORY.resolve(fileName);
        try {
            Files.writeString(filePath, content, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new IOException("Failed to write cache file: " + filePath, e);
        }
    }

    public boolean cacheExists(String fileName) {
        Path filePath = CACHE_DIRECTORY.resolve(fileName);
        return Files.exists(filePath);
    }

    public String getCache(String fileName) throws IOException {
        Path filePath = CACHE_DIRECTORY.resolve(fileName);
        if (!cacheExists(fileName)) {
            throw new IOException("Cache file not found: " + filePath);
        }
        try {
            return Files.readString(filePath).trim();
        } catch (IOException e) {
            throw new IOException("Failed to read cache file: " + filePath, e);
        }
    }

    public File getCacheFile(String fileName) throws IOException {
        Path filePath = CACHE_DIRECTORY.resolve(fileName);
        if (!cacheExists(fileName)) {
            throw new IOException("Cache file not found: " + filePath);
        }
        return filePath.toFile();
    }
}
