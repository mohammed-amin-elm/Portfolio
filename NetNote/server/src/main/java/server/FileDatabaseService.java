package server;

import commons.File;
import commons.Note;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.database.FileRepository;

import java.util.List;
import java.util.Optional;

/**
 * Service class for managing file data in the database.
 * Provides methods for adding, retrieving, and deleting files.
 */
@Service
public class FileDatabaseService {

    private final FileRepository fileRepository;

    /**
     * Constructor for initializing the FileDatabaseService with the required repository.
     *
     * @param fileRepository the repository for file entities.
     */
    @Autowired
    public FileDatabaseService(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    /**
     * Adds a file entry to the database.
     *
     * @param file the file entity to add.
     */
    public void addFile(File file) {
        fileRepository.save(file);
    }

    /**
     * Retrieves all file entries from the database.
     *
     * @return a list of all file entities.
     */
    public List<File> getAllFiles() {
        return fileRepository.findAll();
    }

    /**
     * Retrieves a file entry by its ID.
     *
     * @param id the ID of the file to retrieve.
     * @return an {@code Optional} containing the file entity if found, or empty if not.
     */
    public Optional<File> getFileById(long id) {
        return fileRepository.findById(id);
    }

    /**
     * Deletes a file entry from the database by its ID.
     *
     * @param id the ID of the file to delete.
     */
    public void deleteFile(long id) {
        fileRepository.deleteById(id);
    }


    /**
     * Updates a file entry from the database by its ID.
     *
     * @param id The id of the file to update.
     * @param file The file containing the content to be updated.
     * @return A boolean indicating if it was a success or not.
     */
    public boolean updateFile(long id, File file) {

        if(!fileRepository.existsById(id))
            return false;

        file.setId(id);
        fileRepository.save(file);
        return true;
    }

    /**
     * Retrieves a file entry by its name
     * @param note The note containing the file
     * @param name The name of the file to find
     * @return An optional containing the file entity if found, or empty if not.
     */
    public Optional<File> getFileByNoteAndName(Note note, String name) {
        return note.getFiles().stream().filter(x -> x.getFileName().equals(name)).findFirst();
    }
}
