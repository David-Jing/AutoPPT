package org.fcnabc.autoppt.verses.providers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;


import org.fcnabc.autoppt.verses.models.BibleBook;
import org.fcnabc.autoppt.verses.models.BibleVerse;
import org.fcnabc.autoppt.verses.models.BibleVerseCoordinates;

class EnglishVerseProviderTest {

    private EnglishVerseProvider provider;
    
    @Mock
    private HttpClient mockHttpClient;
        
    @Mock
    private HttpResponse<String> mockResponse;

    private final String dummyKey = "test-api-key";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        provider = new EnglishVerseProvider(dummyKey, mockHttpClient);
    }

    @Test
    void testGetVerse_Success() throws Exception {
        String jsonResponse = "{\"passages\": [\"[16] For God so loved the world...\"]}";
        
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn(jsonResponse);
        when(mockHttpClient.send(
            any(HttpRequest.class),
            ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()
        )).thenReturn(mockResponse);
            
        BibleVerseCoordinates coords = new BibleVerseCoordinates(BibleBook.JOHN, 3, 16);
        List<BibleVerse> result = provider.getVerse(coords);

        assertFalse(result.isEmpty());
        assertEquals(16, result.get(0).verseNumber());
        assertEquals("For God so loved the world...", result.get(0).text());
        verify(mockHttpClient, times(1)).send(any(), any());
    }

    @Test
    void testGetVerse_ApiError() throws Exception {
        // Simulate a 401 Unauthorized (e.g., bad API key)
        when(mockResponse.statusCode()).thenReturn(401);
        when(mockHttpClient.send(
            any(HttpRequest.class),
            ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()
        )).thenReturn(mockResponse);

        BibleVerseCoordinates coords = new BibleVerseCoordinates(BibleBook.JOHN, 1, 1);

        // Verify that our provider throws an IOException as expected
        assertThrows(IOException.class, () -> {
            provider.getVerse(coords);
        });
    }

    @Test
    void testParseRawBibleVerseString() {
        String rawText = "[1] In the beginning [2] God created";
        
        var verses = provider.parseRawBibleVerseString(BibleBook.GENESIS, 1, rawText);
        
        assertEquals(2, verses.size());
        assertEquals(1, verses.get(0).verseNumber());
        assertEquals("In the beginning", verses.get(0).text());
        assertEquals(2, verses.get(1).verseNumber());
        assertEquals("God created", verses.get(1).text());
    }
    
    @Test
    void testIsVerseOutOfBounds_VerseFromDifferentBook() {
        BibleVerse verse = new BibleVerse(new BibleVerseCoordinates(BibleBook.MARK, 1, 1), "text");
        BibleVerseCoordinates startCord = new BibleVerseCoordinates(BibleBook.JOHN, 3, 16);
        BibleVerseCoordinates endCord = new BibleVerseCoordinates(BibleBook.JOHN, 5, 10);
        
        assertTrue(provider.isVerseOutOfBounds(verse, 1, startCord, endCord));
    }

    @Test
    void testIsVerseOutOfBounds_VerseBeforeStartBoundary() {
        BibleVerse verse = new BibleVerse(new BibleVerseCoordinates(BibleBook.JOHN, 3, 15), "text");
        BibleVerseCoordinates startCord = new BibleVerseCoordinates(BibleBook.JOHN, 3, 16);
        BibleVerseCoordinates endCord = new BibleVerseCoordinates(BibleBook.JOHN, 5, 10);
        
        assertTrue(provider.isVerseOutOfBounds(verse, 3, startCord, endCord));
    }

    @Test
    void testIsVerseOutOfBounds_VerseAfterEndBoundary() {
        BibleVerse verse = new BibleVerse(new BibleVerseCoordinates(BibleBook.JOHN, 5, 11), "text");
        BibleVerseCoordinates startCord = new BibleVerseCoordinates(BibleBook.JOHN, 3, 16);
        BibleVerseCoordinates endCord = new BibleVerseCoordinates(BibleBook.JOHN, 5, 10);
        
        assertTrue(provider.isVerseOutOfBounds(verse, 5, startCord, endCord));
    }

    @Test
    void testIsVerseOutOfBounds_VerseWithinBounds() {
        BibleVerse verse = new BibleVerse(new BibleVerseCoordinates(BibleBook.JOHN, 4, 5), "text");
        BibleVerseCoordinates startCord = new BibleVerseCoordinates(BibleBook.JOHN, 3, 16);
        BibleVerseCoordinates endCord = new BibleVerseCoordinates(BibleBook.JOHN, 5, 10);
        
        assertFalse(provider.isVerseOutOfBounds(verse, 4, startCord, endCord));
    }

    @Test
    void testIsVerseOutOfBounds_VerseAtStartBoundary() {
        BibleVerse verse = new BibleVerse(new BibleVerseCoordinates(BibleBook.JOHN, 3, 16), "text");
        BibleVerseCoordinates startCord = new BibleVerseCoordinates(BibleBook.JOHN, 3, 16);
        BibleVerseCoordinates endCord = new BibleVerseCoordinates(BibleBook.JOHN, 5, 10);
        
        assertFalse(provider.isVerseOutOfBounds(verse, 3, startCord, endCord));
    }

    @Test
    void testIsVerseOutOfBounds_VerseAtEndBoundary() {
        BibleVerse verse = new BibleVerse(new BibleVerseCoordinates(BibleBook.JOHN, 5, 10), "text");
        BibleVerseCoordinates startCord = new BibleVerseCoordinates(BibleBook.JOHN, 3, 16);
        BibleVerseCoordinates endCord = new BibleVerseCoordinates(BibleBook.JOHN, 5, 10);
        
        assertFalse(provider.isVerseOutOfBounds(verse, 5, startCord, endCord));
    }
}