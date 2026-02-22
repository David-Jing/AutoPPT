package org.fcnabc.autoppt.io;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileStoreTest {

    @TempDir
    Path tempDir;

    private FileStore fileStore;
    private Path fileDirectory;

    @BeforeEach
    void setUp() throws IOException {
        // Create a sub-directory in the temp dir to act as the file root
        fileDirectory = tempDir.resolve("files");
        // Initialize the FileStore with the temporary path
        fileStore = new FileStore(fileDirectory);
    }

    @Test
    void testConstructorCreatesDirectory() {
        assertTrue(Files.exists(fileDirectory), "File directory should be created by constructor");
        assertTrue(Files.isDirectory(fileDirectory), "Start path should be a directory");
    }

    @Test
    void testSetAndGetCacheFile() throws IOException {
        String fileName = "testFile.txt";
        String content = "Hello, World!";

        fileStore.setFile(fileName, content);

        assertTrue(fileStore.fileExists(fileName), "File should exist after setting");
        
        String retrievedContent = fileStore.getFileContent(fileName);
        assertEquals(content, retrievedContent, "Retrieved content should match saved content");
    }

    @Test
    void testSetCacheFileOverwritesExisting() throws IOException {
        String fileName = "overwrite.txt";
        fileStore.setFile(fileName, "Initial Content");
        
        String newContent = "New Content";
        fileStore.setFile(fileName, newContent);

        String retrieved = fileStore.getFileContent(fileName);
        assertEquals(newContent, retrieved, "Content should be overwritten");
    }

    @Test
    void testCacheFileExists() throws IOException {
        String fileName = "exists.txt";
        assertFalse(fileStore.fileExists(fileName), "File should not exist initially");

        fileStore.setFile(fileName, "content");
        assertTrue(fileStore.fileExists(fileName), "File should exist after creation");
    }

    @Test
    void testGetCacheFileThrowsExceptionWhenNotFound() throws IOException {
        String fileName = "nonExistent.txt";
        try {
            fileStore.getFileContent(fileName);
            fail("Expected RuntimeException was not thrown");
        } catch (IOException e) {
            assertTrue(e.getMessage().contains("File not found"), "Exception message should indicate file not found");
        }
    }

    @Test
    void testListCacheFiles() throws IOException {
        // Create a few files
        fileStore.setFile("file1.txt", "content1");
        fileStore.setFile("file2.log", "content2");
        
        // Create a subdirectory - should strictly not be included based on listCacheFiles impl (Files.list is shallow, but filter checks isRegularFile)
        Files.createDirectories(fileDirectory.resolve("subdir"));

        List<String> files = fileStore.listFiles();

        assertEquals(2, files.size(), "Should list exactly 2 files");
        assertTrue(files.contains("file1.txt"));
        assertTrue(files.contains("file2.log"));
    }

    @Test
    void testGetCacheFile() throws IOException {
        String fileName = "fileObjectTest.txt";
        String content = "File Object Content";
        fileStore.setFile(fileName, content);

        java.io.File file = fileStore.getFile(fileName);
        assertNotNull(file, "File object should not be null");
        assertTrue(file.exists(), "File should exist on disk");
        assertTrue(file.isFile(), "Should be a file");
        assertEquals(fileName, file.getName(), "File name should match");
    }

    @Test
    void testGetCacheFileThrowsWhenNotFound() {
        String fileName = "nonExistentFileObj.txt";
        try {
            fileStore.getFile(fileName);
            fail("Expected IOException was not thrown for getCacheFile");
        } catch (IOException e) {
            assertTrue(e.getMessage().contains("File not found"), "Exception message should indicate file not found");
        }
    }
}
