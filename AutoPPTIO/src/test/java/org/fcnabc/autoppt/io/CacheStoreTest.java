package org.fcnabc.autoppt.io;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CacheStoreTest {

    @TempDir
    Path tempDir;

    private CacheStore cacheStore;
    private Path cacheDirectory;

    @BeforeEach
    void setUp() throws IOException {
        // Create a sub-directory in the temp dir to act as the cache root
        cacheDirectory = tempDir.resolve("cache");
        // Initialize the CacheStore with the temporary path
        cacheStore = new CacheStore(cacheDirectory);
    }

    @Test
    void testConstructorCreatesDirectory() {
        assertTrue(Files.exists(cacheDirectory), "Cache directory should be created by constructor");
        assertTrue(Files.isDirectory(cacheDirectory), "Start path should be a directory");
    }

    @Test
    void testSetAndGetCacheFile() throws IOException {
        String fileName = "testFile.txt";
        String content = "Hello, World!";

        cacheStore.setCache(fileName, content);

        assertTrue(cacheStore.cacheExists(fileName), "File should exist after setting");
        
        String retrievedContent = cacheStore.getCache(fileName);
        assertEquals(content, retrievedContent, "Retrieved content should match saved content");
    }

    @Test
    void testSetCacheFileOverwritesExisting() throws IOException {
        String fileName = "overwrite.txt";
        cacheStore.setCache(fileName, "Initial Content");
        
        String newContent = "New Content";
        cacheStore.setCache(fileName, newContent);

        String retrieved = cacheStore.getCache(fileName);
        assertEquals(newContent, retrieved, "Content should be overwritten");
    }

    @Test
    void testCacheFileExists() throws IOException {
        String fileName = "exists.txt";
        assertFalse(cacheStore.cacheExists(fileName), "File should not exist initially");

        cacheStore.setCache(fileName, "content");
        assertTrue(cacheStore.cacheExists(fileName), "File should exist after creation");
    }

    @Test
    void testGetCacheFileThrowsExceptionWhenNotFound() throws IOException {
        String fileName = "nonExistent.txt";
        try {
            cacheStore.getCache(fileName);
            fail("Expected RuntimeException was not thrown");
        } catch (IOException e) {
            assertTrue(e.getMessage().contains("Cache file not found"), "Exception message should indicate file not found");
        }
    }

    @Test
    void testListCacheFiles() throws IOException {
        // Create a few files
        cacheStore.setCache("file1.txt", "content1");
        cacheStore.setCache("file2.log", "content2");
        
        // Create a subdirectory - should strictly not be included based on listCacheFiles impl (Files.list is shallow, but filter checks isRegularFile)
        Files.createDirectories(cacheDirectory.resolve("subdir"));

        List<String> files = cacheStore.listCache();

        assertEquals(2, files.size(), "Should list exactly 2 files");
        assertTrue(files.contains("file1.txt"));
        assertTrue(files.contains("file2.log"));
    }

    @Test
    void testGetCacheFile() throws IOException {
        String fileName = "fileObjectTest.txt";
        String content = "File Object Content";
        cacheStore.setCache(fileName, content);

        java.io.File file = cacheStore.getCacheFile(fileName);
        assertNotNull(file, "File object should not be null");
        assertTrue(file.exists(), "File should exist on disk");
        assertTrue(file.isFile(), "Should be a file");
        assertEquals(fileName, file.getName(), "File name should match");
    }

    @Test
    void testGetCacheFileThrowsWhenNotFound() {
        String fileName = "nonExistentFileObj.txt";
        try {
            cacheStore.getCacheFile(fileName);
            fail("Expected IOException was not thrown for getCacheFile");
        } catch (IOException e) {
            assertTrue(e.getMessage().contains("Cache file not found"), "Exception message should indicate file not found");
        }
    }
}
