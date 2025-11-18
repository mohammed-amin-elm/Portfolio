package server.api;

import commons.NoteCollection;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.NoteCollectionService;

import java.util.List;

/*
 * -------------------------------------------------------------
 * | JAVA DOC comments are generated with ChatGPT              |
 * -------------------------------------------------------------
 */

/**
 * The NoteCollectionController class provides RESTful endpoints for managing
 * note collections, allowing clients to perform CRUD operations such as
 * retrieving all collections, retrieving a specific collection by ID,
 * adding a new collection, updating an existing collection, and deleting a collection.
 * This controller handles HTTP requests and responses related to note collections
 * and delegates the business logic to the NoteCollectionService.
 */
@RestController
@RequestMapping("/api/collection")
public class NoteCollectionController {
    private final NoteCollectionService noteCollectionService;

    /**
     * Constructs a new NoteCollectionController with the specified NoteCollectionService.
     *
     * @param noteCollectionService the NoteCollectionService to be used by this controller
     */
    public NoteCollectionController(NoteCollectionService noteCollectionService) {
        this.noteCollectionService = noteCollectionService;
    }

    /**
     * Retrieves all note collections.
     *
     * @return a list of all NoteCollection entities
     */
    @GetMapping(path = {"/", ""})
    public List<NoteCollection> getNoteCollection() {
        return noteCollectionService.getNoteCollections();
    }

    /**
     * Retrieves a NoteCollection entity by its ID.
     *
     * @param id the ID of the NoteCollection to retrieve
     * @return a ResponseEntity containing the found NoteCollection,
     * or a ResponseEntity with a 404 Not Found status if no NoteCollection is found
     */
    @GetMapping("/{id}")
    public ResponseEntity<NoteCollection> getNoteCollectionById(@PathVariable long id) {
        return noteCollectionService.getNoteCollectionById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Adds a new note collection to the repository.
     *
     * @param noteCollection the NoteCollection object containing the information
     *                       of the new note collection to be added
     */
    @PostMapping(path = {"/", ""})
    public void addNoteCollection(@RequestBody NoteCollection noteCollection) {
        noteCollectionService.addNoteCollection(noteCollection);
    }

    /**
     * Updates an existing NoteCollection with the given ID
     * using the details provided in the request body.
     *
     * @param id the ID of the NoteCollection to update
     * @param noteCollection the NoteCollection object containing updated details
     */
    @PutMapping("/{id}")
    public void updateNoteCollection(
            @PathVariable long id,
            @RequestBody NoteCollection noteCollection) {
        noteCollectionService.updateNoteCollection(id, noteCollection);
    }

    /**
     * Deletes an existing NoteCollection identified by its ID.
     *
     * @param id the ID of the NoteCollection to be deleted
     */
    @DeleteMapping("/{id}")
    public void deleteNoteCollection(@PathVariable long id) {
        noteCollectionService.deleteNoteCollection(id);
    }

}
