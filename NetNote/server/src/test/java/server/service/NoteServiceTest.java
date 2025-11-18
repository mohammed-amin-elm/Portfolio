package server.service;

import commons.Note;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import server.NoteService;
import server.database.NoteRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NoteServiceTest {
    @Mock
    private NoteRepository noteRepository;

    @InjectMocks
    private NoteService noteService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAddNote() {
        Note testNote = new Note("test","testing content",null);
        noteService.addNote(testNote);
        verify(noteRepository, times(1)).save(testNote);
    }

    @Test
    public void testDeleteNote_ReturnsTrue() {
        long id = 1;

        noteService.deleteNote(id);

        when(noteRepository.existsById(id)).thenReturn(true);

        boolean test = noteService.deleteNote(id);

        verify(noteRepository, times(2)).existsById(id);
        verify(noteRepository, times(1)).deleteById(id);

        assertTrue(test);
    }

    @Test
    public void testDeleteNote_ReturnsFalse() {
        long id = 1;

        noteService.deleteNote(id);

        when(noteRepository.existsById(id)).thenReturn(false);

        boolean test = noteService.deleteNote(id);
        verify(noteRepository, times(2)).existsById(id);
        verify(noteRepository, never()).deleteById(id);
    }

    @Test
    public void testUpdateNote_ReturnsTrue() {
        int id = 1;
        Note testNote = new Note("test","testing content",null);
        testNote.setId(id);

        noteService.addNote(testNote);
        Note updatedNote = new Note("updated","updated content",null);
        when(noteRepository.findById(1L)).thenReturn(Optional.of(testNote));

        boolean test = noteService.updateNote(1,updatedNote);

        assertTrue(test);
        verify(noteRepository, times(1)).findById((long) id);
        verify(noteRepository, times(2)).save(testNote);
    }

    @Test
    public void testUpdateNote_ReturnsFalse() {
        int id = 1;
        Note testNote = new Note("updated","updated content",null);
        testNote.setId(id);

        noteService.addNote(testNote);
        when(noteRepository.findById(1L)).thenReturn(Optional.empty());

        boolean test = noteService.updateNote(1,testNote);
        assertFalse(test);
        verify(noteRepository, times(1)).findById((long) id);
        verify(noteRepository, times(1)).save(any(Note.class));
    }

    @Test
    public void testGetNote_List() {
        Note testNote = new Note("test","testing content",null);
        testNote.setId(1);
        noteService.addNote(testNote);
        Note testNote2 = new Note("test","testing content2",null);
        List<Note> Notes = List.of(testNote,testNote2);

        when(noteRepository.findAll()).thenReturn(Notes);

        List<Note> testList = noteService.getNotes();

        assertEquals(Notes, testList);
        verify(noteRepository, times(1)).findAll();
    }

    @Test
    public void testGetNoteById_ReturnsNote() {
        Note testNote = new Note("test","testing content",null);
        when(noteRepository.findById(1L)).thenReturn(Optional.of(testNote));

        Optional<Note> testOptional = noteService.getNoteById(1L);
        assertTrue(testOptional.isPresent());
        assertEquals(testNote, testOptional.get());
        verify(noteRepository, times(1)).findById(1L);
    }

    @Test
    public void testGetNoteById_ReturnsEmptyOptional() {
        when(noteRepository.findById(1L)).thenReturn(Optional.empty());
        Optional<Note> testOptional = noteService.getNoteById(1L);

        assertFalse(testOptional.isPresent());
        verify(noteRepository, times(1)).findById(1L);
    }

}
