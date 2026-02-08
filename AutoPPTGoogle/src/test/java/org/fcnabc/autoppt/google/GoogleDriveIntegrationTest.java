package org.fcnabc.autoppt.google;

import com.google.api.client.util.DateTime;
import com.google.inject.Guice;
import com.google.inject.Injector;

import lombok.extern.slf4j.Slf4j;

import org.fcnabc.autoppt.google.models.DriveMimeType;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test for GoogleDrive wrapper.
 * This test runs against the real Google Drive API and requires:
 * 1. src/main/resources/credentials.json to be present.
 * 2. An active internet connection.
 * 3. User intervention to authorize the app in the browser (first run only).
 */
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GoogleDriveIntegrationTest {
    private static String uploadedFileId;
    private static String duplicatedFileId;

    private static GoogleDrive googleDrive;

    @BeforeAll
    public static void setup() throws IOException {
        log.info("Setting up Google Drive Integration Test...");
        
        try {
            Injector injector = Guice.createInjector(new GoogleModule());
            googleDrive = injector.getInstance(GoogleDrive.class);
        } catch (Exception e) {
            log.error("Failed to initialize Google Auth. Ensure credentials.json is present.", e);
            throw new RuntimeException(e);
        }
    }

    @Test
    @Order(1)
    public void testUploadFile(@TempDir Path tempDir) throws IOException {
        String fileName = "autoppt_integration_test_" + System.currentTimeMillis() + ".txt";
        Path tempFile = tempDir.resolve(fileName);
        Files.writeString(tempFile, "This is a test content for Google Drive integration.\nCreated at: " + System.currentTimeMillis());
        File fileToUpload = tempFile.toFile();

        log.info("Uploading file: {}", fileName);
        uploadedFileId = googleDrive.uploadFile(fileName, DriveMimeType.PLAIN_TEXT, fileToUpload);
        
        assertNotNull(uploadedFileId, "Uploaded file ID should not be null");
        log.info("Uploaded File ID: {}", uploadedFileId);
    }

    @Test
    @Order(2)
    public void testGetFileMetadata() throws IOException {
        Assumptions.assumeTrue(uploadedFileId != null, "Skipping because upload failed");
        
        DateTime modifiedTime = googleDrive.getFileLastModifiedTime(uploadedFileId);
        assertNotNull(modifiedTime, "Modified time should be retrieved");
        log.info("File Modified Time: {}", modifiedTime);
    }

    @Test
    @Order(3)
    public void testDuplicateFile() throws IOException {
        Assumptions.assumeTrue(uploadedFileId != null, "Skipping because upload failed");

        String fileName = "autoppt_integration_test_copy_" + System.currentTimeMillis() + ".txt";
        duplicatedFileId = googleDrive.duplicateFile(uploadedFileId, fileName);
        
        assertNotNull(duplicatedFileId, "Duplicated file ID should not be null");
        log.info("Duplicated File ID: {}", duplicatedFileId);
    }

    @Test
    @Order(4)
    public void testDownloadFile(@TempDir Path tempDir) throws IOException {
        Assumptions.assumeTrue(uploadedFileId != null, "Skipping because upload failed");

        log.info("Downloading file: {}", uploadedFileId);
        Path downloaded = googleDrive.downloadFile(uploadedFileId, tempDir);
        
        assertTrue(Files.exists(downloaded), "Downloaded file should exist");
        assertTrue(Files.size(downloaded) > 0, "Downloaded file should not be empty");
        log.info("Downloaded to: {}", downloaded);
    }

    @Test
    @Order(5)
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
