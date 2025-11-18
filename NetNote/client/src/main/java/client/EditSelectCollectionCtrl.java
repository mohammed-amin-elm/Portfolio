package client;

import client.utils.FileStorageServerUtils;
import client.utils.PopupMessages;
import client.utils.ValidationUtils;
import commons.NoteCollection;
import client.utils.ServerUtils;
import jakarta.inject.Inject;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;


///Javadoc done with the help of CHATGPT

public class EditSelectCollectionCtrl {
    private final PrimaryCtrl primaryCtrl;
    private final ServerUtils serverUtils;
    private final DefaultCollectionService defaultCollectionService;
    private final FileStorageServerUtils fileStorageServerUtils;
    private final ValidationUtils validationUtils;
    private final PopupMessages popupMessages;

    private NoteCollection selectedCollection;

    @FXML
    private GridPane gridPane;
    @FXML
    private TextField editTitleBox;
    private TextField editServerBox = new TextField("");
    private TextField editInternalNameBox = new TextField("");
    private Label serverStatusLabel = new Label();
    @FXML
    private ListView<NoteCollection> collectionListView;

    private List<NoteCollection> collectionList;
    private boolean invalidName = false;


    @FXML
    private Button deleteButton;
    @FXML
    private Button doneButton;
    @FXML
    private Button addButton;
    @FXML
    private Button confirmButton;
    @FXML
    private Label collectionsLabel;
    @FXML
    private Text selectedLabel;

    @FXML
    private AnchorPane errorPane;

    @FXML
    private ComboBox<String> languageComboBox;;

    /**
     * Constructor for this scene controller.
     *
     * @param primaryCtrl              the primary controller to manage scene transitions
     * @param serverUtils              utility class for server interactions
     * @param defaultCollectionService service for managing the default collection
     * @param fileStorageServerUtils service for handling files
     * @param popupMessages popupMessages
     * @param validationUtils validationUtils
     */
    @Inject
    public EditSelectCollectionCtrl(
            PrimaryCtrl primaryCtrl,
            ServerUtils serverUtils,
            @Autowired DefaultCollectionService defaultCollectionService,
            FileStorageServerUtils fileStorageServerUtils,
            PopupMessages popupMessages,
            ValidationUtils validationUtils) {
        this.primaryCtrl = primaryCtrl;
        this.serverUtils = serverUtils;
        this.defaultCollectionService = defaultCollectionService;
        this.fileStorageServerUtils = fileStorageServerUtils;
        this.validationUtils = validationUtils;
        this.popupMessages = popupMessages;
    }

