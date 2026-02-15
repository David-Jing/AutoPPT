package org.fcnabc.autoppt.io;

import org.fcnabc.autoppt.io.model.Key;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class KeyStoreTest {

    @TempDir
    Path tempDir;

    private KeyStore getKeys;

    @BeforeEach
    void setUp() {
        getKeys = new KeyStore(tempDir);
    }

    @Test
    void testGetKey_readsFromFile() throws IOException {
        Key key = Key.GOOGLE_CREDENTIALS;
        Path keyPath = tempDir.resolve(key.getKeyFile());
        Files.writeString(keyPath, "secret-value");

        // First read: from file
        String value = getKeys.getKey(key);
        assertEquals("secret-value", value);

        // Modify file directly
        Files.writeString(keyPath, "modified-value");

        // Second read: should be from file (modified value)
        String newValue = getKeys.getKey(key);
        assertEquals("modified-value", newValue);
    }

    @Test
    void testSetKey_writesToFile() throws IOException {
        Key key = Key.ESV_API_KEY;
        String expectedValue = "new-api-key";

        getKeys.setKey(key, expectedValue);

        // Check file content
        Path keyPath = tempDir.resolve(key.getKeyFile());
        assertTrue(Files.exists(keyPath));
        assertEquals(expectedValue, Files.readString(keyPath));

        // Delete file -> getKey should fail
        Files.delete(keyPath);
        assertThrows(RuntimeException.class, () -> getKeys.getKey(key));
    }

    @Test
    void testGetKey_throwsIfNotFound() {
        Key key = Key.GOOGLE_CREDENTIALS;
        assertThrows(RuntimeException.class, () -> getKeys.getKey(key));
    }
}
