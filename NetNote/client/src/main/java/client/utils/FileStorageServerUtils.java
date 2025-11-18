package client.utils;

import commons.File;
import commons.Note;
import commons.NoteCollection;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;

import java.util.List;

import static jakarta.ws.rs.core.MediaType.*;

public class FileStorageServerUtils {
    private static final String SERVER = "http://localhost:8080/";

    /**
     * Retrieves a list of all files from the server.
     * This method sends a GET request to the server defined by the SERVER constant.
     * If the server responds with a status code of 200, the response is parsed into
     * a List<File> and returned. Otherwise, an error message is printed, and null
     * is returned.
     *
     * @return a List<File> containing all files from the server if the request is successful,
     *         or null if the request fails.
     */
    public List<File> getAllFiles() {
        var response = ClientBuilder.newClient()
                .target(SERVER)
                .path("/api/files/")
                .request(APPLICATION_JSON)
                .get();

        if (response.getStatus() == 200) {
            return response.readEntity(new GenericType<>() {});
        } else {
            System.out.println(response.getStatusInfo().getReasonPhrase());
        }

        return null;
    }

    /**
     * Uploads a file to the server for the specified note ID.
     * This method sends a POST request to upload the given file to the server.
     * The server endpoint is constructed using the specified ID. If the server
     * responds with a 400 status code, it indicates that the specified note does
     * not exist. If a 500 status code is received, it indicates a server-side
     * exception.
     *
     * @param file the file to be uploaded
     * @param id   the ID of the note to which the file will be associated
     * @return The status code of the request.
     */
    public int uploadFile(java.io.File file, long id) {

        FileDataBodyPart filePart = new FileDataBodyPart("file", file);
        FormDataMultiPart multiPart = (FormDataMultiPart)
                new FormDataMultiPart().bodyPart(filePart);

        var response = ClientBuilder.newClient()
                .register(MultiPartFeature.class)
                .target(SERVER)
                .path("api/files/" + id)
                .request(MULTIPART_FORM_DATA)
                .post(Entity.entity(multiPart, multiPart.getMediaType()));

        return response.getStatus();
    }

    /**
     * Deletes a file on the server associated with the specified note ID.
     *
     * This method sends a DELETE request to the server to remove the file
     * identified by the given note ID. The status code of the server's response
     * is returned. A successful deletion typically returns a 200 status code,
     * but other status codes may indicate errors.
     *
     * @param id the ID of the note whose associated file is to be deleted
     * @return the status code of the server's response
     */
    public int deleteFile(long id) {
        var response = ClientBuilder.newClient()
                .target(SERVER)
                .path("api/files/" + id)
                .request()
                .delete();

        return response.getStatus();
    }

    /**
     * Retrieves the content of a file from the server as a byte array.
     * The method sends an HTTP GET request to the server using the provided file ID.
     * If the file is found, its content is returned as a byte array. If the file does not
     * exist or another error occurs, appropriate actions are
     * taken based on the HTTP response status.
     *
     * @param id the unique identifier of the file to retrieve
     * @return the content of the file as a byte array if the file
     * exists and the server responds with HTTP 200;
     * null if the file is not found (HTTP 404) or if another error occurs
     */
    public byte[] getContent(long id) {
        var response = ClientBuilder.newClient()
                .target(SERVER)
                .path("api/files/" + id)
                .request()
                .get();

        switch(response.getStatus()) {
            case 200:
                return response.readEntity(byte[].class);
            case 404:
                System.out.println("File not found");
                return null;
            default:
                return null;
        }
    }

    /**
     * Renames a file on the server by sending an updated file entity.
     * The method sends an HTTP PUT request to the server with the specified file ID and
     * the new file entity. The server processes the request and updates the file details.
     *
     * @param id the unique identifier of the file to rename
     * @param fileEntity the file entity containing the new name and other details
     */
    public void renameFile(long id, File fileEntity) {
        var requestBody = Entity.entity(fileEntity, APPLICATION_JSON);
        var response = ClientBuilder.newClient()
                .target(SERVER)
                .path("api/files/" + id)
                .request(APPLICATION_JSON)
                .put(requestBody);
    }

    /**
     * Updates Note directory on filesystem.
     * @param id The id of the Note to update
     * @param newNote A Note Entity containing the new information.
     * @param collectionId The NoteCollection to move to
     */
    public void updateNoteDirectory(long id, Note newNote, long collectionId) {
        var requestBody = Entity.entity(newNote, APPLICATION_JSON);
        var response = ClientBuilder.newClient()
                .target(SERVER)
                .path("api/files/note/" + id + "/" + collectionId)
                .request(APPLICATION_JSON)
                .put(requestBody);
    }

    /**
     * Deletes Note directory on filesystem
     * @param id The id of the Note to update
     */
    public void deleteNoteDirectory(long id) {
        var response = ClientBuilder.newClient()
                .target(SERVER)
                .path("api/files/note/" + id)
                .request()
                .delete();

    }

    /**
     * Deletes NoteCollection directory from filesystem
     * @param id The id of the NoteCollection to delete.
     */
    public void deleteNoteCollectionDirectory(long id) {
        var response = ClientBuilder.newClient()
                .target(SERVER)
                .path("api/files/collection/" + id)
                .request()
                .delete();
    }

    /**
     * Updates the NoteCollection directory on the filesystem.
     * @param id The id of the NoteCollection to update
     * @param newNoteCollection A NoteCollection entity containing the new information.
     */
    public void updateNoteCollectionDirectory(long id, NoteCollection newNoteCollection) {
        var requestBody = Entity.entity(newNoteCollection, APPLICATION_JSON);
        var response = ClientBuilder.newClient()
                .target(SERVER)
                .path("api/files/collection/" + id)
                .request(APPLICATION_JSON)
                .put(requestBody);
    }
}
