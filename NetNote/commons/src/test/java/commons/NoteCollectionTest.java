/*
 * -------------------------------------------------------------
 * | JAVA DOC comments are generated with ChatGPT              |
 * -------------------------------------------------------------
 */

package commons;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class NoteCollectionTest {

    /**
     * Tests the functionality of adding a note to the NoteCollection.
     * Verifies that the note is added to the collection correctly.
     */
    @Test
    public void testAddNote() {
        NoteCollection noteCollection = new NoteCollection("title", "internalName", "url");
        Note note = new Note("title", "content", null);
        noteCollection.addNote(note);
        List<Note> notes = noteCollection.getNotes();
        assertEquals(1, notes.size());
        assertEquals(note, notes.get(0));
    }

    /**
     * Tests the functionality of removing a note from the NoteCollection by its ID.
     * Verifies that the note is removed correctly from the collection.
     */
    @Test
    public void testRemoveNote() {
        NoteCollection noteCollection = new NoteCollection("title", "internalName", "url");
        Note note1 = new Note("title", "content", null);
        note1.setId(1);
        noteCollection.addNote(note1);
        noteCollection.removeNote(1);
        List<Note> notes = noteCollection.getNotes();
        assertEquals(0, notes.size());

    }

    /**
     * Tests the setter method for the ID of the NoteCollection.
     * Verifies that the ID is set correctly.
     */
    @Test
    public void testSetId() {
        NoteCollection noteCollection = new NoteCollection("title", "internalName", "url");
        noteCollection.setId(1);
        assertEquals(1, noteCollection.getId());
    }

    /**
     * Tests the setter method for the title of the NoteCollection.
     * Verifies that the title is set correctly.
     */
    @Test
    public void testSetTitle() {
        NoteCollection noteCollection = new NoteCollection("title", "internalName", "url");
        noteCollection.setTitle("title1");
        assertEquals("title1", noteCollection.getTitle());
    }

    /**
     * Tests the `equals` method of the NoteCollection class for inequality.
     * Verifies that two NoteCollection objects with different properties are not considered equal.
     */
    @Test
    public void testNotEquals() {
        NoteCollection noteCollection1 = new NoteCollection("title1", "internalName1", "url1");
        NoteCollection noteCollection2 = new NoteCollection("title2", "internalName2", "url2");
        assertNotEquals(noteCollection1, noteCollection2);
    }

    /**
     * Tests the `equals` method of the NoteCollection class for equality.
     * Verifies that two NoteCollection objects with the same properties are considered equal.
     */
    @Test
    public void testEquals() {
        NoteCollection noteCollection1 = new NoteCollection("title", "internalName", "url");
        NoteCollection noteCollection2 = new NoteCollection("title", "internalName", "url");
        assertEquals(noteCollection1, noteCollection2);
    }

    /**
     * Tests the `hashCode` method of the NoteCollection class when objects are not equal.
     * Verifies that the hash codes of two different NoteCollection objects are not the same.
     */
    @Test
    public void testHashCodeNotEqual() {
        NoteCollection noteCollection1 = new NoteCollection("title1", "internalName1", "url1");
        NoteCollection noteCollection2 = new NoteCollection("title2", "internalName2", "url2");
        assertNotEquals(noteCollection1.hashCode(), noteCollection2.hashCode());
    }

    /**
     * Tests the `hashCode` method of the NoteCollection class when objects are equal.
     * Verifies that the hash codes of two equal NoteCollection objects are the same.
     */
    @Test
    public void testHashCodeEqual() {
        NoteCollection noteCollection1 = new NoteCollection("title", "internalName", "url");
        NoteCollection noteCollection2 = new NoteCollection("title", "internalName", "url");
        assertEquals(noteCollection1.hashCode(), noteCollection2.hashCode());
    }

    /**
     * Tests the `toString` method of the NoteCollection class.
     * Verifies that the string representation of the NoteCollection is the title of the collection.
     */
    @Test
    public void testToString() {
        NoteCollection noteCollection = new NoteCollection("title", "internalName", "url");
        assertEquals("title", noteCollection.toString());
    }
}
