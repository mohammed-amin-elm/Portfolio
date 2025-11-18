package client;

import client.utils.PopupMessages;
import client.utils.ServerUtils;
import client.utils.ValidationUtils;
import commons.Note;
import commons.NoteCollection;
import jakarta.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import commons.wsmessage.Message;
import commons.wsmessage.UpdateType;

import javax.swing.*;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
/*
 * -------------------------------------------------------------
 * | JAVA DOC comments are generated with ChatGPT              |
 * -------------------------------------------------------------
 */


/**
 * The NoteCreationCtrl class manages the note creation
 * user interface and interacts with the server
 * to add new notes to a selected collection. It provides
 * functionality to initialize the scene, monitor
 * server status, and manage user interactions for creating notes.
 */
public class NoteCreationCtrl {
    private final PrimaryCtrl pc;
    private final ServerUtils serverUtils;
    private final ValidationUtils validationUtils;
    private final PopupMessages popupMessages;
    private final DefaultCollectionService defaultCollectionService;

    @FXML
    private Button cancelButton;
    @FXML
    private Button createButton;

    @FXML private Text createText;

    @FXML
    private TextField titleTextField;
    @FXML
    private Text serverStatus;
    @FXML
    private GridPane gridPane;
    @FXML
    private AnchorPane errorPane;
    private boolean invalidName = false;

    /**
     * Initializes a continuous server status check that runs on a separate daemon thread.
     * This method sets up a loop that periodically checks the availability of the server
     * every 5 seconds using the {@link ServerUtils#isServerAvailable()} method.
     * If the server is reachable, it updates the `serverStatus` text to indicate it is reachable;
     * otherwise, it sets the status to "Server not reachable".
     * This functionality assists in monitoring and informing the user of the server's status
     * within the user interface.
     */
    public void serverCheckInit() {
        Thread serverCheckThread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(5000);
                } catch (Exception _) {

                }

                if(!serverUtils.isServerAvailable())
                    serverStatus.setText("Server not reachable");
                else serverStatus.setText("Server reachable");
            }
        });
        serverCheckThread.setDaemon(true);
        serverCheckThread.start();
    }

    /**
     * Constructs a NoteCreationCtrl with the given
     * PrimaryCtrl and ServerUtils instances.
     *
     * @param pc the PrimaryCtrl instance used to manage
     *          the main application window and scenes
     * @param serverUtils the ServerUtils instance that
     *                   provides server-related utilities and operations
     * @param validationUtils the validation utilities for the name
     * @param defaultCollectionService the handler for the default collection
     * @param popupMessages pupupMessages
     */
    @Inject
    public NoteCreationCtrl(PrimaryCtrl pc,
                            ServerUtils serverUtils, ValidationUtils validationUtils,
                            DefaultCollectionService defaultCollectionService,
                            PopupMessages popupMessages) {
        this.pc = pc;
        this.serverUtils = serverUtils;
        this.validationUtils = validationUtils;
        this.defaultCollectionService = defaultCollectionService;
        this.popupMessages = popupMessages;
    }

    /**
     * Handles keyboard shortcuts
     */
    private void configureKeyboardShortcuts() {
        gridPane.setOnKeyPressed(event -> {
            if ((event.isControlDown() || event.isMetaDown()) && event.getCode() == KeyCode.K) {
                System.out.println("Ctrl/Cmd + K pressed");
                pc.showDefaultScene();
                event.consume();
            }
            if ((event.getCode() == KeyCode.ESCAPE)){
                System.out.println("esc pressed");
                titleTextField.requestFocus();
                event.consume();
            }
            if ((event.getCode() == KeyCode.ENTER)){
                System.out.println("Enter pressed");
                try {
                    onCreateClick();
                    event.consume();
                } catch (Exception e) {
                    System.err.println("An error occurred while creating a note: " 
                    + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
        titleTextField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                System.out.println("Enter pressed");
                try {
                    onCreateClick();
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
     * Initializes the NoteCreationCtrl setup
     * by performing necessary initial checks
     * and populating the collection dropdown.
     * This method first initiates a continuous
     * server status check to update the server status
     * indicator in the user interface.
     * It then retrieves a list of all available note
     * collections from the server and updates the collection
     * dropdown menu with these collections,
     * allowing the user to select a collection
     * for organizing notes.
     * Additionally handles keyboard shortcuts
     */
    public void initialize() {
        serverCheckInit();
        gridPane.setFocusTraversable(true);
        configureKeyboardShortcuts();
    }
    /**
     * Handles the cancel button click event by reverting the user interface to the default scene.
     * This method is typically invoked when the user decides not to proceed with the current
     * operation, such as note creation, and wishes
     * to return to the main or previous view of the application.
     * It calls the {@link PrimaryCtrl#showDefaultScene()} method to perform this action.
     */
    public void onCancelClick() {
        defaultCollectionService.setSelectedFile(null);
        pc.showDefaultScene();
    }

    /**
     * Handles the creation of a new note when the create button is clicked.
     * This method retrieves the title of the note from the title text field,
     * initializes the note content as an empty string, and adds the newly
     * created note to the currently selected note collection using
     * the server utilities.
     */
    public void onCreateClick() throws ExecutionException, InterruptedException {
        if(defaultCollectionService.getDefaultCollection().isEmpty())
            return;

        NoteCollection selectedCollection =
                defaultCollectionService.getDefaultCollection().get();

        String title = titleTextField.getText();
        String content = "";

        if(!validationUtils.isValidName(title, errorPane)) {
            titleTextField.clear();
            titleTextField.setPromptText("Invalid name!");
            invalidName = true;
            return;
        }

        String errorMessage = "A note with this title already exists in this collection";
        if(validationUtils.isDuplicateNoteTitle(title,
                defaultCollectionService.getDefaultCollection().get(), errorPane, errorMessage)) {
            titleTextField.clear();
            titleTextField.setPromptText("Duplicate title!");
            invalidName = true;
            return;
        }

        Note newNote = new Note(title, content, selectedCollection);
        serverUtils.addNoteToCollection(
                selectedCollection.getId(),
                newNote
        );

        pc.showDefaultScene("note " + title + " created", false);
        pc.webSocketMessage(new Message(UpdateType.ADDED, "", newNote.getId()));
    }

    /**
     * Removes the "Invalid name" prompt text if a key is entered after an
     * invalid name has been entered.
     */
    public void onTitleTextFieldAction() {
        if(!invalidName)
            return;
        titleTextField.setPromptText("");
        invalidName = false;
    }

    /**
     * Refreshes the scene by clearing the title text field.
     */
    public void refresh() {
        defaultCollectionService.setSelectedFile(null);
        titleTextField.setText("");
    }

    /**
     * changes language
     * @param resourceBundle the language
     */
    public void changeLanguage(ResourceBundle resourceBundle) {
        createButton.setText(resourceBundle.getString("create.button"));
        cancelButton.setText(resourceBundle.getString("cancel.button"));
        createText.setText(resourceBundle.getString("create.text"));
    }
}
