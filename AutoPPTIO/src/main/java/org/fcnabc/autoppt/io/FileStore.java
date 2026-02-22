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
public class FileStore {
    private final Path FILE_DIRECTORY;

    @Inject
    public FileStore(@Named("FileDirectory") Path fileDirectory) throws IOException {
        this.FILE_DIRECTORY = fileDirectory;
        if (!FILE_DIRECTORY.toFile().exists() && !FILE_DIRECTORY.toFile().mkdirs()) {
            throw new IOException("Failed to create directory for files: " + FILE_DIRECTORY);
        }
    }

    public Path getFileDirectory() {
        return FILE_DIRECTORY;
    }

    public List<String> listFiles() throws IOException {
        try (Stream<Path> paths = Files.list(FILE_DIRECTORY)) {
            return paths.filter(Files::isRegularFile)
                        .map(Path::getFileName)
                        .map(Path::toString)
                        .collect(Collectors.toList());
        } catch (IOException e) {
            throw new IOException("Failed to list files in directory: " + FILE_DIRECTORY, e);
        }
    }

    public void setFile(String fileName, String content) throws IOException {
        Path filePath = FILE_DIRECTORY.resolve(fileName);
        try {
            Files.writeString(filePath, content, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new IOException("Failed to write file: " + filePath, e);
        }
    }

    public boolean fileExists(String fileName) {
        Path filePath = FILE_DIRECTORY.resolve(fileName);
        return Files.exists(filePath);
    }

    public String getFileContent(String fileName) throws IOException {
        Path filePath = FILE_DIRECTORY.resolve(fileName);
        if (!fileExists(fileName)) {
            throw new IOException("File not found: " + filePath);
        }
        try {
            return Files.readString(filePath).trim();
        } catch (IOException e) {
            throw new IOException("Failed to read file: " + filePath, e);
        }
    }

    public File getFile(String fileName) throws IOException {
        Path filePath = FILE_DIRECTORY.resolve(fileName);
        if (!fileExists(fileName)) {
            throw new IOException("File not found: " + filePath);
        }
        return filePath.toFile();
    }
}
