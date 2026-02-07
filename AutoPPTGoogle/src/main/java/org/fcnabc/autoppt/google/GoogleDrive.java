package org.fcnabc.autoppt.google;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.FileContent;
import com.google.api.client.util.DateTime;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.Drive.Files.Copy;
import com.google.api.services.drive.model.File;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

/**
 * A wrapper around the Google Drive API that provides methods for 
 * file operations such as duplication, deletion, uploading, and downloading.
 */
@Singleton
public class GoogleDrive {
    private final Credential credential;
    private Drive service;

    @Inject
    public GoogleDrive(Credential credential, @Named("googleAppName") String applicationName) {
        this.credential = credential;
        this.service = new Drive.Builder(
                this.credential.getTransport(),
                this.credential.getJsonFactory(),
                this.credential)
                .setApplicationName(applicationName)
                .build();
    }

    public void deleteFile(String fileId) throws IOException {
        service.files().delete(fileId).execute();
    }

    public String duplicateFile(String fileId, String newTitle) throws IOException {
        Copy copyRequest = service.files().copy(fileId, new File().setName(newTitle));
        File copiedFile = copyRequest.execute();
        return copiedFile.getId();
    }

    public void uploadFile(String fileName, String mimeType, java.io.File fileContent) throws IOException {
        File fileMetadata = new File();
        fileMetadata.setName(fileName);
        fileMetadata.setMimeType(mimeType);

        FileContent mediaContent = new FileContent(mimeType, fileContent);
        service.files().create(fileMetadata, mediaContent).execute();
    }

    public void replaceFileContent(String fileId, String mimeType, java.io.File newContent) throws IOException {
        File fileMetadata = new File();
        fileMetadata.setMimeType(mimeType);

        FileContent mediaContent = new FileContent(mimeType, newContent);
        service.files().update(fileId, fileMetadata, mediaContent).execute();
    }

    public DateTime getFileLastModifiedTime(String fileId) throws IOException {
        File file = service.files().get(fileId).setFields("modifiedTime").execute();
        return file.getModifiedTime();
    }

    public Path downloadFile(String fileId, Path directory) throws IOException {
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
