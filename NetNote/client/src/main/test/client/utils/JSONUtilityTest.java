package client.utils;

import client.utils.JSONUtility;
import com.fasterxml.jackson.core.JsonProcessingException;
import commons.Note;
import commons.NoteCollection;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for JSONUtility. Contains unit tests for methods responsible for
 * JSON serialization, deserialization, and formatting.
 */
public class JSONUtilityTest {

    /**
     * Tests the toJson method to ensure it correctly serializes a valid Note object.
     *
     * @throws JsonProcessingException if there is a problem during JSON processing.
     */
    @Test
    public void testToJson_ValidObject() throws JsonProcessingException {
        NoteCollection collection = new NoteCollection("Test Collection", "test-collection", "http://example.com/collection");
        Note note = new Note("Test Title", "Test Content", collection);
        collection.addNote(note);
        String json = JSONUtility.toJson(note);
        assertTrue(json.contains("Test Title"));
        assertTrue(json.contains("Test Content"));
    }



    /**
     * Tests the fromJsonToNote method to ensure it correctly deserializes a valid JSON string
     * into a Note object.
     *
     * @throws JsonProcessingException if there is a problem during JSON processing.
     */
    @Test
    public void testFromJsonToNote_ValidJson() throws JsonProcessingException {
        String json = "{" +
                "\"title\":\"Test Title\"," +
                "\"content\":\"Test Content\"," +
                "\"collection\":null}"; // Note: Collection is null here for simplicity

        Note note = JSONUtility.fromJsonToNote(json);

        assertEquals("Test Title", note.getTitle());
        assertEquals("Test Content", note.getContent());
        assertNull(note.getCollection());
    }

    /**
     * Tests the fromJsonToNote method with an invalid JSON string and expects
     * a JsonProcessingException to be thrown.
     */
    @Test
    public void testFromJsonToNote_InvalidJson() {
        String invalidJson = "{invalid json}";

        assertThrows(JsonProcessingException.class, () -> {
            JSONUtility.fromJsonToNote(invalidJson);
        });
    }

    /**
     * Tests the parseJson method to ensure it correctly parses a valid JSON string
     * into a JsonNode object.
     *
     * @throws JsonProcessingException if there is a problem during JSON processing.
     */
    @Test
    public void testParseJson_ValidJson() throws JsonProcessingException {
        String json = "{" +
                "\"title\":\"Test Title\"," +
                "\"content\":\"Test Content\"}";

        var jsonNode = JSONUtility.parseJson(json);

        assertEquals("Test Title", jsonNode.get("title").asText());
        assertEquals("Test Content", jsonNode.get("content").asText());
    }

    /**
     * Tests the parseJson method with an invalid JSON string and expects
     * a JsonProcessingException to be thrown.
     */
    @Test
    public void testParseJson_InvalidJson() {
        String invalidJson = "{invalid json}";

        assertThrows(JsonProcessingException.class, () -> {
            JSONUtility.parseJson(invalidJson);
        });
    }

    /**
     * Tests the prettyPrint method to ensure it correctly formats a valid JSON string
     * into a readable, indented format.
     *
     * @throws JsonProcessingException if there is a problem during JSON processing.
     */
    @Test
    public void testPrettyPrint_ValidJson() throws JsonProcessingException {
        String json = "{" +
                "\"title\":\"Test Title\"," +
                "\"content\":\"Test Content\"}";

        String prettyJson = JSONUtility.prettyPrint(json);

        assertTrue(prettyJson.contains("Test Title"));
        assertTrue(prettyJson.contains("Test Content"));
        assertTrue(prettyJson.contains(System.lineSeparator()));
    }

    /**
     * Tests the prettyPrint method with an invalid JSON string and expects
     * a JsonProcessingException to be thrown.
     */
    @Test
    public void testPrettyPrint_InvalidJson() {
        String invalidJson = "{invalid json}";

        assertThrows(JsonProcessingException.class, () -> {
            JSONUtility.prettyPrint(invalidJson);
        });
    }
}
