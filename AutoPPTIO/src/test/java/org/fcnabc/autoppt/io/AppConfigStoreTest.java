package org.fcnabc.autoppt.io;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.fcnabc.autoppt.io.model.AppConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class AppConfigStoreTest {

    @TempDir
    Path tempDir;

    private AppConfigStore appConfigStore;
    private Path appDirectory;
    private Path appConfigFile;

    @BeforeEach
    void setUp() {
        appDirectory = tempDir.resolve("AutoPPT"); 
        // AppConfigStore creates directory if not exists
        appConfigFile = appDirectory.resolve("AppConfig.json");
    }

    @Test
    @SuppressWarnings("null")
    void testInitialize_NoConfigFile_ShouldNotThrow() {
        // When initializing with a non-existent file, it should just log a warning and not throw
        assertDoesNotThrow(() -> appConfigStore = new AppConfigStore(appDirectory));
        
        // And getAppConfig should throw because it's not set
        RuntimeException exception = assertThrows(RuntimeException.class, () -> appConfigStore.getAppConfig());
        assertEquals("App configuration not found. Please set the app configuration first.", exception.getMessage());
    }

    @Test
    void testSetAppConfig_ShouldUpdateInMemoryOnly() {
        // AppConfigStore creates the directory if it doesn't exist
        appConfigStore = new AppConfigStore(appDirectory);
        
        AppConfig config = new AppConfig("google-key", "esv-key", "file-id");
        String json = config.toJson();

        appConfigStore.setAppConfig(json);

        // Verify in-memory state
        AppConfig loadedConfig = appConfigStore.getAppConfig();
        assertEquals(config, loadedConfig);
        
        // Verify file does NOT exist (since setAppConfig no longer saves automatically)
        assertFalse(Files.exists(appConfigFile), "File should not be created by setAppConfig alone");
    }

    @Test
    void testSaveAppConfigToFile_ShouldSaveToFile() throws IOException {
        appConfigStore = new AppConfigStore(appDirectory);
        
        AppConfig config = new AppConfig("google-key", "esv-key", "file-id");
        String json = config.toJson();

        appConfigStore.setAppConfig(json);
        appConfigStore.saveAppConfigToFile();

        // Verify file exists
        assertTrue(Files.exists(appConfigFile));
        
        // Verify content matches
        String readJson = Files.readString(appConfigFile);
        assertEquals(json, readJson);
        
        // Verify we can reload it
        AppConfigStore newStore = new AppConfigStore(appDirectory);
        assertEquals(config, newStore.getAppConfig());
    }
    
    @Test
    @SuppressWarnings("null")
    void testSaveAppConfigToFile_NoConfig_ShouldThrow() {
        appConfigStore = new AppConfigStore(appDirectory);
        
        RuntimeException exception = assertThrows(RuntimeException.class, () -> appConfigStore.saveAppConfigToFile());
        assertEquals("App configuration not found. Please set the app configuration first.", exception.getMessage());
    }

    @Test
    void testInitialize_WithExistingConfigFile_ShouldLoadConfig() throws IOException {
        // Setup existing file
        if (!Files.exists(appDirectory)) {
            Files.createDirectories(appDirectory);
        }
        AppConfig config = new AppConfig("google-key", "esv-key", "file-id");
        Files.writeString(appConfigFile, config.toJson());

        // Initialize store
        appConfigStore = new AppConfigStore(appDirectory);

        // Verify config loaded
        AppConfig loadedConfig = appConfigStore.getAppConfig();
        assertEquals(config, loadedConfig);
    }
    
    @Test
    void testSetAppConfig_InvalidJson_ShouldThrow() {
        appConfigStore = new AppConfigStore(appDirectory);
        String invalidJson = "invalid";
        
        assertThrows(RuntimeException.class, () -> appConfigStore.setAppConfig(invalidJson));
    }
}
