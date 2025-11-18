package client;

import client.utils.FileStorageServerUtils;
import client.utils.ServerUtils;
import client.utils.ValidationUtils;
import commons.Note;
import commons.NoteCollection;
import jakarta.inject.Inject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import commons.wsmessage.Message;
import commons.wsmessage.UpdateType;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.AnchorPane;

import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * The NoteEditCtrl class is responsible for managing the note editing
 * interface and interacting with the server to update the note's title
 * and collection. It allows users to change a note's name and its
 * associated collection while keeping track of the changes on the server.
 */
public class NoteEditCtrl {
    private final PrimaryCtrl pc;
    private final ServerUtils serverUtils;
    private final ValidationUtils validationUtils;
    private final DefaultCollectionService defaultCollectionService;
    private final FileStorageServerUtils fileStorageServerUtils;

    @FXML
    private ComboBox<NoteCollection> collectionDropDown;

    @FXML
    private TextField collectionNameBox;

    @FXML
    private GridPane gridPane;

    @FXML
    private AnchorPane errorMessagePane;

    @FXML
    private Button cancelButton;

    @FXML
    private Button confirmButton;
    @FXML
    private Label editLabel;

    /**
     * Constructs a NoteEditCtrl with the provided dependencies.
     *
     * @param pc the PrimaryCtrl instance used to manage scene transitions
     * @param serverUtils the ServerUtils instance for interacting with the server
     * @param validationUtils the ValidationUtils instance for validating note names
     * @param defaultCollectionService the DefaultCollectionService for managing collections
     */


    @Inject
    private NoteEditCtrl(
            PrimaryCtrl pc,
            ServerUtils serverUtils, ValidationUtils validationUtils,
            DefaultCollectionService defaultCollectionService,
            FileStorageServerUtils fileStorageServerUtils) {
        this.pc = pc;
        this.serverUtils = serverUtils;
        this.validationUtils = validationUtils;
        this.defaultCollectionService = defaultCollectionService;
        this.fileStorageServerUtils = fileStorageServerUtils;
    }

