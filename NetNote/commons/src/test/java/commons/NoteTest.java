/*
 * -------------------------------------------------------------
 * | JAVA DOC comments are generated with ChatGPT              |
 * -------------------------------------------------------------
 */

package commons;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class NoteTest {

    /**
     * Tests the setter method for the ID of the Note object.
     * Verifies that the ID is set correctly.
     */
    @Test
    public void testSetId() {
        Note note = new Note("title", "content", null);
        note.setId(1);
        assertEquals(1, note.getId());
    }

    /**
     * Tests the setter method for the title of the Note object.
     * Verifies that the title is set correctly.
     */
    @Test
    public void testSetTitle() {
        Note note = new Note("title", "content", null);
        note.setTitle("title1");
        assertEquals("title1", note.getTitle());
    }

    /**
     * Tests the setter method for the content of the Note object.
     * Verifies that the content is set correctly.
     */
    @Test
    public void testSetContent() {
        Note note = new Note("title", "content", null);
        note.setContent("content1");
        assertEquals("content1", note.getContent());
    }

    /**
     * Tests the update method of the Note object.
     * Verifies that the content and title are updated correctly from another Note object.
     */
    @Test
    public void testUpdateNote() {
        Note note = new Note("title", "content", null);
        Note note2 = new Note("title2", "content2", null);
        note.updateNote(note2);
        assertEquals("title2", note.getTitle());
        assertEquals("content2", note.getContent());
    }

    /**
     * Tests the setter method for the NoteCollection associated with the Note object.
     * Verifies that the collection is set correctly.
     */
    @Test
    public void testSetNoteCollection() {
        Note note = new Note("title", "content", null);
        NoteCollection noteCollection = new NoteCollection("title", "internalName", "url");
        note.setNoteCollection(noteCollection);
        assertEquals(note.getCollection(), noteCollection);
    }

    /**
     * Tests the `equals` method of the Note object for inequality.
     * Verifies that two Note objects with different properties are not considered equal.
     */
    @Test
    public void testNotEquals() {
        Note note = new Note("title", "content", null);
        Note note2 = new Note("title2", "content2", null);
        assertNotEquals(note, note2);
    }

    /**
     * Tests the `equals` method of the Note object for equality.
     * Verifies that two Note objects with the same properties are considered equal.
     */
    @Test
    public void testEquals() {
        Note note = new Note("title", "content", null);
        Note note2 = new Note("title", "content", null);
        assertEquals(note, note2);
    }

    /**
     * Tests the `hashCode` method of the Note object when objects are not equal.
     * Verifies that the hash codes of two different Note objects are not the same.
     */
    @Test
    public void testHashCodeNotEqual() {
        Note note = new Note("title", "content", null);
        Note note2 = new Note("title2", "content2", null);
        assertNotEquals(note.hashCode(), note2.hashCode());
    }

    /**
     * Tests the `hashCode` method of the Note object when objects are equal.
     * Verifies that the hash codes of two equal Note objects are the same.
     */
    @Test
    public void testHashCodeEqual() {
        Note note = new Note("title", "content", null);
        Note note2 = new Note("title", "content", null);
        assertEquals(note.hashCode(), note2.hashCode());
    }

    /**
     * Tests the `toString` method of the Note object.
     * Verifies that the string representation of the Note object is the title of the note.
     */
    @Test
    public void testToString() {
        Note note = new Note("title", "content", null);
        assertEquals("title", note.toString());
    }

}
