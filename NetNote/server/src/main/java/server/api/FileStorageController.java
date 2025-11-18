package server.api;

import commons.File;
import commons.Note;
import commons.NoteCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import server.NoteCollectionService;
import server.config.FileStorageConfig;
import server.FileDatabaseService;
import server.FileStorageService;
import server.NoteService;
import java.nio.file.Path;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing file storage operations.
 * Provides endpoints for uploading, retrieving, and deleting files.
 */
@RestController
@RequestMapping("/api/files")
public class FileStorageController {

    private final FileDatabaseService fileDatabaseService;
    private final FileStorageService fileStorageService;
    private final FileStorageConfig fileStorageConfig;
    private final NoteService noteService;
    private final NoteCollectionService noteCollectionService;

    /**
     * Constructor for initializing the FileStorageController with required services.
     *
     * @param fileDatabaseService the service for interacting with the file database.
     * @param fileStorageService the service for handling file storage on the filesystem.
     * @param fileStorageConfig the configuration for file storage paths.
     * @param noteService the service for managing notes associated with files.
     * @param noteCollectionService the service for managing noteCollections
     */
    @Autowired
    public FileStorageController(
            FileDatabaseService fileDatabaseService,
            FileStorageService fileStorageService,
            FileStorageConfig fileStorageConfig,
            NoteService noteService,
            NoteCollectionService noteCollectionService) {
        this.fileDatabaseService = fileDatabaseService;
        this.fileStorageService = fileStorageService;
        this.fileStorageConfig = fileStorageConfig;
        this.noteService = noteService;
        this.noteCollectionService = noteCollectionService;
    }

    /**
     * Retrieves all files stored in the database.
     *
     * @return a list of all file entities.
     */
    @GetMapping("/")
    public List<File> getAllFiles() {
        return fileDatabaseService.getAllFiles();
    }

