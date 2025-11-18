/*
 * -------------------------------------------------------------
 * | JAVA DOC comments are generated with ChatGPT              |
 * -------------------------------------------------------------
 */

package commons;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a collection of notes with an ID, title, and a list of notes.
 */
@Entity
public class NoteCollection {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String title;
    private String internalName;
    private String url;

    @OneToMany(
            mappedBy = "collection",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JsonManagedReference
    private List<Note> notes;

    protected NoteCollection() {}

    /**
     * protected Constructor to instantiate the Note object
     */
    protected NoteCollection(String internalName) {
        this.internalName = internalName;
    }

    /**
     * Json creator
     * @param title title
     * @param id id
     * @param internalName internalName
     * @param url url
     * @param notes notes
     */
    @JsonCreator
    public NoteCollection(
            @JsonProperty("title") String title,
            @JsonProperty("id") long id,
            @JsonProperty("internalName") String internalName,
            @JsonProperty("url") String url,
            @JsonProperty("notes") List<Note> notes
          ) {
        this.title = title;
        this.id = id;
        this.internalName = internalName;
        this.url = url;
        this.notes = notes;
    }

    /**
     * Constructs a new NoteCollection object with the specified ID and title.
     * Initializes an empty list of notes.
     *
     * @param title the title of the note collection
     * @param internalName the internalName of NoteCollection object that is
     * going to be used for the url
     * @param url the url of the NoteCollection object
     */
    public NoteCollection(String title, String internalName, String url) {
        this.title = title;
        this.internalName = internalName;
        this.url = url;
        this.notes = new ArrayList<>();
    }
    /**
     * Adds a note to the collection.
     *
     * @param note the note to be added to the collection
     */
    public void addNote(Note note) {
        notes.add(note);
    }


    /**
     * gets the internalName of note collection
     *
     * @return the internalName of note collection
     */
    public String getInternalName() {
        return internalName;
    }

    /**
     * gets the url of the note collection
     *
     * @return returns the url of note collection
     */
    public String getUrl() {
        return url;
    }

    /**
     * Gets the ID of the note collection.
     *
     * @return the ID of the note collection
     */
    public long getId() {
        return id;
    }

    /**
     * Gets the title of the note collection.
     *
     * @return the title of the note collection
     */
    public String getTitle() {
        return title;
    }

    /**
     * Gets the list of notes in the collection.
     *
     * @return the list of notes in the collection
     */
    public List<Note> getNotes() {
        return notes;
    }


    /**
     * Sets the ID of the note collection.
     *
     * @param id the new ID to set for the note collection
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * sets the title of the collection
     * @param title title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Compares this NoteCollection object with the specified object for equality.
     * Two NoteCollection objects are considered equal if they have the same
     * title, internalName, URL, and notes list.
     *
     * @param o the object to compare this NoteCollection with
     * @return true if the objects are considered equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NoteCollection that = (NoteCollection) o;
        return Objects.equals(title, that.title) &&
                Objects.equals(internalName, that.internalName) &&
                Objects.equals(url, that.url) &&
                Objects.equals(notes, that.notes);
    }

    /**
     * sets the collection of all notes in this
     * collection to this collection
     */
    public void updateNotes() {
        for(Note note : notes) {
            note.setNoteCollection(this);
        }
    }

    /**
     * Returns a hash code value for the NoteCollection object.
     * The hash code is calculated using the title, internalName, URL, and notes list.
     *
     * @return the hash code value for this NoteCollection object
     */
    @Override
    public int hashCode() {
        return Objects.hash(title, internalName, url, notes);
    }


    /**
     * Returns a string representation of the note collection, which is the title of the collection.
     *
     * @return the title of the note collection as a String
     */
    @Override
    public String toString() {
        return title;
    }

    /**
     * removes a note from the collection by id
     * @param id the id of the note to be removed
     */
    public void removeNote(long id) {
        notes.removeIf(note -> note.getId() == id);
    }
}