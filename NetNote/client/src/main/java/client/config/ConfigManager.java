package client.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class ConfigManager {

    private final String configFilePath = "client/src/main/resources/client/config/config.json";
    /**
     * saves an object to config file
     * @param object the object to be saved
     */
    public void saveToFile(Object object) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        //objectMapper.enable(com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT);

        if(object == null)
            return;

        try(BufferedWriter fileWriter = new BufferedWriter(new FileWriter(configFilePath))) {
            fileWriter.write(objectMapper.writeValueAsString(object));
            fileWriter.flush();
            System.out.println("Configuration saved successfully to " + configFilePath);
        } catch (IOException e) {
            //e.printStackTrace();
        }
    }

    /**
     * Reads and deserializes the JSON data from a file into a Config object.
     * @param config in which config to load from file
     */
    public void loadFromFileInto(Config config) {
        ObjectMapper objectMapper = new ObjectMapper();

        File file = new File(configFilePath);
        if (!file.exists()) {
            System.out.println("ohoh!!!!!!");
            config.setNoteCollections(new ArrayList<>());
        }

        try {
            Config fromFile = objectMapper.readValue(file, Config.class);
            System.out.println(fromFile);

            config.setNoteCollections(fromFile.getNoteCollections());
        } catch (IOException e) {
            System.out.println("ohoh!!!!!!");
            config.setNoteCollections(new ArrayList<>());
        }
    }

}

