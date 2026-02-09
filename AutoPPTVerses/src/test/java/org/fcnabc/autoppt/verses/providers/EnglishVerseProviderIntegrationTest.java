package org.fcnabc.autoppt.verses.providers;

import com.google.inject.CreationException;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.ProvisionException;
import org.fcnabc.autoppt.io.IOModule;
import org.fcnabc.autoppt.verses.VerseModule;
import org.fcnabc.autoppt.verses.models.BibleBook;
import org.fcnabc.autoppt.verses.models.BibleVerse;
import org.fcnabc.autoppt.verses.models.BibleVerseCoordinates;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

@Tag("integration")
class EnglishVerseProviderIntegrationTest {
    // Disable/enable this integration tests
    private static final Boolean TESTS_ENABLED = true;

    private EnglishVerseProvider provider;

    @BeforeEach
    void setUp() {
        Assumptions.assumeTrue(TESTS_ENABLED, "English Verse Provider Integration Tests are disabled. Set TESTS_ENABLED to true to enable them.");

        try {
            Injector injector = Guice.createInjector(new IOModule(), new VerseModule());
            provider = injector.getInstance(EnglishVerseProvider.class);
        } catch (ProvisionException | CreationException e) {
            // Skip test if key file is missing
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException && 
               (cause.getMessage().contains("Key file not found") || cause.getMessage().contains("Key file is not readable"))) {
                assumeTrue(false, "Integration test skipped: " + cause.getMessage());
            }
            throw e;
        }
    }

    @Test
    void testGetVerse_RealApiCall_John316() throws Exception {
        BibleVerseCoordinates coords = new BibleVerseCoordinates(BibleBook.JOHN, 3, 16);
        List<BibleVerse> verses = provider.getVerse(coords);

        assertNotNull(verses, "Response should not be null");
        assertFalse(verses.isEmpty(), "Should return at least one verse");
        assertEquals(1, verses.size(), "Should return exactly one verse");
        assertNotNull(verses.get(0));

        assertEquals(3, verses.get(0).chapter());
        assertEquals(16, verses.get(0).verseNumber());
        assertEquals(BibleBook.JOHN, verses.get(0).book());
        assertEquals("“For God so loved the world, that he gave his only Son, that whoever believes in him should not perish but have eternal life.", verses.get(0).text());
    }

    @Test
    void testGetVerse_RealApiCall_Psalm23() throws Exception {
        BibleVerseCoordinates startCoords = new BibleVerseCoordinates(BibleBook.PSALMS, 23, 1);
        BibleVerseCoordinates endCoords = new BibleVerseCoordinates(BibleBook.PSALMS, 23, 6);
        List<BibleVerse> verses = provider.getVerses(startCoords, endCoords);

        assertNotNull(verses, "Response should not be null");
        assertEquals(6, verses.size(), "Should return 6 verses for Psalm 23:1-6");

        for (int i = 0; i < verses.size(); i++) {
            assertNotNull(verses.get(i), "Verse " + (i + 1) + " should not be null");
            assertEquals(23, verses.get(i).chapter(), "Verse " + (i + 1) + " should be in chapter 23");
            assertEquals(i + 1, verses.get(i).verseNumber(), "Verse " + (i + 1) + " should have verse number " + (i + 1));
            assertEquals(BibleBook.PSALMS, verses.get(i).book(), "Verse " + (i + 1) + " should be from the book of Psalms");
        }

        assertEquals("The LORD is my shepherd; I shall not want.", verses.get(0).text());
        assertEquals("He makes me lie down in green pastures. He leads me beside still waters.", verses.get(1).text());
        assertEquals("He restores my soul. He leads me in paths of righteousness for his name’s sake.", verses.get(2).text());
        assertEquals("Even though I walk through the valley of the shadow of death, I will fear no evil, for you are with me; your rod and your staff, they comfort me.", verses.get(3).text());
        assertEquals("You prepare a table before me in the presence of my enemies; you anoint my head with oil; my cup overflows.", verses.get(4).text());
        assertEquals("Surely goodness and mercy shall follow me all the days of my life, and I shall dwell in the house of the LORD forever.", verses.get(5).text());
    }
}
