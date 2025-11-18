package client.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import commons.NoteCollection;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Config {
    @JsonProperty("noteCollections")
    private final List<NoteCollection> noteCollections;

    /**
     * noargs constructor
     */
    public Config() {
        this.noteCollections = new ArrayList<>();
    }

    /**
     * json creator
     * @param noteCollections noteCollections
     */
    @JsonCreator
    public Config(
            @JsonProperty("noteCollections") List<NoteCollection> noteCollections
    ) {
        this.noteCollections = noteCollections;
    }

    /**
     * getter for all note collections
     * @return all note collections
     */
    @JsonProperty("noteCollections")
    public List<NoteCollection> getNoteCollections() {
        return noteCollections;
    }

    /**
     * setter for all note collections
     * @param noteCollections note collections
     */
    @JsonProperty("noteCollections")
    public void setNoteCollections(List<NoteCollection> noteCollections) {
        this.noteCollections.clear();
        this.noteCollections.addAll(noteCollections);
    }

    /**
     * adds one note collection to the config
     * @param noteCollection the note collection
     */
    public void addNoteCollection(NoteCollection noteCollection) {
        this.noteCollections.add(noteCollection);
    }

    /**
     * compares an object with this object
     * @param o the object
     * @return true/false
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Config config = (Config) o;
        return Objects.equals(noteCollections, config.noteCollections);
    }

    /**
     * generates a hashcode for this object
     * @return a hashcode of this object
     */
    @Override
    public int hashCode() {
        return Objects.hash(noteCollections);
    }

    /**
     * to string method
     * @return a string of this object
     */
    @Override
    public String toString() {
        return "Config{" +
                "noteCollections=" + noteCollections +
                '}';
    }
}