    /**
     * Uploads a file and associates it with a specific note.
     * The file is saved to the local filesystem and a corresponding entry is added to the database.
     *
     * @param file the file to upload.
     * @param id the ID of the note to associate with the file.
     * @return Returns 400 Bad Request if a Note does not exist. 200 OK if everything went
     *  successfully 500 Internal Server Error if there is an exception.
     */
    @PostMapping("/{id}")
    public ResponseEntity<Void> uploadFile(@RequestParam("file") MultipartFile file,
                                           @PathVariable long id) {
        try {
            String fileName = file.getOriginalFilename();
            String contentType = file.getContentType();
            long fileSize = file.getSize();
            ZonedDateTime dateAdded = ZonedDateTime.now();

            Optional<Note> note = noteService.getNoteById(id);
            if(note.isEmpty()) return ResponseEntity.notFound().build();
            if(note.get().getCollection() == null) return ResponseEntity.notFound().build();
            if(fileName == null) return ResponseEntity.badRequest().build();

            System.out.println(fileName);
            System.out.println(note.get().getCollection().getTitle());
            System.out.println(note.get().getTitle());

            // Store file to local machine
            Path filePath = fileStorageConfig.getUploadDir()
                    .resolve(note.get().getCollection().getTitle())
                    .resolve(note.get().getTitle())
                    .resolve(fileName);

            System.out.println(filePath);

            fileStorageService.saveFile(file, filePath);

            // Store file entry to the database
            File fileEntity = new File(fileName, contentType, note.get());
            fileEntity.setSize(fileSize);
            fileEntity.setDateAdded(dateAdded);
            fileDatabaseService.addFile(fileEntity);

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            System.out.println("Something went wrong: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Deletes a file by its ID.
     * The file is removed from the filesystem and its database entry is deleted.
     *
     * @param id the ID of the file to delete.
     * @return Returns 200 OK if the file is successfully deleted.
     * 404 Not Found if the does not exist.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFile(@PathVariable long id) {
        Optional<File> file = fileDatabaseService.getFileById(id);
        if (file.isEmpty()) return ResponseEntity.notFound().build();

        Note note = file.get().getNote();
        NoteCollection collection = note.getCollection();

        if(collection == null) return ResponseEntity.notFound().build();

        // Delete file from filesystem
        String fileName = file.get().getFileName();
        Path filePath = fileStorageConfig.getUploadDir()
                .resolve(collection.getTitle())
                .resolve(note.getTitle())
                .resolve(fileName);

        fileStorageService.deleteFile(filePath);

        // Delete file from database
        fileDatabaseService.deleteFile(id);

        return ResponseEntity.ok().build();
    }

    /**
     * Gets the file content by its ID and returns a string and produces
     * a 200 OK response. If the file doesn't exist it returns a 404
     * not found response.
     *
     * @param id the ID of the file to get.
     * @return an optional containing a file or not based on if it
     * exists.
     */
    @GetMapping("/{id}")
    public ResponseEntity<byte[]> getContent(@PathVariable long id) {

        Optional<File> file = fileDatabaseService.getFileById(id);
        if (file.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Note note = file.get().getNote();
        NoteCollection collection = note.getCollection();

        if(collection == null) return ResponseEntity.notFound().build();

        Path path = fileStorageConfig.getUploadDir()
                .resolve(collection.getTitle())
                .resolve(note.getTitle())
                .resolve(file.get().getFileName());

        Optional<byte[]> content = fileStorageService.getFileContent(path);
        return content.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * A REST endpoint to get content of images as base64
     * @param noteId The id of the note containing the image
     * @param fileName The name of the image
     * @return The base64 content as a string
     */
    @GetMapping("/notes/{noteId}/{fileName}")
    public ResponseEntity<String> getNote(@PathVariable long noteId,
                                          @PathVariable String fileName) {
        Optional<Note> note = noteService.getNoteById(noteId);
        if(note.isEmpty()) return ResponseEntity.notFound().build();

        Optional<File> file = fileDatabaseService.getFileByNoteAndName(note.get(), fileName);
        if (file.isEmpty()) return ResponseEntity.notFound().build();

        String base64Content = fileStorageService.getImageContent(file.get(), note.get());
        if(base64Content == null) return ResponseEntity.notFound().build();

        return ResponseEntity.ok(base64Content);
    }

    /**
     * Updates an existing file's name with the given ID
     * using the new name provided in the request body.
     *
     * @param id the ID of the file to rename
     * @param fileInfo the file containing the updated name
     * @return 200 OK if the renaming was successfully, and 404 Not Found
     * if the file or Note does not exist.
     */
    @PutMapping("/{id}")
    @ResponseStatus(value = HttpStatus.OK)
    public ResponseEntity<Void> updateFileName(@PathVariable long id,
                                               @RequestBody File fileInfo) {

        Optional<File> file = fileDatabaseService.getFileById(id);
        if (file.isEmpty()) return ResponseEntity.notFound().build();

        Note note = file.get().getNote();
        NoteCollection collection = note.getCollection();

        if(collection == null) return ResponseEntity.notFound().build();

        String oldName = file.get().getFileName();
        String newName = fileInfo.getFileName();

        // Renaming file in the system
        Path oldPath = fileStorageConfig.getUploadDir()
                .resolve(collection.getTitle())
                .resolve(note.getTitle())
                .resolve(oldName);

        Path newPath = fileStorageConfig.getUploadDir()
                .resolve(collection.getTitle())
                .resolve(note.getTitle())
                .resolve(newName);
        boolean systemSuccess = fileStorageService.renameFile(oldPath, newPath);

        // Renaming file in the database
        file.get().setFileName(newName);
        boolean databaseSuccess = fileDatabaseService.updateFile(id, file.get());

        if(!databaseSuccess && systemSuccess) {
            // reverting file name in the system
            fileStorageService.renameFile(newPath, oldPath);
        }

        if(!systemSuccess && databaseSuccess) {
            // reverting file name in the database
            file.get().setFileName(oldName);
            fileDatabaseService.updateFile(id, file.get());
        }

        return ResponseEntity.ok().build();
    }

    /**
     * This is a REST endpoint to update the note directory in the filesystem.
     * @param noteId The id of the Note Entity to update
     * @param collectionId The id of the Collection Entity to move to
     * @param newNote The Note Entity containing the new information
     * @return A response status of 200 OK if everything went okay.
     */
    @PutMapping("/note/{noteId}/{collectionId}")
    public ResponseEntity<Void> updateNoteDirectory(
            @PathVariable long noteId, @PathVariable long collectionId, @RequestBody Note newNote) {
        Optional<Note> oldNote = noteService.getNoteById(noteId);
        Optional<NoteCollection> newNoteCollection =
                noteCollectionService.getNoteCollectionById(collectionId);

        if(oldNote.isEmpty() || newNoteCollection.isEmpty())
            return ResponseEntity.notFound().build();

        if(oldNote.get().getCollection() == null)
            return ResponseEntity.notFound().build();

        fileStorageService.updateNoteDirectory(oldNote.get(), newNote, newNoteCollection.get());

        return ResponseEntity.ok().build();
    }

    /**
     * This is a REST endpoint to delete a note directory in the filesystem.
     * @param id The id of the Note entity to delete
     * @return A response status of 200 OK if everything went okay
     */
    @DeleteMapping("/note/{id}")
    public ResponseEntity<Void> deleteNoteDirectory(@PathVariable long id) {
        Optional<Note> note = noteService.getNoteById(id);
        if(note.isEmpty()) return ResponseEntity.notFound().build();
        if(note.get().getCollection() == null) return ResponseEntity.notFound().build();

        fileStorageService.deleteNoteDirectory(note.get());

        return ResponseEntity.ok().build();
    }

    /**
     * This is a REST endpoint to delete a noteCollection directory in the filesystem.
     * @param id The id of the NoteCollection entity to delete
     * @return A response status of 200 OK if everything went okay.
     */
    @DeleteMapping("/collection/{id}")
    public ResponseEntity<Void> deleteCollectionDirectory(@PathVariable long id) {
        Optional<NoteCollection> noteCollection = noteCollectionService.getNoteCollectionById(id);
        if(noteCollection.isEmpty()) return ResponseEntity.notFound().build();

        fileStorageService.deleteNoteCollectionDirectory(noteCollection.get());

        return ResponseEntity.ok().build();
    }

    /**
     * This is a REST endpoint to update a noteCollection directory in the filesystem.
     * @param id The id of the NoteCollection
     * @param newNoteCollection A NoteCollection Entity containing the new information.
     * @return A response status of 200 OK if everything went okay.
     */
    @PutMapping("/collection/{id}")
    public ResponseEntity<Void> updateCollectionDirectory(
            @PathVariable long id,
            @RequestBody NoteCollection newNoteCollection) {

        Optional<NoteCollection> oldNoteCollection =
                noteCollectionService.getNoteCollectionById(id);

        if(oldNoteCollection.isEmpty()) return ResponseEntity.notFound().build();

        fileStorageService.updateNoteCollectionDirectory(
                oldNoteCollection.get(), newNoteCollection);

        return ResponseEntity.ok().build();
    }
}
