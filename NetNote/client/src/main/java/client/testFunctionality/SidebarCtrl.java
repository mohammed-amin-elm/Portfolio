/*
 * -------------------------------------------------------------
 * | JAVA DOC comments are generated with ChatGPT              |
 * -------------------------------------------------------------
 */

package client.testFunctionality;

import client.PrimaryCtrl;
import commons.Note;
import jakarta.inject.Inject;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;

import java.util.List;

/**
 * Controller for the Sidebar which handles note-related operations.
 * It manages the creation, deletion, and refresh of notes in the ListView.
 */
public class SidebarCtrl {
    private PrimaryCtrl pc;

    @FXML
    private ListView<String> noteListView;
    private ObservableList<Note> notes;

    /**
     * Constructor that injects the PrimaryCtrl.
     *
     * @param pc the PrimaryCtrl instance
     */
    @Inject
    public SidebarCtrl(PrimaryCtrl pc) {
        this.pc = pc;
    }

    /**
     * Initializes the SidebarCtrl, setting up mock data for testing.
     * In a real-world scenario, this could be replaced with a server request.
     */
    @FXML
    private void initialize() {
        notes = FXCollections.observableArrayList();
        //testNotes();
        sendRequest();
    }

    /**
     * Handler for the Create button. This is supposed to open the note creating scene.
     */
    @FXML
    public void createClick() {
        // TODO: Switch to a scene to create the note
        pc.showNoteCreationScene();
    }

    /**
     * Handler for the Refresh button. Clears the notes and refreshes the ListView.
     */
    @FXML
    public void refreshClick() {
        // I am not sure what refresh is really supposed to do.
        notes.clear();
        setNoteListView();
        System.out.println(notes);
    }

    /**
     * Handler for the Delete button. Deletes the selected note from the list.
     * It does not delete the note from the server.
     */
    @FXML
    public void deleteClick() {
        String selectedTitle = noteListView.getSelectionModel().getSelectedItem();
        if (selectedTitle != null) {
            for (Note note : notes) {
                if (note.getTitle().equals(selectedTitle)) {
                    notes.remove(note);
                    setNoteListView();
                    System.out.println(notes);
                }
            }
        }
    }

    /**
     * Sends a GET request to the server to fetch notes and updates the ListView.
     */
    private void sendRequest() {
        try (var client = ClientBuilder.newClient();
             Response response = client
                     .target("http://localhost:8080/api/note/")
                     .request(MediaType.APPLICATION_JSON)
                     .get()) {

            handleResponse(response);

        } catch (Exception e) {
            onConnectionFailed(e);
        }
    }

    /**
     * Handles the case where the connection fails.
     * It shows a retry button.
     *
     * @param e the exception encountered during the connection attempt
     */
    private void onConnectionFailed(Exception e) {
        System.out.println(e.getMessage());
        Button retryButton = new Button("Connection failed. Press to retry.");
        retryButton.setOnAction(event -> sendRequest());
    }

    /**
     * Processes the server response containing the notes.
     * If the response is successful, it converts the JSON data to a list of notes.
     *
     * @param response the server response containing note data
     */
    private void handleResponse(Response response) {
        if (response.getStatus() == 200) {
            List<Note> noteList = response.readEntity(new GenericType<List<Note>>() {});


            notes = FXCollections.observableArrayList(noteList);

            setNoteListView();
            System.out.println("Fetched notes: " + notes);
        } else {
            System.out.println("Bad response request");
        }
    }

    /**
     * Updates the ListView with the titles of the notes.
     */
    private void setNoteListView() {
        List<String> titles = notes.stream()
                .map(Note::getTitle)
                .toList();

        var noteTitles = FXCollections.observableArrayList(titles);

        noteListView.setItems(noteTitles);
    }
}
