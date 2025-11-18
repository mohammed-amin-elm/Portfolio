package server;

import commons.Note;
import commons.NoteCollection;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import server.config.FileStorageConfig;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Base64;
import java.util.Optional;

/**
 * Service class for handling file storage operations on the filesystem.
 * Provides methods to save and delete files.
 */
@Service
public class FileStorageService {

    private final FileStorageConfig fileStorageConfig;


    /**
     * Constructor of the FileStorageService that uses Dependency Injection
     * @param fileStorageConfig The FileStorageConfig instance
     */
    @Autowired
    public FileStorageService(FileStorageConfig fileStorageConfig) {
        this.fileStorageConfig = fileStorageConfig;
    }

    /**
     * Saves a given file to the specified file path on the local filesystem.
     *
     * @param file     the file to save.
     * @param filePath the target path where the file will be saved.
     */
    public void saveFile(MultipartFile file, Path filePath) {
        try {
            Path parent = filePath.getParent();
            if(parent != null) {
                Files.createDirectories(parent);
            }

            file.transferTo(filePath.toFile());
        } catch (IOException e) {
            System.out.println("Error saving file: " + e.getMessage());
        }
    }

    /**
     * Deletes a file at the specified path if it exists.
     *
     * @param filePath the path of the file to delete.
     */
    public void deleteFile(Path filePath) {
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            System.out.println("Error deleting file: " + e.getMessage());
        }
    }

    /**
     * Takes an old file path and a new file path name and updates the
     * old file path.
     *
     * @param oldPath The old path to the file
     * @param newPath The new file name
     * @return A boolean indicating whether the file renaming
     * was a success or not
     */
    public boolean renameFile(Path oldPath, Path newPath) {

        try {
            Files.move(oldPath, newPath, StandardCopyOption.REPLACE_EXISTING);
            return true;
        }
        catch (IOException e) {
            System.out.println("Could not rename the file: " + e.getMessage());
            return false;
        }
    }

    /**
     * Takes a file path and tries to return the content of the file.
     *
     * @param path The path of the file the content will be
     *                 returned from
     * @return An optional containing the content of the file if it
     * found any
     */
    public Optional<byte[]> getFileContent(Path path) {

        try {
            byte[] content = Files.readAllBytes(path);
            return Optional.of(content);
        }
        catch (IOException e) {
            System.out.println("Error getting file content: " + e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Get the content of an image as base64
     * @param fileEntity The image file entity
     * @param note The note containing the image
     * @return A string containing the data ase base64
     */
    public String getImageContent(commons.File fileEntity, Note note) {
        Path path = fileStorageConfig.getUploadDir()
                .resolve(note.getCollection().getTitle())
                .resolve(note.getTitle())
                .resolve(fileEntity.getFileName());

        Optional<byte[]> content = getFileContent(path);

        if(content.isPresent()) {
            String base64 = Base64.getEncoder().encodeToString(content.get());
            String contentType = fileEntity.getContentType();
            return "<img src='data:" + contentType + ";base64," + base64 + "'/>";
        }
        return null;
    }

    /**
     * Deletes the directory of the note and all its corresponding files from the filesystem.
     * @param note The Note entity to delete
     */
    public void deleteNoteDirectory(Note note) {
        NoteCollection noteCollection = note.getCollection();

        Path filePath = fileStorageConfig.getUploadDir()
                .resolve(noteCollection.getTitle())
                .resolve(note.getTitle());

        try {
            FileUtils.deleteDirectory(filePath.toFile());
        } catch (IOException e) {
            System.out.println("Error deleting note: " + e.getMessage());
        }
    }

    /**
     * Deletes the directory of the collection and all its corresponding
     * notes and files from the filesystem.
     * @param noteCollection The NoteCollection to delete
     */
    public void deleteNoteCollectionDirectory(NoteCollection noteCollection) {
        Path filePath = fileStorageConfig.getUploadDir()
                .resolve(noteCollection.getTitle());

        try {
            FileUtils.deleteDirectory(filePath.toFile());
        } catch (IOException e) {
            System.out.println("Error deleting note: " + e.getMessage());
        }
    }

    /**
     * Updates the Note directory in the filesystem, both renaming it
     * and moving it to a new collection.
     * @param oldNote The Note Entity to update
     * @param newNote A new Note Entity containing the new information.
     * @param newNoteCollection The NoteCollection to move to
     */
    public void updateNoteDirectory(Note oldNote, Note newNote, NoteCollection newNoteCollection) {
        String targetCollection = newNoteCollection.getTitle();

        File sourceDirectory = fileStorageConfig.getUploadDir()
                .resolve(oldNote.getCollection().getTitle())
                .resolve(oldNote.getTitle()).toFile();

        try {
            Files.createDirectories(fileStorageConfig.getUploadDir()
                    .resolve(targetCollection));
        } catch (IOException e) {
            System.out.println("Error updating note: " + e.getMessage());
        }

        File targetDirectory = fileStorageConfig.getUploadDir()
                .resolve(targetCollection)
                .resolve(newNote.getTitle()).toFile();

        boolean status = sourceDirectory.renameTo(targetDirectory);

        if(!status) {
            System.out.println("Updating note failed!");
        }
    }

    /**
     * Update the NoteCollection directory in the filesystem, by renaming it.
     * @param oldNoteCollection The NoteCollection Entity to update
     * @param newNoteCollection A new NoteCollection Entity containing the new information.
     */
    public void updateNoteCollectionDirectory(
            NoteCollection oldNoteCollection,
            NoteCollection newNoteCollection) {

        File sourceDirectory = fileStorageConfig.getUploadDir()
                .resolve(oldNoteCollection.getTitle()).toFile();

        File targetDirectory = fileStorageConfig.getUploadDir()
                .resolve(newNoteCollection.getTitle()).toFile();

        boolean status = sourceDirectory.renameTo(targetDirectory);

        if(!status) {
            System.out.println("Updating note failed!");
        }
    }
}
