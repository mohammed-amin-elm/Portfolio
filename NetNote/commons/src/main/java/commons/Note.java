/*
 * -------------------------------------------------------------
 * | JAVA DOC comments are generated with ChatGPT              |
 * -------------------------------------------------------------
 */

package commons;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a note with a unique ID, title, content, and an associated note collection.
 * This entity is meant to be stored in a database and is linked to a NoteCollection.
 */

@Entity
public class Note {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonProperty("id")
    private long id;
    @JsonProperty("title")
    private String title;

    @JsonProperty("content")
    @Lob
    private String content;

    @OneToMany(
            mappedBy = "note",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JsonManagedReference
    private List<File> files;

    @ManyToOne
    @JoinColumn(
            name = "collection_id",
            referencedColumnName = "id"
    )
    @JsonBackReference
    private NoteCollection collection;

    /**
     * protected Constructor to instantiate the Note object
     */
    protected Note() {}

    /**
     * json creator
     * @param title title
     * @param id id
     * @param content content
     * @param files files
     */
    @JsonCreator
    public Note(@JsonProperty("title") String title,
                @JsonProperty("id") long id,
                @JsonProperty("content") String content,
                @JsonProperty("files") List<File> files
                ) {
        this.title = title;
        this.id = id;
        this.content = content;
        this.files = files;
    }

    /**
     * Constructs a new Note object with the specified ID, title, content, and collection.
     *
     * @param title the title of the note
     * @param content the content of the note
     * @param collection the collection to which the note belongs
     */
    public Note(String title, String content, NoteCollection collection) {
        this.title = title;
        this.content = content;
        this.collection = collection;
        this.files = new ArrayList<>();
    }


    /**
     * Gets the ID of the note.
     *
     * @return the ID of the note
     */
    public long getId() {
        return id;
    }

    /**
     * Set ID of the note
     * @param id The new ID
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Gets the title of the note.
     *
     * @return the title of the note
     */
    public String getTitle() {
        return title;
    }

    /**
     * Gets the content of the note.
     *
     * @return the content of the note
     */
    public String getContent() {
        return content;
    }

    /**
     * Gets the collection to which the note belongs.
     *
     * @return returns the collection of the note
     */
    public NoteCollection getCollection() {return collection;}

    /**
     * Get all the files belonging tot the current note
     * @return An ArrayList with the file entities.
     */
    public List<File> getFiles() {
        return files;
    }

    /**
     * Compares this Note object with the specified object for equality.
     * Two Note objects are considered equal if they have the same
     * title, content, files, and collection.
     *
     * @param o the object to compare this Note with
     * @return true if the objects are considered equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Note note = (Note) o;
        return Objects.equals(title, note.title) &&
                Objects.equals(content, note.content) &&
                Objects.equals(files, note.files);
    }

    /**
     * Returns a hash code value for the Note object.
     * The hash code is calculated using the title, content, files, and collection.
     *
     * @return the hash code value for this Note object
     */
    @Override
    public int hashCode() {
        return Objects.hash(title, content, files, collection);
    }


    /**
     * Returns a string representation of the note, which is the title of the note.
     *
     * @return the title of the note as a String
     */
    @Override
    public String toString() {
        return title;
    }


    /**
     * Sets the NoteCollection for this note.
     *
     * @param noteCollection the NoteCollection to which this note should be associated
     */
    public void setNoteCollection(NoteCollection noteCollection) {
        this.collection = noteCollection;
    }

    /**
     * Updates the current note's title, content,
     * and collection with the values from the provided note.
     *
     * @param note the note object containing updated title, content, and collection information
     */
    public void updateNote(Note note) {
        this.title = note.getTitle();
        this.content = note.getContent();
    }

    /**
     * sets the collection of all notes in this
     * collection to this collection
     */
    public void updateFiles() {
        for(File file : files) {
            file.setNote(this);
        }
    }

    /**
     * setter for the title field
     * @param title new title for note
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * setter for the content
     * @param newContent content
     */
    public void setContent(String newContent) {
        this.content = newContent;
    }
}