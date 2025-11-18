package server.service;

import commons.File;
import commons.Note;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import server.FileDatabaseService;
import server.database.FileRepository;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Provides unit tests for {@link FileDatabaseService}.
 * Includes tests for adding and deleting files using a mocked {@link FileRepository}.
 */
public class FileDataBaseServiceTest {

    /**
     * Tests the {@link FileDatabaseService#addFile(File)} method to ensure a file is successfully added.
     *
     * Constructs a mock {@link FileRepository} and simulates adding a file to the repository.
     * Verifies that the size of the file list increases after the addition.
     */
    @Test
    void addFileSuccess() {
        ArrayList<File> allFiles = new ArrayList<>();
        FileRepository fileRepository = Mockito.mock(FileRepository.class);
        FileDatabaseService fileDatabaseService = new FileDatabaseService(fileRepository);
        Note note = Mockito.mock(Note.class);
        File file = new File("fileName", "contentType", note);
        file.setId(1);

        when(fileRepository.findAll()).thenReturn(allFiles);
        doAnswer(invocation -> {
            allFiles.add(file);
            return null;
        }).when(fileRepository).save(file);

        fileDatabaseService.addFile(file);
        int oldResult = fileDatabaseService.getAllFiles().size();
        fileDatabaseService.addFile(file);
        int newResult = fileDatabaseService.getAllFiles().size();

        assertNotEquals(oldResult, newResult);
    }

    /**
     * Tests the {@link FileDatabaseService#deleteFile(Long)} method to ensure a file is successfully deleted.
     *
     * Constructs a mock {@link FileRepository} and simulates adding and deleting a file by its ID.
     * Verifies that the size of the file list decreases after deletion.
     */
    @Test
    void deleteFileSuccess() {
        ArrayList<File> allFiles = new ArrayList<>();
        FileRepository fileRepository = Mockito.mock(FileRepository.class);
        FileDatabaseService fileDatabaseService = new FileDatabaseService(fileRepository);
        Note note = Mockito.mock(Note.class);
        File file = new File("fileName", "contentType", note);
        file.setId(1L);

        when(fileRepository.findAll()).thenReturn(allFiles);
        doAnswer(invocation -> {
            allFiles.add(file);
            return null;
        }).when(fileRepository).save(file);
        doAnswer(invocation -> {
            allFiles.removeIf(f -> f.getId() == 1L);
            return null;
        }).when(fileRepository).deleteById(1L);

        fileDatabaseService.addFile(file);
        int initialSize = fileDatabaseService.getAllFiles().size();
        fileDatabaseService.deleteFile(1L);
        int finalSize = fileDatabaseService.getAllFiles().size();

        assertNotEquals(initialSize, finalSize);
    }
    /**
     * Tests the {@link FileDatabaseService#updateFile(long, File)} method to ensure a file's content is updated.
     *
     * Constructs a mock {@link FileRepository} and simulates updating a file in the repository.
     * Verifies that the file's content changes after the update, ensuring the old and new content differ.
     */
    @Test
    void updateFileSuccess() {
        ArrayList<File> allFiles = new ArrayList<>();
        FileRepository fileRepository = Mockito.mock(FileRepository.class);
        FileDatabaseService fileDatabaseService = new FileDatabaseService(fileRepository);

        Note note = Mockito.mock(Note.class);
        File oldFile = new File("oldFileName", "oldContentType", note);
        oldFile.setId(1L);
        allFiles.add(oldFile);

        File updatedFile = new File("newFileName", "newContentType", note);
        updatedFile.setId(1L);

        when(fileRepository.existsById(1L)).thenReturn(true);
        doAnswer(invocation -> {
            allFiles.removeIf(f -> f.getId() == 1L);
            allFiles.add(updatedFile);
            return null;
        }).when(fileRepository).save(updatedFile);
        boolean result = fileDatabaseService.updateFile(1L, updatedFile);
        assertTrue(result, "File update operation should return true");
        assertEquals(1, allFiles.size(), "There should still be exactly one file in the repository");

        File updatedResultFile = allFiles.get(0);
        assertNotEquals("oldFileName", updatedResultFile.getFileName(), "File name should be updated");
        assertNotEquals("oldContentType", updatedResultFile.getContentType(), "File content type should be updated");
    }

}
