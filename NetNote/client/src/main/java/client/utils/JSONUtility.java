package client.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import commons.Note;

public class JSONUtility {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * turns an Object into a JSON type String
     *
     * @param object takes an object as a parameter
     * @return returns a String that is in JSON format
     * @throws JsonProcessingException can throw Exception when processing JSON String
     */
    public static String toJson(Object object) throws JsonProcessingException {
        return objectMapper.writeValueAsString(object);
    }

    /**
     * creates a Note instance based on a JSON string
     *
     * @param jsonString takes JSON String as parameter
     * @return returns a Note that belongs to Note class
     * @throws JsonProcessingException can throw Exception when processing JSON String
     */
    public static Note fromJsonToNote(String jsonString) throws JsonProcessingException {
        return objectMapper.readValue(jsonString, Note.class);
    }

    /**
     * creates a JsonNode structure from a String, that can be used to get attributes of the object
     *
     * @param jsonString takes JSON string as parameter
     * @return returns a JSON node which splits the object into its components
     * @throws JsonProcessingException can throw Exception when processing JSON String
     */
    public static JsonNode parseJson(String jsonString) throws JsonProcessingException {
        return objectMapper.readTree(jsonString);
    }

    /**
     * takes JSON String as input and returns pretty printed version of the string
     *
     * @param jsonString takes JSON string as parameter
     * @return returns the prettey print version of the JSON String
     * @throws JsonProcessingException can throw Exception when processing JSON String
     */
    public static String prettyPrint(String jsonString) throws JsonProcessingException {
        JsonNode jsonNode = objectMapper.readTree(jsonString);
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNode);
    }
}

