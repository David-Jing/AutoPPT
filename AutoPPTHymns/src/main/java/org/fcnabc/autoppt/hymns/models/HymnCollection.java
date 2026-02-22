package org.fcnabc.autoppt.hymns.models;

import java.util.Map;

import org.msgpack.jackson.dataformat.MessagePackFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;

public record HymnCollection(
    Instant lastUpdated,
    Map<String, Hymn> hymns
) {
    private static final ObjectMapper MAPPER = new ObjectMapper(new MessagePackFactory());

    public static HymnCollection fromBytes(byte[] messagePack) throws RuntimeException {
        try {
            HymnCollection hymnCollection = MAPPER.readValue(messagePack, HymnCollection.class);
            return hymnCollection;
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse hymn collection bytes", e);
        }
    }

    public static byte[] toBytes(HymnCollection hymnCollection) throws RuntimeException {
        try {
            return MAPPER.writeValueAsBytes(hymnCollection);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize hymn collection to bytes", e);
        }
    }
}
