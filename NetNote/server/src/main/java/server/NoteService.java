package server;

import commons.Note;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.database.NoteRepository;

import java.util.List;
import java.util.Optional;

/*
 * -------------------------------------------------------------
 * | JAVA DOC comments are generated with ChatGPT              |
 * -------------------------------------------------------------
 */

/**
 * NoteService is a service class that provides operations for managing notes.
 */
@Service
public class NoteService {
    
    private final NoteRepository repo;
    private final FileStorageService fileStorageService;

    /**
     * NoteService is a service class that provides operations for managing notes.
     * @param repo repository to be injected
     * @param fileStorageService fileStorageService to be injected
     */
    @Autowired
    public NoteService(NoteRepository repo, FileStorageService fileStorageService) {
        this.repo = repo;
        this.fileStorageService = fileStorageService;
    }
    
    /**
     * Retrieves a list of all notes from the repository.
     *
     * @return a list of all notes
     */
    public List<Note> getNotes() {
        return repo.findAll();
    }

    /**
     * Retrieves a note from the repository using the given ID.
     *
     * @param id the unique identifier of the note to retrieve
     * @return an Optional containing the note if found, or an empty Optional if not
     */
    public Optional<Note> getNoteById(long id) {
        return repo.findById(id);
    }

    /**
     * Deletes a note from the repository using the given ID.
     *
     * @param id the unique identifier of the note to delete
     * @return true if the note was successfully deleted, false if the note does not exist
     */
    public boolean deleteNote(long id) {
        if(!repo.existsById(id))
            return false;

        repo.deleteById(id);
        return true;
    }

    /**
     * Adds a new note to the repository.
     *
     * @param note the note object to be added
     */
    public void addNote(Note note) {
        repo.save(note);
    }

    /**
     * Updates an existing note in the repository with the provided details.
     *
     * @param id the unique identifier of the note to update
     * @param note the note object containing updated information
     * @return true if the note was successfully updated, false if the note does not exist
     */
    public boolean updateNote(long id, Note note) {
        Note oldNote = repo.findById(id).orElse(null);

        if(oldNote == null)
            return false;

        oldNote.updateNote(note);
        oldNote.updateFiles();
        repo.save(oldNote);
        return true;
    }
}
