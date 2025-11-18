/*
 * -------------------------------------------------------------
 * | JAVA DOC comments are generated with ChatGPT              |
 * -------------------------------------------------------------
 */
package client.utils;

import commons.Note;
import commons.NoteCollection;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.client.ClientConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ServerUtilsTest {

    private ServerUtils utils;
    @Mock
    private ServerUtils serverUtils;
    @Mock
    private Client mockClient;
    @Mock
    private WebTarget mockWebTarget;
    @Mock
    private Invocation.Builder mockBuilder;
    @Mock
    private Response mockResponse;

    @BeforeEach
    void setup() {
        utils = new ServerUtils();
    }

    /**
     * Tests if the server is available by invoking the `isServerAvailable` method.
     *
     * @see ServerUtils#isServerAvailable()
     */
    @Test
    void isServerAvailable() {
        assertTrue(utils.isServerAvailable());
    }

    /**
     * Tests the retrieval of all note collections using the `getAllCollections` method.
     *
     * @see ServerUtils#getAllCollections()
     */
    @Test
    void getAllCollections() {
        List<NoteCollection> collection = utils.getAllCollections();
        assertNotNull(collection);
    }

    /**
     * Tests adding a new note collection and then deleting it. Verifies that the collection
     * is properly added and subsequently removed.
     *
     * @see ServerUtils#newNoteCollection(NoteCollection)
     * @see ServerUtils#deleteNoteCollection(long)
     */
    @Test
    void testAddDeleteNoteCollection() {
        NoteCollection noteCollection = new NoteCollection("TITLE", "INTERNALNAME", "URL");
        utils.newNoteCollection(noteCollection);

        NoteCollection lastNoteCollection = utils.getAllCollections().getLast();

        long collectionId = lastNoteCollection.getId();
        noteCollection.setId(collectionId);

        assertEquals(lastNoteCollection, noteCollection);

        utils.deleteNoteCollection(collectionId);
        if (utils.getAllCollections().isEmpty()) return;

        assertNotEquals(utils.getAllCollections().getLast(), lastNoteCollection);
    }

    /**
     * Tests the retrieval of all notes using the `getNotes` method.
     *
     * @see ServerUtils#getNotes()
     */
    @Test
    void getNotes() {
        List<Note> notes = utils.getNotes();
        assertNotNull(notes);
    }

    /**
     * Tests if a note collection with a specified internal name exists.
     *
     * @see ServerUtils#containsInternalName(String)
     */
    @Test
    void containsInternalName() {
        String internalName = "internalName";
        NoteCollection noteCollection = new NoteCollection("TITLE", "internalName", "URL");
        utils.newNoteCollection(noteCollection);
        assertTrue(utils.containsInternalName(internalName));
    }

    /**
     * Tests if a note collection with a non-existing internal name is correctly identified.
     *
     * @see ServerUtils#containsInternalName(String)
     */
    @Test
    void doesntContainInternalName() {
        String internalName = "testInternalName";
        NoteCollection noteCollection = new NoteCollection("TITLE", "internalName", "URL");
        utils.newNoteCollection(noteCollection);
        assertFalse(utils.containsInternalName(internalName));
    }

    /**
     * Tests if a partial match for an internal name is correctly identified.
     *
     * @see ServerUtils#containsInternalName(String)
     */
    @Test
    void partialInternalName() {
        String internalName = "internalName";
        NoteCollection noteCollection = new NoteCollection("TITLE", "internalName", "URL");
        utils.newNoteCollection(noteCollection);
        assertTrue(utils.containsInternalName(internalName));
    }

    /**
     * Tests if a note collection contains the specified URL.
     *
     * @see ServerUtils#containsUrl(String)
     */
    @Test
    void containsURL() {
        NoteCollection noteCollection = new NoteCollection("TITLE", "INTERNALNAME", "URL");
        utils.newNoteCollection(noteCollection);
        assertTrue(utils.containsUrl("URL"));
    }

    /**
     * Tests if a non-existing URL is correctly identified.
     *
     * @see ServerUtils#containsUrl(String)
     */
    @Test
    @Disabled
    void notContainsURL() {
        NoteCollection noteCollection = new NoteCollection("TITLE", "INTERNALNAME", "URL");
        utils.newNoteCollection(noteCollection);
        List<NoteCollection> allCollections = utils.getAllCollections();
        System.out.println("All collections: " + allCollections);
        assertTrue(allCollections.contains(noteCollection), "The new collection was not added properly.");
        assertFalse(utils.containsUrl("Example"), "Expected 'containsUrl' to return false for a non-matching URL.");
    }

    /**
     * Tests if a partially matching URL is correctly identified.
     *
     * @see ServerUtils#containsUrl(String)
     */
    @Test
    void partiallyContainsURL() {
        NoteCollection noteCollection = new NoteCollection("TITLE", "INTERNALNAME", "01URL23");
        utils.newNoteCollection(noteCollection);
        assertTrue(utils.containsUrl("01URL23"));
    }

    /**
     * Disabled test for updating a note within a note collection.
     *
     * @see ServerUtils#updateNote(long, Note)
     */
    @Test
    @Disabled
    void updateNote() {
        NoteCollection noteCollection = new NoteCollection("TITLE", "INTERNALNAME", "URL");
        noteCollection.setId(10);
        Note note = new Note("example", "example", noteCollection);
        Note note1 = new Note("title", "content", null);
        long id = note.getId();
        utils.newNoteCollection(noteCollection);
        utils.updateNote(id, note1);
        assertTrue(utils.getNote(id).getTitle().equals("title"));
    }

    /**
     * Disabled test for updating a note collection and verifying its title.
     *
     * @see ServerUtils#updateNoteCollection(long, NoteCollection)
     */
    @Test
    @Disabled
    void testUpdateNoteCollection() {
        NoteCollection collection = utils.getAllCollections().get(0);
        long collectionId = collection.getId();

        String newTitle = "Updated Title";
        collection.setTitle(newTitle);

        utils.updateNoteCollection(collectionId, collection);

        NoteCollection updatedCollection = utils.getNoteCollectionById(collectionId);
        assertEquals(newTitle, updatedCollection.getTitle(), "Title should be updated.");
    }
    /**
     * Tests if a note collection contains the specified title.
     *
     * @see ServerUtils#containsTitle(String)
     */
    @Test
    void containsTitle() {
        NoteCollection noteCollection = new NoteCollection("Sample Title", "INTERNALNAME", "URL");
        utils.newNoteCollection(noteCollection);
        assertTrue(utils.containsTitle("Sample Title"));
    }

    /**
     * Tests deleting a note collection and verifying it no longer exists.
     *
     * @see ServerUtils#deleteNoteCollection(long)
     */
    @Test
    void deleteNoteCollection() {
        NoteCollection noteCollection = new NoteCollection("Temporary Title", "TempInternalName", "TempURL");
        utils.newNoteCollection(noteCollection);
        NoteCollection createdCollection = utils.getAllCollections().get(utils.getAllCollections().size() - 1);
        long collectionId = createdCollection.getId();
        assertTrue(utils.containsInternalName("TempInternalName"));
        utils.deleteNoteCollection(collectionId);
        assertFalse(utils.containsInternalName("TempInternalName"));
    }


    /**
     * Tests whether it receives all titles and properly puts them in one list.
     *
     * @see ServerUtils#getAllTitles()
     */
    @Test
    void getAllTitles() {
        List<Note> allNotes = utils.getNotes();
        List<NoteCollection> allCollections = utils.getAllCollections();
        int total = allNotes.size() + allCollections.size();

        List<String> noteTitles = allNotes.stream()
                .map(n -> n.getTitle())
                .toList();
        List<String> collectionTitles = allNotes.stream()
                .map(c -> c.getTitle())
                .toList();
        List<String> allTitles = utils.getAllTitles();

        assertNotNull(allTitles);
        assertEquals(total, allTitles.size());
        assertTrue(allTitles.containsAll(noteTitles));
        assertTrue(allTitles.containsAll(collectionTitles));
    }

}
