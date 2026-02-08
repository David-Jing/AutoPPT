package org.fcnabc.autoppt.google;

import com.google.inject.Guice;
import com.google.inject.Injector;
import lombok.extern.slf4j.Slf4j;
import org.fcnabc.autoppt.google.models.ParagraphAlignment;
import org.fcnabc.autoppt.google.models.SlideObject;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test for GoogleSlides wrapper.
 * This test runs against the real Google Slides API and requires:
 * 1. src/main/resources/credentials.json to be present.
 * 2. An active internet connection.
 * 3. User intervention to authorize the app in the browser (first run only).
 */
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GoogleSlidesIntegrationTest {
    // Set to false to keep test files for manual inspection
    private static final Boolean CLEANUP_AFTER_TESTS = false;

    // Disable/enable this integration tests
    private static final Boolean TESTS_ENABLED = false;

    /*
    Test using the following template which contains 1 slide with:
    - 1 Textbox with non-empty text
    - 1 Table (2x2) with non-empty cells
    https://docs.google.com/presentation/d/1rzfb64jlw2K05HY0bxx8C2ez33G9qALQMyPuMjuUrH4/edit
    */
    private static final String EXISTING_PRESENTATION_ID = "1rzfb64jlw2K05HY0bxx8C2ez33G9qALQMyPuMjuUrH4";

    private static GoogleDrive googleDrive;
    private static GoogleSlides googleSlides;
    private static String workingPresentationId;

    @BeforeAll
    public static void setup() {
        Assumptions.assumeTrue(TESTS_ENABLED, "Google Slides Integration Tests are disabled. Set TESTS_ENABLED to true to enable them.");
        
        log.info("Setting up Google Slides Integration Test...");
        try {
            Injector injector = Guice.createInjector(new GoogleModule());
            googleDrive = injector.getInstance(GoogleDrive.class);
            googleSlides = injector.getInstance(GoogleSlides.class);
        } catch (Exception e) {
            log.error("Failed to initialize Google Services. Ensure credentials.json is present.", e);
            throw new RuntimeException(e);
        }
    }

    @Test
    @Order(1)
    public void testDuplicatePresentation() throws IOException {
        String fileName = "autoppt_slides_integration_test_" + System.currentTimeMillis();
        log.info("Duplicating presentation: {} to {}", EXISTING_PRESENTATION_ID, fileName);

        workingPresentationId = googleDrive.duplicateFile(EXISTING_PRESENTATION_ID, fileName);
        assertNotNull(workingPresentationId, "Duplicated presentation ID should not be null");
        log.info("Working Presentation ID: {}", workingPresentationId);
    }

    @Test
    @Order(2)
    public void testSetPresentationContext() throws IOException {
        Assumptions.assumeTrue(workingPresentationId != null, "Skipping because duplication failed");

        googleSlides.setPresentationContext(workingPresentationId);
        int slideCount = googleSlides.getSlideCount();
        assertTrue(slideCount > 0, "Presentation should have at least one slide");
        log.info("Slide Count: {}", slideCount);
    }

    @Test
    @Order(3)
    public void testModifySlideContent() throws IOException {
        Assumptions.assumeTrue(workingPresentationId != null, "Skipping because duplication failed");

        // Refresh context to ensure we have latest state
        googleSlides.setPresentationContext(workingPresentationId);
        int slideIndex = 0;

        // 1. Identify Elements
        Map<SlideObject, String> textElements = googleSlides.getTextElementsInSlide(slideIndex);
        List<SlideObject> tables = googleSlides.getTablesInSlide(slideIndex);

        assertFalse(textElements.isEmpty() && textElements.size() == 1, "Should find one textbox element in slide");
        assertFalse(tables.isEmpty() && tables.size() == 1, "Should find one table element in slide");

        log.info("Found {} text elements and {} tables in slide {}", textElements.size(), tables.size(), slideIndex);

        // 2. Modify Textbox (assumed to be the non-table text element)
        SlideObject textBox = textElements.keySet().stream()
                .filter(so -> !so.isTableCell())
                .findFirst()
                .orElse(null);

        if (textBox != null) {
            log.info("Modifying textbox: {}", textBox.id());
            googleSlides.setText(textBox, "Updated Textbox Content");
            googleSlides.setTextColor(textBox, "#FF5733"); // Custom orange
            googleSlides.setStyle(textBox, 0, 7, true, false, true, false, 18.0, "Georgia");
            googleSlides.setParagraphStyle(textBox, 1.5f, ParagraphAlignment.CENTER);
        } else {
            log.warn("No standalone textbox found in slide 0");
        }

        // 3. Modify Table (assumed 2x2)
        if (!tables.isEmpty()) {
            String tableId = tables.get(0).id();
            log.info("Modifying table: {}", tableId);

            // Modifying Cell (0,0)
            SlideObject cell00 = new SlideObject(tableId, 0, 0, true);
            googleSlides.setText(cell00, "Cell(0,0)");
            googleSlides.setTextColor(cell00, "#0000FF"); // Blue

            // Modifying Cell (1,1)
            SlideObject cell11 = new SlideObject(tableId, 1, 1, true);
            googleSlides.setText(cell11, "Cell(1,1)");
            googleSlides.setStyle(cell11, 0, 5, false, true, false, false, 12.0, "Arial");
        } else {
            log.warn("No tables found in slide 0");
        }

        // Execute Batch
        googleSlides.executeUpdates();
        log.info("Executed updates on slide content");
    }

    @Test
    @Order(4)
    public void testDuplicateSlide() throws IOException {
        Assumptions.assumeTrue(workingPresentationId != null, "Skipping because duplication failed");

        googleSlides.setPresentationContext(workingPresentationId);
        int initialCount = googleSlides.getSlideCount();

        log.info("Duplicating slide index 0");
        Map<SlideObject, SlideObject> duplicationMap = googleSlides.duplicateSlide(0);
        googleSlides.executeUpdates();

        // Verify count increased
        int newCount = googleSlides.getSlideCount();
        assertEquals(initialCount + 1, newCount, "Slide count should increase by 1");
        log.info("New Slide Count: {}", newCount);

        // Verify we got a mapping
        assertFalse(duplicationMap.isEmpty(), "Duplication map should not be empty");
    }

    @Test
    @Order(5)
    public void testDeleteSlide() throws IOException {
        Assumptions.assumeTrue(workingPresentationId != null, "Skipping because duplication failed");
        
        googleSlides.setPresentationContext(workingPresentationId);
        int initialCount = googleSlides.getSlideCount();
        
        if (initialCount > 1) {
            // Delete the last slide (which should be the duplicate we just made)
            String lastSlideId = googleSlides.getSlideId(initialCount - 1);
            log.info("Deleting last slide: {}", lastSlideId);
            
            googleSlides.deleteSlide(lastSlideId);
            googleSlides.executeUpdates();
            
            assertEquals(initialCount - 1, googleSlides.getSlideCount(), "Slide count should decrease by 1");
        }
    }

    @AfterAll
    public static void cleanup() throws IOException {
        if (workingPresentationId != null && CLEANUP_AFTER_TESTS) {
            log.info("Cleaning up - deleting file: {}", workingPresentationId);
            googleDrive.deleteFile(workingPresentationId);
        }
    }
}
