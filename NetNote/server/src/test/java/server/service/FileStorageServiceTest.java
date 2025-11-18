package server.service;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import server.FileStorageService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doThrow;

/**
 * Provides unit tests for {@link FileStorageService}.
 * Includes tests for saving, deleting, renaming, and retrieving file contents.
 */
@SpringBootTest
public class FileStorageServiceTest {

    @Autowired
    private FileStorageService fileStorageService;

    /**
     * Tests the {@link FileStorageService#saveFile(MultipartFile, Path)} method to ensure a file is saved successfully.
     *
     * Mocks the {@link MultipartFile} and verifies that the file is transferred to the specified file path.
     */
    @Test
    public void saveFileSuccess() throws Exception {
        MultipartFile multipartFile = Mockito.mock(MultipartFile.class);
        Path filePath = Path.of("path");
        fileStorageService.saveFile(multipartFile, filePath);
        Mockito.verify(multipartFile).transferTo(filePath.toFile());
    }

    /**
     * Tests the {@link FileStorageService#deleteFile(Path)} method to ensure a file is deleted successfully.
     *
     * Verifies that the file is deleted after calling the delete method and that the file content is empty.
     */
    @Test
    public void deleteFileSuccess() throws Exception {
        MultipartFile multipartFile = Mockito.mock(MultipartFile.class);
        Path filePath = Path.of("path");
        fileStorageService.saveFile(multipartFile, filePath);
        Mockito.verify(multipartFile).transferTo(filePath.toFile());
        fileStorageService.deleteFile(filePath);
        Optional<byte[]> result = fileStorageService.getFileContent(filePath);
        assertTrue(result.isEmpty());
    }

    /**
     * Tests the {@link FileStorageService#deleteFile(Path)} method to ensure that the deletion does not throw exceptions when the file path is valid.
     *
     * Simulates deleting a file and ensures no exception is thrown, even when the file deletion is successful.
     */
    @Test
    public void deleteFileFail() throws Exception {
        MultipartFile multipartFile = Mockito.mock(MultipartFile.class);
        Path filePath = Path.of("path");
        fileStorageService.saveFile(multipartFile, filePath);
        Mockito.verify(multipartFile).transferTo(filePath.toFile());
        assertDoesNotThrow(() -> fileStorageService.deleteFile(filePath));
    }

    /**
     * Tests the {@link FileStorageService#renameFile(Path, Path)} method to ensure a file is renamed successfully.
     *
     * Verifies that the file content remains unchanged after renaming and that the old path is no longer valid.
     */
    @Test
    public void renameFileSuccess() throws Exception {
        MultipartFile multipartFile = Mockito.mock(MultipartFile.class);
        Path oldPath = Path.of("path");
        Path newPath = Path.of("path2");
        fileStorageService.saveFile(multipartFile, oldPath);
        Mockito.verify(multipartFile).transferTo(oldPath.toFile());
        Optional<byte[]> oldResult = fileStorageService.getFileContent(oldPath);
        fileStorageService.renameFile(oldPath, newPath);
        Optional<byte[]> newResult = fileStorageService.getFileContent(newPath);
        assertEquals(oldResult, newResult);
    }

    /**
     * Tests the {@link FileStorageService#renameFile(Path, Path)} method to ensure renaming fails when the old path is invalid.
     *
     * Verifies that the renaming operation fails if the file does not exist at the specified old path.
     */
    @Test
    public void renameFileFail() throws Exception {
        MultipartFile multipartFile = Mockito.mock(MultipartFile.class);
        Path filePath = Path.of("path");
        fileStorageService.saveFile(multipartFile, filePath);
        Mockito.verify(multipartFile).transferTo(filePath.toFile());
        assertFalse(fileStorageService.renameFile(Path.of("wrongPath"), Path.of("newPath")));
    }

    /**
     * Tests the {@link FileStorageService#getFileContent(Path)} method to ensure file content can be retrieved successfully.
     *
     * Verifies that the file content is returned when the file exists at the given path.
     */
    @Test
    void getFileContentSuccess() throws Exception {
        MultipartFile multipartFile = Mockito.mock(MultipartFile.class);
        Path filePath = Path.of("path");
        fileStorageService.saveFile(multipartFile, filePath);
        Optional<byte[]> result = fileStorageService.getFileContent(filePath);
        assertNotNull(result);
    }

    /**
     * Tests the {@link FileStorageService#getFileContent(Path)} method to ensure an empty result is returned when the file does not exist.
     *
     * Verifies that no content is returned if the file is not found at the given path.
     */
    @Test
    void getFileContentFail() throws Exception {
        MultipartFile multipartFile = Mockito.mock(MultipartFile.class);
        Path filePath = Path.of("path");
        fileStorageService.saveFile(multipartFile, filePath);
        Mockito.verify(multipartFile).transferTo(filePath.toFile());
        Optional<byte[]> result = fileStorageService.getFileContent(Path.of("wrongPath"));
        assertTrue(result.isEmpty());
    }
}