    /**
     * Sets up a keyboard shortcut (Ctrl + K or Cmd + K) to navigate to the default scene.
     * This allows users to quickly return to the main scene using the keyboard.
     */
    private void configureKeyboardShortcuts() {
        gridPane.setOnKeyPressed(event -> {
            if ((event.isControlDown() || event.isMetaDown()) && event.getCode() == KeyCode.K) {
                System.out.println("Ctrl/Cmd + K pressed");
                primaryCtrl.showDefaultScene();
                event.consume();
            }
            if ((event.getCode() == KeyCode.ESCAPE)){
                System.out.println("esc pressed");
                editTitleBox.requestFocus();
                event.consume();
            }
            if ((event.getCode() == KeyCode.ENTER)){
                System.out.println("Enter pressed");
                try {
                    onAddClick();
                    event.consume();
                } catch (Exception e) {
                    System.err.println("An error occurred while creating a note: "
                            + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
        editTitleBox.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                System.out.println("Enter pressed");
                try {
                    onAddClick();
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
     * Initializes the controller by populating the ListView with available note collections
     * and configuring keyboard shortcuts.
     */
    public void initialize() {
        collectionListView.setItems(
                FXCollections.observableList(serverUtils.getAllCollections())
        );
        configureKeyboardShortcuts();
        editTitleBox.toFront();
        editTitleBox.requestFocus();
    }

    /**
     * Refreshes the view by clearing the input fields and updating the ListView
     * with the latest collections from the server.
     */
    public void refresh() {
        editTitleBox.setText("");
        collectionListView.setItems(
                FXCollections.observableList(serverUtils.getAllCollections())
        );
        collectionListView.getSelectionModel().clearSelection();
        selectedCollection = null;
        defaultCollectionService.setCollectionToEdit(null);
        editTitleBox.requestFocus();
    }

    /**
     * Navigates to the collection edit scene, updates the ListView, and prepares the UI.
     */
    public void onConfirmClick() {
        if(defaultCollectionService.getCollectionToEdit().isEmpty()) {
            popupMessages.showFadeInAlert(errorPane, "Please select a collection!");
            return;
        }

        primaryCtrl.showCollectionEditScene();
        // refresh();
    }

    /**
     * Deletes the selected collection from the server and updates the ListView.
     */
    public void onDeleteClick() {
        if (selectedCollection == null) {
            popupMessages.showFadeInAlert(errorPane, "Please select a collection!");
            return;
        }

        boolean confirmed = popupMessages.showDeleteConfirmationDialog(
                "Are your sure you want to delete this collection?");
        if(confirmed) {
            fileStorageServerUtils.deleteNoteCollectionDirectory(selectedCollection.getId());
            serverUtils.deleteNoteCollection(selectedCollection.getId());
            refresh();
        }
    }

    /**
     * Handles the selection of a collection from the ListView and updates the selected collection.
     */
    public void onSidebarCollectionClick() {
        selectedCollection = collectionListView.getSelectionModel().getSelectedItem();

        if(selectedCollection == null)
            return;

        defaultCollectionService.setCollectionToEdit(selectedCollection);
        System.out.println(selectedCollection);
    }

    /**
     * Navigates back to the default scene.
     * Logs the current state of the
     * DefaultCollectionService.
     */
    public void onDoneClicked() {
        System.out.println(defaultCollectionService);
        defaultCollectionService.setSelectedFile(null);

        primaryCtrl.showDefaultScene();
    }

    /**
     * Creates a new note collection with the
     * specified details and updates the ListView.
     */
    public void onAddClick() {
        String title = editTitleBox.getText();
        String url = editServerBox.getText();
        String internalName = editInternalNameBox.getText();

        if(!validationUtils.isValidName(title, errorPane)) {
            editTitleBox.clear();
            editTitleBox.setPromptText("Invalid name!");
            invalidName = true;
            return;
        }

        collectionList = serverUtils.getAllCollections();
        if (!serverUtils.isServerAvailable()) {
            serverStatusLabel.setText("Server is not available");
        } else if (serverUtils.containsTitle(title)) {
            serverStatusLabel.setText("Collection already exists");
            popupMessages.showFadeInAlert(errorPane, "Collection already exists");
        } else {
            serverStatusLabel.setText("Adding new collection");
            popupMessages.showFadeInMessage(errorPane, "Collection added!");
            serverUtils.newNoteCollection(
                    new NoteCollection(title, url, internalName)
            );
        }
        refresh();

    }

    /**
     * Clears the "Invalid name" prompt text after an invalid entry.
     */
    public void onEditTitleBoxAction() {
        if (!invalidName) {
            return;
        }
        editTitleBox.setPromptText("");
        invalidName = false;
    }


    /**
     * Handles language selection and updates the application's language
     * based on the selected value from the ComboBox.
     */
    @FXML
    private void onLanguageSelected() {
        String selectedLanguage = languageComboBox.getValue();

        primaryCtrl.setLanguage(getResourceBundle(selectedLanguage));
    }

    private ResourceBundle getResourceBundle(String language) {
        Locale dutch = new Locale("nl", "NL");

        return switch (language) {
            case "French" -> ResourceBundle.getBundle("messages", Locale.FRENCH);
            case "Italian" -> ResourceBundle.getBundle("messages", Locale.ITALIAN);
            case "German" -> ResourceBundle.getBundle("messages", Locale.GERMAN);
            case "Dutch" -> ResourceBundle.getBundle("messages", dutch);
            default -> ResourceBundle.getBundle("messages", Locale.ENGLISH);
        };
    }


    /**
     * Updates the text of all buttons in the application using the current resource bundle.
     * The resource bundle must be properly initialized before calling this method.
     * @param resourceBundle the resource bundle
     */
    public void changeLanguage(ResourceBundle resourceBundle) {
        if(deleteButton == null || doneButton == null
                || addButton == null || confirmButton == null) {
            return;
        }

        deleteButton.setText(resourceBundle.getString("delete.button"));
        doneButton.setText(resourceBundle.getString("done.button"));
        addButton.setText(resourceBundle.getString("add.button"));
        confirmButton.setText(resourceBundle.getString("edit.button"));
        collectionsLabel.setText(resourceBundle.getString("collections.label"));
        selectedLabel.setText(resourceBundle.getString("selected.label"));
    }

}
