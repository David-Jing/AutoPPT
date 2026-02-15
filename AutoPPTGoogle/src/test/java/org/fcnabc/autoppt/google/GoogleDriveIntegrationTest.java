package org.fcnabc.autoppt.google;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import lombok.extern.slf4j.Slf4j;
import com.google.api.client.util.DateTime;
import com.google.inject.Guice;
import com.google.inject.Injector;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.*;

import org.fcnabc.autoppt.io.IOModule;
import org.fcnabc.autoppt.google.models.DriveMimeType;

/**
 * Integration test for GoogleDrive wrapper.
 * This test runs against the real Google Drive API and requires:
 * 1. google-credentials.json to be present.
 * 2. An active internet connection.
 * 3. User intervention to authorize the app in the browser (first run only).
 */
@Slf4j
@Tag("integration")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GoogleDriveIntegrationTest {
    // Disable/enable this integration tests
    private static final Boolean TESTS_ENABLED = true;

    private static String uploadedFileId;
    private static String duplicatedFileId;

    private static GoogleDrive googleDrive;

    @BeforeAll
    public static void setup() throws IOException {
        Assumptions.assumeTrue(TESTS_ENABLED, "Google Drive Integration Tests are disabled. Set TESTS_ENABLED to true to enable them.");

        log.info("Setting up Google Drive Integration Test...");
        
        try {
            Injector injector = Guice.createInjector(new GoogleModule(), new IOModule());
            googleDrive = injector.getInstance(GoogleDrive.class);
        } catch (Exception e) {
            log.error("Failed to initialize Google Auth. Ensure google-credentials.json is present.", e);
            throw new RuntimeException(e);
        }
    }

    @Test
    @Order(1)
    public void testUploadFile(@TempDir Path tempDir) throws IOException {
        String fileName = "autoppt_integration_test_" + System.currentTimeMillis() + ".txt";
        // Explicit content
        String content = "Initial Content";
        Path tempFile = tempDir.resolve(fileName);
        Files.writeString(tempFile, content);
        
        log.info("Uploading file: {}", fileName);
        uploadedFileId = googleDrive.uploadFile(fileName, DriveMimeType.PLAIN_TEXT, tempFile.toFile());
        
        assertNotNull(uploadedFileId, "Uploaded file ID should not be null");
        log.info("Uploaded File ID: {}", uploadedFileId);
    }

    @Test
    @Order(2)
    public void testGetFileMetadata() throws IOException {
        Assumptions.assumeTrue(uploadedFileId != null, "Skipping because upload failed");
        
        DateTime modifiedTime = googleDrive.getFileLastModifiedTime(uploadedFileId);
        assertNotNull(modifiedTime, "Modified time should be retrieved");
        
        // Validation: Mod time should be recent (within 1 minute)
        long now = System.currentTimeMillis();
        long diff = Math.abs(now - modifiedTime.getValue());
        assertTrue(diff < 60000, "File modification time should be within last 1 minutes");

        log.info("File Modified Time: {}", modifiedTime);
    }

    @Test
    @Order(3)
    public void testUpdateFileContent(@TempDir Path tempDir) throws IOException, InterruptedException {
        Assumptions.assumeTrue(uploadedFileId != null, "Skipping because upload failed");

        DateTime oldModifiedTime = googleDrive.getFileLastModifiedTime(uploadedFileId);
        log.info("Old Modified Time: {}", oldModifiedTime);

        // Ensure time triggers a change
        Thread.sleep(2000);

        String newContent = "Updated Content " + System.currentTimeMillis();
        Path tempFile = tempDir.resolve("updated.txt");
        Files.writeString(tempFile, newContent);

        log.info("Updating file content...");
        googleDrive.updateFile(uploadedFileId, DriveMimeType.PLAIN_TEXT, tempFile.toFile());

        // Validate Modification Time
        DateTime newModifiedTime = googleDrive.getFileLastModifiedTime(uploadedFileId);
        log.info("New Modified Time: {}", newModifiedTime);
        assertTrue(newModifiedTime.getValue() > oldModifiedTime.getValue(), 
            "Modified time should have increased after update");

        // Validate File Contents
        Path downloadDir = tempDir.resolve("verify");
        Files.createDirectories(downloadDir);
        Path downloaded = googleDrive.downloadFile(uploadedFileId, downloadDir);
        String downloadedContent = Files.readString(downloaded);
        
        assertEquals(newContent, downloadedContent, "Downloaded content should match the updated content");
    }

    @Test
    @Order(4)
    public void testFileExists() {
        Assumptions.assumeTrue(uploadedFileId != null, "Skipping because upload failed");

        // Check positive case
        boolean exists = googleDrive.fileExists(uploadedFileId);
        assertTrue(exists, "Uploaded file should exist");

        // Check negative case
        boolean notExists = googleDrive.fileExists("invalid-file-id-should-not-exist-" + System.currentTimeMillis());
        assertFalse(notExists, "Non-existent file ID should return false");
        
        log.info("File existence check passed for ID: {}", uploadedFileId);
    }

    @Test
    @Order(5)
    public void testDownloadFileToString() throws IOException {
        Assumptions.assumeTrue(uploadedFileId != null, "Skipping because upload failed");

        log.info("Downloading file to string: {}", uploadedFileId);
        String content = googleDrive.downloadFile(uploadedFileId);
        assertNotNull(content, "Downloaded content should not be null");
        assertTrue(content.contains("Updated Content"), "Downloaded content should contain the updated text");
        log.info("Downloaded content length: {}", content.length());
    }

    @Test
    @Order(6)
    public void testDuplicateFile() throws IOException {
        Assumptions.assumeTrue(uploadedFileId != null, "Skipping because upload failed");

        String fileName = "autoppt_integration_test_copy_" + System.currentTimeMillis() + ".txt";
        duplicatedFileId = googleDrive.duplicateFile(uploadedFileId, fileName);
        
        assertNotNull(duplicatedFileId, "Duplicated file ID should not be null");
        log.info("Duplicated File ID: {}", duplicatedFileId);
    }

    @Test
    @Order(7)
    public void testDownloadFile(@TempDir Path tempDir) throws IOException {
        Assumptions.assumeTrue(uploadedFileId != null, "Skipping because upload failed");

        log.info("Downloading file: {}", uploadedFileId);
        Path downloaded = googleDrive.downloadFile(uploadedFileId, tempDir);
        
        assertTrue(Files.exists(downloaded), "Downloaded file should exist");
        assertTrue(Files.size(downloaded) > 0, "Downloaded file should not be empty");
        log.info("Downloaded to: {}", downloaded);
    }

    @Test
    @Order(8)
    public void testCleanup() {
        if (uploadedFileId != null) {
            try {
                googleDrive.deleteFile(uploadedFileId);
                log.info("Deleted uploaded file: {}", uploadedFileId);
            } catch (IOException e) {
                log.error("Failed to delete uploaded file: {}", e.getMessage());
            }
        }
        if (duplicatedFileId != null) {
            try {
                googleDrive.deleteFile(duplicatedFileId);
                log.info("Deleted duplicated file: {}", duplicatedFileId);
            } catch (IOException e) {
                log.error("Failed to delete duplicated file: {}", e.getMessage());
            }
        }
    }
}