    /**
     * Configures the keyboard shortcut (Ctrl + K or Cmd + K) to navigate to the default scene.
     * This allows users to quickly return to the main scene using the keyboard.
     */
    private void configureKeyboardShortcuts() {
        gridPane.setOnKeyPressed(event -> {
            if ((event.isControlDown() || event.isMetaDown()) && event.getCode() == KeyCode.K) {
                System.out.println("Ctrl/Cmd + K pressed AYOOOO");
                pc.showDefaultScene();
                event.consume();
            }
            if ((event.getCode() == KeyCode.ESCAPE)){
                System.out.println("esc pressed AYOOO");
                collectionNameBox.requestFocus();
                event.consume();
            }
            if ((event.getCode() == KeyCode.ENTER)){
                System.out.println("Enter pressed AYOOO");
                try {
                    onConfirmClick();
                    event.consume();
                } catch (Exception e) {
                    System.err.println("An error occurred while creating a note: "
                            + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
        collectionNameBox.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                System.out.println("Enter pressed");
                try {
                    onConfirmClick();
                    event.consume();
                } catch (Exception e) {
                    System.err.println("An error occurred while creating a note: "
                            + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Initializes the controller. Called to initialize
     * the controller's state when the scene is loaded.
     */
    public void initialize() {

        gridPane.setFocusTraversable(true);
        configureKeyboardShortcuts();
    }

    /**
     * Refreshes the scene by setting the current note's name in the text box
     * and updating the collection dropdown with all available note collections.
     */
    public void refresh() {
        collectionNameBox.setText(
                defaultCollectionService
                        .getSelectedNote()
                        .toString()
        );
        ObservableList<NoteCollection> collections = FXCollections.observableArrayList();
        collections.setAll(serverUtils.getAllCollections());
        collectionDropDown.setItems(collections);
        defaultCollectionService.setSelectedFile(null);

        serverUtils.addCollectionToNote(defaultCollectionService.getSelectedNote());
    }

    /**
     * Updates the title of the selected note if the entered name is valid.
     * The method checks for name validity and updates the note's title on the server.
     * Additionally, it updates the linked notes' references to reflect the new name.
     *
     * @return true if the name update was successful, false otherwise
     * @throws ExecutionException if an error occurs while executing the update
     * @throws InterruptedException if the update process is interrupted
     */
    private boolean updateName() throws ExecutionException, InterruptedException {
        String newName = collectionNameBox.getText();
        Note noteToUpdate = defaultCollectionService.getSelectedNote();
        serverUtils.addCollectionToNote(noteToUpdate);

        NoteCollection target;
        if(collectionDropDown.getValue() == null)
            target = noteToUpdate.getCollection();
        else target = collectionDropDown.getValue();


        if (!validationUtils.isValidName(newName, errorMessagePane))
            return false;
        if(validationUtils.isDuplicateNoteTitle(newName, target,
                errorMessagePane, "A note with this title already exists in this collection"))
            return false;

        if(newName.equals(noteToUpdate.getTitle())) {
            fileStorageServerUtils.updateNoteDirectory(noteToUpdate.getId(), noteToUpdate,
                    target.getId());
            return true;
        }


        if(target.getId() == noteToUpdate.getCollection().getId()) {
            var notes = target.getNotes();
            for(Note note : notes) {
                Pattern linkPattern = Pattern.compile("\\[\\[(.*?)]]");
                Matcher matcher = linkPattern.matcher(note.getContent());

                String newContent = matcher.replaceAll(matchResult -> {
                    String linkedNote = matchResult.group(1);

                    if(linkedNote.equals(noteToUpdate.getTitle()))
                        return "[[" + newName + "]]";

                    return "[[" + linkedNote + "]]";
                });

                note.setContent(newContent);
                serverUtils.updateNote(note.getId(), note);
            }
        }

        noteToUpdate.setTitle(newName);

        fileStorageServerUtils.updateNoteDirectory(noteToUpdate.getId(), noteToUpdate,
                target.getId());

        serverUtils.updateNote(noteToUpdate.getId(), noteToUpdate);
        pc.webSocketMessage(new Message(UpdateType.TITLE_CHANGE, newName, noteToUpdate.getId()));
        return true;
    }

    /**
     * Updates the collection in which the selected note is stored.
     * If the note is moved to a different collection, the server is updated
     * accordingly.
     */
    private void updateCollection() {
        Note noteToUpdate = defaultCollectionService.getSelectedNote();
        NoteCollection newNoteCollection = collectionDropDown.getValue();
        NoteCollection oldNoteCollection = noteToUpdate.getCollection();

        if(newNoteCollection == null || oldNoteCollection == null)
            return;

        if(oldNoteCollection.getId() == newNoteCollection.getId())
            return;

        String errorMessage = "A note with this title already exists in "
                + newNoteCollection.getTitle();
        if(validationUtils.isDuplicateNoteTitle(noteToUpdate.getTitle(), newNoteCollection,
                errorMessagePane, errorMessage)) {
            collectionNameBox.setText(errorMessage);
            return;
        }

        oldNoteCollection.removeNote(noteToUpdate.getId());
        newNoteCollection.addNote(noteToUpdate);

        noteToUpdate.setNoteCollection(newNoteCollection);

        serverUtils.updateNote(noteToUpdate.getId(), noteToUpdate);
        serverUtils.updateNoteCollection(oldNoteCollection.getId(), oldNoteCollection);
        serverUtils.updateNoteCollection(newNoteCollection.getId(), newNoteCollection);
    }

    /**
     * Handler for the confirm button. Updates the selected note's name and collection
     * on the server, and then switches the user interface back to the default scene.
     *
     * @throws ExecutionException if an error occurs during the note update process
     * @throws InterruptedException if the note update process is interrupted
     */
    public void onConfirmClick() throws ExecutionException, InterruptedException {
        if(!updateName())
            return;
        updateCollection();
        pc.showDefaultScene("Note updated", false);
    }

    /**
     * Handler for the cancel button. Returns the user to the default scene
     * without making any changes.
     */
    public void onCancelClick() {
        defaultCollectionService.setSelectedFile(null);
        pc.showDefaultScene();
    }

    /**
     * changes the language
     * @param resourceBundle the language
     */
    public void changeLanguage(ResourceBundle resourceBundle) {
        cancelButton.setText(resourceBundle.getString("cancel.button"));
        confirmButton.setText(resourceBundle.getString("confirm.button"));
        collectionDropDown.setPromptText(resourceBundle.getString("collections.prompt"));
        collectionNameBox.setPromptText(resourceBundle.getString("create.text"));
        editLabel.setText(resourceBundle.getString("create.text"));
    }
}
