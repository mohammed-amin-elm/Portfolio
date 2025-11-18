package client;

import client.utils.FileStorageServerUtils;
import client.utils.PopupMessages;
import client.utils.ServerUtils;
import client.utils.ValidationUtils;
import commons.NoteCollection;
import jakarta.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.ResourceBundle;

public class EditCollectionCtrl {
    private final PrimaryCtrl primaryCtrl;
    private final ServerUtils serverUtils;
    private final DefaultCollectionService defaultCollectionService;
    private final ValidationUtils validationUtils;
    private final PopupMessages popupMessages;
    private final FileStorageServerUtils fileStorageServerUtils;

    @FXML
    private VBox vBox;
    @FXML
    private TextField textbox;
    @FXML
    private AnchorPane errorMessagePane;
    @FXML
    private Label serverStateLabel;
    @FXML
    private Text renameText;

    @FXML
    private GridPane gridPane;
    @FXML
    private Button cancelButton;
    @FXML
    private Button confirmButton;

    /**
     * Constructor
     * @param primaryCtrl primaryCtrl
     * @param serverUtils serverUtils
     * @param defaultCollectionService defaultCollectionService
     * @param validationUtils validationUtils
     * @param fileStorageServerUtils fileStorageServerUtils
     * @param popupMessages popupMessages
     */
    @Inject
    public EditCollectionCtrl( PrimaryCtrl primaryCtrl,
                               ServerUtils serverUtils,
                               DefaultCollectionService defaultCollectionService,
                               ValidationUtils validationUtils,
                               FileStorageServerUtils fileStorageServerUtils,
                               PopupMessages popupMessages) {
        this.primaryCtrl = primaryCtrl;
        this.serverUtils = serverUtils;
        this.defaultCollectionService = defaultCollectionService;
        this.validationUtils = validationUtils;
        this.fileStorageServerUtils = fileStorageServerUtils;
        this.popupMessages = popupMessages;
    }

    /**
     * Configures the keyboard shortcuts.
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
                textbox.requestFocus();
                event.consume();
            }
            if ((event.getCode() == KeyCode.ENTER)){
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
        textbox.setOnKeyPressed(event -> {
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
     * Refreshes the scene on enter by setting the textbox text to the title of the
     * collection to edit. If no collection is selected, it navigates to the collection
     * edit selection scene.
     */
    public void refresh() {
        if (defaultCollectionService.getCollectionToEdit().isEmpty()) {
            primaryCtrl.showCollectionEditSelectionScene();
            return;
        }
        textbox.setText(defaultCollectionService.getCollectionToEdit().get().getTitle());

        if(serverUtils.isServerAvailable()) {
            serverStateLabel.setText("Server is reachable!");
        } else {
            serverStateLabel.setText("Server is not reachable!");
        }
    }

    /**
     * handler for the confirm button
     */
    public void onConfirmClick() {
        String newTitle = textbox.getText();

        if(!validationUtils.isValidName(newTitle, errorMessagePane))
            return;
        if(validationUtils.isDuplicateTitle(newTitle,
                serverUtils.getAllCollections().stream().map(NoteCollection::getTitle).toList(),
                errorMessagePane, "A collection with this title already exists."))
            return;

        defaultCollectionService.getCollectionToEdit().get().setTitle(newTitle);

        fileStorageServerUtils.updateNoteCollectionDirectory(
                defaultCollectionService.getCollectionToEdit().get().getId(),
                defaultCollectionService.getCollectionToEdit().get()
        );

        serverUtils.updateNoteCollection(
                defaultCollectionService.getCollectionToEdit().get().getId(),
                defaultCollectionService.getCollectionToEdit().get()
        );
        primaryCtrl.showCollectionEditSelectionScene();
    }

    /**
     * Handles the cancel button click by clearing the collection to edit and
     * navigating to the collection edit selection scene.
     */
    public void onCancelClick() {
        defaultCollectionService.setCollectionToEdit(null);
        primaryCtrl.showCollectionEditSelectionScene();
    }

    /**
     * changes language
     * @param resourceBundle the language
     */
    public void changeLanguage(ResourceBundle resourceBundle) {
        cancelButton.setText(resourceBundle.getString("cancel.button"));
        confirmButton.setText(resourceBundle.getString("confirm.button"));
        renameText.setText(resourceBundle.getString("rename.text"));
    }
}
