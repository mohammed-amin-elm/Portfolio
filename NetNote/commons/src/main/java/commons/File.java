package commons;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * Entity class representing a file associated with a note.
 * Contains metadata about the file, such as its name, content type, and the note it is linked to.
 */
@Entity
public class File {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String fileName;
    private String contentType;
    private long size;
    private ZonedDateTime dateAdded;

    @ManyToOne
    @JoinColumn(
            name = "note_id",
            referencedColumnName = "id"
    )
    @JsonBackReference
    private Note note;

    /**
     * Default constructor for JPA.
     * Required for the framework to create instances of this entity.
     */
    protected File() {}

    /**
     * Constructs a new File entity with the given file name, content type, and associated note.
     *
     * @param fileName    the name of the file.
     * @param contentType the MIME type of the file.
     * @param note        the note associated with this file.
     */
    public File(String fileName, String contentType, Note note) {
        this.fileName = fileName;
        this.contentType = contentType;
        this.note = note;
    }

    /**
     * Json creator
     * @param fileName fileName
     * @param contentType contentType
     */
    @JsonCreator
    public File(
            @JsonProperty("fileName") String fileName,
            @JsonProperty("contentType") String contentType
    ) {
        this.fileName = fileName;
        this.contentType = contentType;
    }

    /**
     * Gets the ID of the file.
     *
     * @return the unique identifier for this file.
     */
    public long getId() {
        return id;
    }

    /**
     * Sets the ID of the file.
     *
     * @param id the unique identifier to set.
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Gets the content type of the file.
     *
     * @return the MIME type of the file.
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * Sets the content type of the file.
     *
     * @param contentType the MIME type to set.
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /**
     * Gets the name of the file.
     *
     * @return the name of the file.
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Sets the name of the file.
     *
     * @param fileName the name of the file to set.
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Gets the parent Note
     * @return The parent Note entity
     */
    public Note getNote() {
        return note;
    }

    /**
     * Set the Note
     * @param note The note
     */
    public void setNote(Note note) {
        this.note = note;
    }

    /**
     * Returns the size of the file
     * @return the size of the file in bytes
     */
    public long getSize() {
        return size;
    }

    /**
     * Sets the size of the file
     * @param size the size of the file in bytes
     */
    public void setSize(long size) {
        this.size = size;
    }

    /**
     * Returns the date that the file was added
     * @return the date that the file was added
     */
    public ZonedDateTime getDateAdded() {
        return dateAdded;
    }

    /**
     * @param dateAdded the
     */
    public void setDateAdded(ZonedDateTime dateAdded) {
        this.dateAdded = dateAdded;
    }

    /**
     * Checks if this file is equal to another object.
     *
     * @param o the object to compare with.
     * @return true if both objects are equal, otherwise false.
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        File file = (File) o;
        return Objects.equals(fileName, file.fileName) &&
                Objects.equals(contentType, file.contentType) &&
                size == file.getSize();
    }

    /**
     * Computes a hash code for this file.
     *
     * @return a hash code value for the file.
     */
    @Override
    public int hashCode() {
        return Objects.hash(fileName, contentType, size);
    }
}
