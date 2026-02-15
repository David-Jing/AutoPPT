package org.fcnabc.autoppt.google;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import lombok.extern.slf4j.Slf4j;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.FileContent;
import com.google.api.client.util.DateTime;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.Drive.Files.Copy;
import com.google.api.services.drive.model.File;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import org.fcnabc.autoppt.google.models.DriveMimeType;

/**
 * A wrapper around the Google Drive API that provides methods for 
 * file operations such as duplication, deletion, uploading, and downloading.
 */
@Slf4j
@Singleton
public class GoogleDrive {
    private final Credential credential;
    private Drive service;

    @Inject
    public GoogleDrive(Credential credential, @Named("googleAppName") String applicationName) {
        log.info("Initializing Google Drive service...");
        this.credential = credential;
        this.service = new Drive.Builder(
                this.credential.getTransport(),
                this.credential.getJsonFactory(),
                this.credential)
                .setApplicationName(applicationName)
                .build();
    }

    public void deleteFile(String fileId) throws IOException {
        log.info("Deleting file with ID: {}", fileId);
        service.files().delete(fileId).execute();
    }

    public String duplicateFile(String fileId, String fileName) throws IOException {
        log.info("Duplicating file with ID: {} to new file name: {}", fileId, fileName);
        Copy copyRequest = service.files().copy(fileId, new File().setName(fileName));
        File copiedFile = copyRequest.execute();
        return copiedFile.getId();
    }

    public String uploadFile(String fileName, DriveMimeType mimeType, java.io.File fileContent) throws IOException {
        log.info("Uploading file: {}", fileName);
        File fileMetadata = new File();
        fileMetadata.setName(fileName);
        fileMetadata.setMimeType(mimeType.getMimeType());

        FileContent mediaContent = new FileContent(mimeType.getMimeType(), fileContent);
        File file = service.files().create(fileMetadata, mediaContent).execute();
        return file.getId();
    }

    public void updateFile(String fileId, DriveMimeType mimeType, java.io.File newContent) throws IOException {
        log.info("Updating content of file with ID: {}", fileId);
        File fileMetadata = new File();
        fileMetadata.setMimeType(mimeType.getMimeType());

        FileContent mediaContent = new FileContent(mimeType.getMimeType(), newContent);
        service.files().update(fileId, fileMetadata, mediaContent).execute();
    }

    public DateTime getFileLastModifiedTime(String fileId) throws IOException {
        log.info("Retrieving last modified time for file with ID: {}", fileId);
        File file = service.files().get(fileId).setFields("modifiedTime").execute();
        return file.getModifiedTime();
    }

    public boolean fileExists(String fileId) {
        log.info("Checking existence of file with ID: {}", fileId);
        try {
            service.files().get(fileId).setFields("id").execute();
            return true;
        } catch (IOException e) {
            log.warn("File with ID: {} does not exist.", fileId);
            return false;
        }
    }

    public String downloadFile(String fileId) throws IOException {
        log.info("Downloading file with ID to string memory: {}", fileId);
        Path tempFileDir = Path.of(System.getProperty("java.io.tmpdir"));
        Path tempFilePath = downloadFile(fileId, tempFileDir);

        String content = java.nio.file.Files.readString(tempFilePath);
        try {
            java.nio.file.Files.delete(tempFilePath);
        } catch (IOException e) {
            log.warn("Failed to delete temporary file: {}", tempFilePath, e);
        }
        return content.trim();
    }

    public Path downloadFile(String fileId, Path directory) throws IOException {
        log.info("Downloading file with ID: {} to directory: {}", fileId, directory);
        File file = service.files().get(fileId).setFields("name").execute();
        Path filePath = directory.resolve(file.getName());

        try (OutputStream outputStream = java.nio.file.Files.newOutputStream(
                filePath,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE)) {
            service.files().get(fileId).executeMediaAndDownloadTo(outputStream);
        }

        return filePath;
    }
}
