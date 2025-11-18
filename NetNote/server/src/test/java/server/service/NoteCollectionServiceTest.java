package server.service;

import commons.NoteCollection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import server.NoteCollectionService;
import server.database.NoteCollectionRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * All comments generated with ChatGPT
 */
class NoteCollectionServiceTest {

    @Mock
    private NoteCollectionRepository noteCollectionRepository;

    @InjectMocks
    private NoteCollectionService noteCollectionService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * This test verifies that the addNoteCollection method correctly calls
     * the save method in the repository.
     */
    @Test
    void addNoteCollection() {
        NoteCollection noteCollection = new NoteCollection("NoteCol", "NoteCol", "NoteCol");
        noteCollectionService.addNoteCollection(noteCollection);
        verify(noteCollectionRepository, times(1)).save(noteCollection);
    }

    /**
     * This test verifies that the getNoteCollections method returns all
     * entities in the repository.
     */
    @Test
    void getNoteCollections() {
        NoteCollection noteCollection1 = new NoteCollection("NoteCol", "NoteCol", "NoteCol");
        NoteCollection noteCollection2 = new NoteCollection("NoteCol", "NoteCol", "NoteCol");
        NoteCollection noteCollection3 = new NoteCollection("NoteCol", "NoteCol", "NoteCol");
        when(noteCollectionRepository.findAll()).thenReturn(List.of(noteCollection1, noteCollection2, noteCollection3));

        List<NoteCollection> result = noteCollectionService.getNoteCollections();

        assertEquals(3, result.size());
        assertTrue(result.contains(noteCollection1));
        assertTrue(result.contains(noteCollection2));
        assertTrue(result.contains(noteCollection3));
        verify(noteCollectionRepository, times(1)).findAll();
    }

    /**
     * This test verifies that the getNoteCollectionById method correctly
     * retrieves an existing entity.
     */
    @Test
    void getNoteCollectionById_Found() {
        long id = 1L;
        NoteCollection noteCollection = new NoteCollection("NoteCol", "NoteCol", "NoteCol");
        when(noteCollectionRepository.findById(id)).thenReturn(Optional.of(noteCollection));

        Optional<NoteCollection> result = noteCollectionService.getNoteCollectionById(id);

        assertTrue(result.isPresent());
        assertEquals(noteCollection, result.get());
        verify(noteCollectionRepository, times(1)).findById(id);
    }

    /**
     * This test verifies that the getNoteCollectionById method correctly
     * returns an empty Optional when the entity does not exist.
     */
    @Test
    void getNoteCollectionById_NotFound() {
        long id = 1L;
        when(noteCollectionRepository.findById(id)).thenReturn(Optional.empty());

        Optional<NoteCollection> result = noteCollectionService.getNoteCollectionById(id);

        assertTrue(result.isEmpty());
        verify(noteCollectionRepository, times(1)).findById(id);
    }

    /**
     * This test verifies that the deleteNoteCollection method correctly
     * deletes an existing entity.
     */
    @Test
    void deleteNoteCollection_Exists() {
        long id = 1L;
        when(noteCollectionRepository.existsById(id)).thenReturn(true);

        boolean result = noteCollectionService.deleteNoteCollection(id);

        assertTrue(result);
        verify(noteCollectionRepository, times(1)).existsById(id);
        verify(noteCollectionRepository, times(1)).deleteById(id);
    }

    /**
     * This test verifies that the deleteNoteCollection method returns false
     * when the entity does not exist.
     */
    @Test
    void deleteNoteCollection_NotExists() {
        long id = 1L;
        when(noteCollectionRepository.existsById(id)).thenReturn(false);

        boolean result = noteCollectionService.deleteNoteCollection(id);

        assertFalse(result);
        verify(noteCollectionRepository, times(1)).existsById(id);
        verify(noteCollectionRepository, never()).deleteById(id);
    }

    /**
     * This test verifies that the updateNoteCollection method successfully updates
     * an existing entity and sets the correct ID.
     */
    @Test
    void updateNoteCollection_Exists() {
        long id = 1L;
        NoteCollection existingCollection = new NoteCollection("NoteCol", "NoteCol", "NoteCol");
        existingCollection.setId(id);

        NoteCollection updatedCollection = new NoteCollection("NoteCol", "NoteCol", "NoteCol");
        when(noteCollectionRepository.existsById(id)).thenReturn(true);

        boolean result = noteCollectionService.updateNoteCollection(id, updatedCollection);

        assertTrue(result);
        assertEquals(id, updatedCollection.getId());
        verify(noteCollectionRepository, times(1)).existsById(id);
        verify(noteCollectionRepository, times(1)).save(updatedCollection);
    }

    /**
     * This test verifies that the updateNoteCollection method returns false
     * when the entity does not exist.
     */
    @Test
    void updateNoteCollection_NotExists() {
        long id = 1L;
        NoteCollection updatedCollection = new NoteCollection("NoteCol", "NoteCol", "NoteCol");
        when(noteCollectionRepository.existsById(id)).thenReturn(false);

        boolean result = noteCollectionService.updateNoteCollection(id, updatedCollection);

        assertFalse(result);
        verify(noteCollectionRepository, times(1)).existsById(id);
        verify(noteCollectionRepository, never()).save(updatedCollection);
    }
}
