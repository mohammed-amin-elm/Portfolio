package server;

import commons.Note;
import commons.NoteCollection;
import org.springframework.stereotype.Service;
import server.database.NoteCollectionRepository;

import java.util.List;
import java.util.Optional;

/*
 * -------------------------------------------------------------
 * | JAVA DOC comments are generated with ChatGPT              |
 * -------------------------------------------------------------
 */


/**
 * Service class for managing NoteCollection entities.
 */
@Service
public class NoteCollectionService {

    private final NoteCollectionRepository repo;
    private final FileStorageService fileStorageService;

    /**
     * Constructs a new NoteCollectionService with the specified NoteCollectionRepository.
     *
     * @param repo the NoteCollectionRepository to be used by this service
     * @param fileStorageService the FileStorageService instance
     */
    public NoteCollectionService(NoteCollectionRepository repo,
                                 FileStorageService fileStorageService) {
        this.repo = repo;
        this.fileStorageService = fileStorageService;
    }

    /**
     * Adds a new NoteCollection to the repository.
     *
     * @param noteCollection the NoteCollection entity
     *                       to be added to the repository
     */
    public void addNoteCollection(NoteCollection noteCollection) {
        repo.save(noteCollection);
    }

    /**
     * Deletes a NoteCollection entity identified by its ID.
     * If the NoteCollection entity exists, it will be deleted.
     *
     * @param id the ID of the NoteCollection to be deleted
     * @return true if the NoteCollection was successfully deleted,
     * false if the NoteCollection does not exist
     */
    public boolean deleteNoteCollection(long id) {
        if(!repo.existsById(id))
            return false;

        repo.deleteById(id);
        return true;
    }

    /**
     * Updates an existing NoteCollection entity in the repository.
     *
     * @param id the ID of the NoteCollection to update
     * @param noteCollection the NoteCollection entity
     *                      with updated information
     * @return true if the NoteCollection was successfully updated,
     *         false if no NoteCollection with the given ID exists
     */
    public boolean updateNoteCollection(long id, NoteCollection noteCollection) {
        if(!repo.existsById(id))
            return false;

        noteCollection.setId(id);
        noteCollection.updateNotes();
        noteCollection.getNotes().forEach(Note::updateFiles);

        repo.save(noteCollection);
        return true;
    }

    /**
     * Retrieves all note collections from the repository.
     *
     * @return a list of all NoteCollection entities
     */
    public List<NoteCollection> getNoteCollections() {
        return repo.findAll();
    }

    /**
     * Retrieves a NoteCollection entity by its ID.
     *
     * @param id the ID of the NoteCollection to retrieve
     * @return an Optional containing the found NoteCollection,
     * or an empty Optional if no NoteCollection is found
     */
    public Optional<NoteCollection> getNoteCollectionById(long id) {
        return repo.findById(id);
    }
}
