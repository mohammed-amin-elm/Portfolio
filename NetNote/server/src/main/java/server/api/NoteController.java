package server.api;

import commons.Note;
import commons.NoteCollection;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.NoteCollectionService;
import server.NoteService;

import java.util.List;

/*
 * -------------------------------------------------------------
 * | JAVA DOC comments are generated with ChatGPT              |
 * -------------------------------------------------------------
 */

/**
 * This class serves as a REST controller for managing notes.
 * It handles HTTP requests related to note operations.
 */
@RestController
@RequestMapping("/api/note")
public class NoteController {

    private final NoteService noteService;
    private final NoteCollectionService noteCollectionService;

    /**
     * Constructs a new NoteController with the specified NoteService and NoteCollectionService.
     *
     * @param noteService           the service used for note-related operations
     * @param noteCollectionService the service used for note collection-related operations
     */
    public NoteController(NoteService noteService,
                          NoteCollectionService noteCollectionService) {
        this.noteService = noteService;
        this.noteCollectionService = noteCollectionService;
    }

    /**
     * Handles GET requests to retrieve a single note.
     *
     * @return the retrieved Note object
     */
    @GetMapping("/")
    public List<Note> getNotes() {
        return noteService.getNotes();
    }


    /**
     * Retrieves a note by its unique identifier.
     *
     * @param id the unique identifier of the note to retrieve
     * @return a ResponseEntity containing the
     * retrieved Note object or a Not Found status if the note is not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Note> getById(@PathVariable long id) {
        return noteService.getNoteById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Adds a new note to the specified note collection.
     *
     * @param note the Note object containing note details to be added
     * @param id   the ID of the note collection to which the note will be added
     */
    @PostMapping("/{id}")
    public void addNote(@RequestBody Note note,
                        @PathVariable long id) {
        if(noteCollectionService.getNoteCollectionById(id).isEmpty())
            return;

        NoteCollection noteCollection = noteCollectionService
                .getNoteCollectionById(id).get();
        note.setNoteCollection(noteCollection);
        noteService.addNote(note);

    }

    /**
     * Adds a new note to the repository.
     *
     * @param note the Note object to be added to the repository
     * @param id   the id of the Note we want updated
     */
    @PutMapping("/{id}")
    public void updateNote(@PathVariable("id") Long id,
                           @RequestBody Note note) {
        noteService.updateNote(id, note);
    }

    /**
     * Deletes a note identified by its unique ID.
     *
     * @param id the unique identifier of the note to delete
     */
    @DeleteMapping("/{id}")
    public void deleteNote(@PathVariable long id) {
        noteService.deleteNote(id);
    }
}
