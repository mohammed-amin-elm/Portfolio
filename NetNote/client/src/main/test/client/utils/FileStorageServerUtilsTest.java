package client.utils;

import commons.File;
import commons.Note;
import commons.NoteCollection;
import org.junit.jupiter.api.*;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileStorageServerUtilsTest {

    final static String fileName = "test.txt";
    final static String testFilePath = "src/main/test/client/utils/" + fileName;
    final static String testFileContent = "TESTCONTENT";
    static java.io.File testFile;

    FileStorageServerUtils utils;
    ServerUtils serverUtils;



    NoteCollection testNoteCollection;
    Note testNote;

    @BeforeAll
    static void fileSetup() {
        try {
            testFile = new java.io.File(testFilePath);
            testFile.createNewFile();

            FileWriter myWriter = new FileWriter(testFile);
            myWriter.write(testFileContent);
            myWriter.close();
        } catch( IOException e ) {
            System.out.println("An error occurred while trying to create the file.");
            e.printStackTrace();
        }
    }

    @AfterAll
    static void deleteFile() {
        try {
            testFile.delete();
        } catch( Exception e ) {
            System.out.println("An error occurred while trying to delete the file.");
            e.printStackTrace();
        }
    }

    @BeforeEach
    void setup() {
        this.utils = new FileStorageServerUtils();
        this.serverUtils = new ServerUtils();

        testNoteCollection = new NoteCollection("TITLE", "INTERNALNAME", "URL");
        serverUtils.newNoteCollection(testNoteCollection);

        testNote = new Note("TITLE", "CONTENT", testNoteCollection);
        serverUtils.addNote(testNote, serverUtils.getAllCollections().getLast().getId());
    }

    @AfterEach
    void teardown() {
        serverUtils.deleteNoteCollection(serverUtils.getAllCollections().getLast().getId());
    }

    @Test
    void getAllFiles() {
        List<File> files = utils.getAllFiles();
        if(files == null) return;

        for(File file : files) {
            System.out.println(file.getFileName());
        }
    }

    @Test
    void testUploadAndDeleteFile() {
        List<File> filesBefore = utils.getAllFiles();

        java.io.File testFile = new java.io.File(testFilePath);
        utils.uploadFile(testFile, serverUtils.getAllCollections().getLast().getNotes().getLast().getId());

        List<File> filesAfter = utils.getAllFiles();

        assertEquals(filesBefore.size(), filesAfter.size() - 1);
        assertEquals(filesAfter.getLast().getFileName(), testFile.getName());

        utils.deleteFile(filesAfter.getLast().getId());

        filesAfter = utils.getAllFiles();
        assertEquals(filesBefore.size(), filesAfter.size());
    }

    @Test
    void testGetContent() {
        utils.uploadFile(testFile, serverUtils.getAllCollections().getLast().getNotes().getLast().getId());
        long fileId = utils.getAllFiles().getLast().getId();

        byte[] content = utils.getContent(fileId);
        assertEquals(testFileContent, new String(content));

        utils.deleteFile(fileId);
    }

    @Test
    void renameFile() {
        utils.uploadFile(testFile, serverUtils.getAllCollections().getLast().getNotes().getLast().getId());
        long fileId = utils.getAllFiles().getLast().getId();

        assertEquals(fileName, utils.getAllFiles().getLast().getFileName());

        utils.renameFile(fileId, new File("newname.txt", "text/plain", testNote));
        assertEquals("newname.txt", utils.getAllFiles().getLast().getFileName());

        utils.deleteFile(fileId);
    }

    @Test
    void testUploadFile_NonExistentFile() {
        java.io.File nonExistentFile = new java.io.File("nonexistent.txt");

        Exception exception = assertThrows(jakarta.ws.rs.client.ResponseProcessingException.class, () -> {
            utils.uploadFile(nonExistentFile, serverUtils.getAllCollections().getLast().getNotes().getLast().getId());
        });

        assertTrue(exception.getMessage().contains("nonexistent.txt"));
    }

    @Test
    void testGetContent_NonExistentFile() {
        byte[] content = utils.getContent(-1L);
        assertNull(content);
    }

    @Test
    void testDeleteNonExistentFile() {
        long invalidFileId = -1L;

        int responseCode = utils.deleteFile(invalidFileId);

        assertEquals(404, responseCode);
    }

    @Test
    void testUploadFile_EmptyContent() {
        try {
            java.io.File emptyFile = new java.io.File("empty.txt");
            emptyFile.createNewFile();

            int responseCode = utils.uploadFile(emptyFile, serverUtils.getAllCollections().getLast().getNotes().getLast().getId());
            assertEquals(200, responseCode);

            emptyFile.delete();
        } catch (IOException e) {
            fail("Failed to create or delete the empty file for testing.");
        }
    }

    @Test
    void testGetAllFiles_NoFiles() {
        List<File> files = utils.getAllFiles();
        assertNotNull(files);
        assertTrue(files.isEmpty());
    }

    @Test
    void testRenameFile_SameName() {
        utils.uploadFile(testFile, serverUtils.getAllCollections().getLast().getNotes().getLast().getId());
        long fileId = utils.getAllFiles().getLast().getId();

        utils.renameFile(fileId, new File(fileName, "text/plain", testNote));
        assertEquals(fileName, utils.getAllFiles().getLast().getFileName());

        utils.deleteFile(fileId);
    }

    @Test
    void testDeleteFile_AlreadyDeleted() {
        utils.uploadFile(testFile, serverUtils.getAllCollections().getLast().getNotes().getLast().getId());
        long fileId = utils.getAllFiles().getLast().getId();

        utils.deleteFile(fileId);
        int responseCode = utils.deleteFile(fileId);

        assertEquals(404, responseCode);
    }



}