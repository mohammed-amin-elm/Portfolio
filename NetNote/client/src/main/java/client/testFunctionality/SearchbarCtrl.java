package client.testFunctionality;

import client.PrimaryCtrl;
import commons.Note;
import jakarta.inject.Inject;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

/**
 * The SearchbarCtrl class is responsible for fetching text from the TextField
 * and uses that to filter the notes from the server.
 */
public class SearchbarCtrl {

    @FXML
    private TextField searchbar;
    @FXML
    private ScrollPane notepadTitles;
    private PrimaryCtrl pc;

    /**
     * Constructor for the SearchbarCtrl class
     * @param pc the primary controller that will be used
     *           to switch scenes
     */
    @Inject
    public SearchbarCtrl(PrimaryCtrl pc) {
        this.pc = pc;
    }

    /**
     * Event that fires when enter is pressed after interacting with the
     * searchbar.
     */
    @FXML
    public void onEnterSearchbar() {
        sendRequest();
    }

    /**
     *  Sends a request to the server to get all the notes
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
     * This gets called when the client fails to connect to the server and
     * creates a button to reattempt the connection.
     * @param e The exception that was thrown when the connection failed
     */
    private void onConnectionFailed(Exception e) {

        System.out.println(e.getMessage());
        Button retryButton = new Button("Connection failed. Press to retry.");
        retryButton.setOnAction(event -> sendRequest());

        VBox root = new VBox();
        root.setSpacing(10);
        root.setPadding(new Insets(10));
        root.getChildren().add(retryButton);

        notepadTitles.setContent(root);
    }

    /**
     * This method handles the response that was given by the server
     * @param response the server response to getting all the notes
     */
    private void handleResponse(Response response) {

        if (response.getStatus() == 200) {
            String keyword = searchbar.getText();
            if (keyword.isEmpty()) {
                resetNoteList();
                return;
            }

            List<Note> allNotes = sortSelectableNotes(
                    response.readEntity(new GenericType<>() {}),
                    keyword);
            createSelectableNoteList(allNotes);
        }
        else {
            System.out.println("Bad response request");
        }
    }

    /**
     * This function filters the given list based on title and context. After
     * that it adds them together and removes and duplicates.
     * @param notes The list of notes that needs to get filtered
     * @param keyword the keyword that will be used to filter the list
     * @return a filtered list based on the keyword
     */
    private List<Note> sortSelectableNotes(List<Note> notes, String keyword) {

        return notes.stream().
                filter(note -> note.getTitle().contains(keyword) ||
                        note.getContent().contains(keyword)).
                toList();
    }

    /**
     * This function creates a button for each note that had been filtered
     * based on the keyword that was entered. And puts it into the ScrollPane
     * @param filteredList The list of notes a button needs to be made of
     */
    private void createSelectableNoteList(List<Note> filteredList) {

        List<Button> noteButtons = new ArrayList<>();
        VBox root = new VBox();

        filteredList.forEach(note -> {
            Button newButton = new Button(note.getTitle());
            newButton.setOnAction(event -> onNoteButtonPressed(note));
            noteButtons.add(newButton);
        });

        root.getChildren().addAll(noteButtons);
        root.setSpacing(10);
        root.setPadding(new Insets(10));
        notepadTitles.setContent(root);
    }


    /**
     * Makes the result note list empty.
     */
    private void resetNoteList() {
        VBox root = new VBox();
        notepadTitles.setContent(root);
    }

    /**
     * The event that fires when the note button has been pressed
     * @param note The note on the button that the user has pressed
     */
    private void onNoteButtonPressed(Note note) {
        // TODO: Switch to a scene to edit the note
        System.out.println(note.getContent());
    }

}
