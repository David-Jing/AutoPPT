package org.fcnabc.autoppt.io;

import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import org.fcnabc.autoppt.io.model.Key;

@Singleton
public class KeyStore {
    private final Path KEY_DIRECTORY;

    @Inject
    public KeyStore(@Named("KeyDirectory") Path keyDirectory) throws RuntimeException {
        this.KEY_DIRECTORY = keyDirectory;
        if (!KEY_DIRECTORY.toFile().exists() && !KEY_DIRECTORY.toFile().mkdirs()) {
            throw new RuntimeException("Failed to create directory for keys: " + KEY_DIRECTORY);
        }
    }

    public Path getKeyDirectory() {
        return KEY_DIRECTORY;
    }

    public String getKey(Key key) throws RuntimeException {
        Path keyPath = getKeyPath(key);
        if (!Files.exists(keyPath)) {
            throw new RuntimeException("Key file not found: " + keyPath);
        }
        try {
            return Files.readString(keyPath).trim();
        } catch (Exception e) {
            throw new RuntimeException("Failed to read key file: " + keyPath, e);
        }
    }

    public void setKey(Key key, String value) throws RuntimeException {
        Path keyPath = getKeyPath(key);
        try {
            Files.writeString(keyPath, value, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (Exception e) {
            throw new RuntimeException("Failed to write key file: " + keyPath, e);
        }
    }

    private Path getKeyPath(Key key) {
        return KEY_DIRECTORY.resolve(key.getKeyFile());
    }

}
