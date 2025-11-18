package client;

import client.utils.FileStorageServerUtils;
import commons.File;
import commons.Note;
import jakarta.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;

import java.util.ResourceBundle;

public class FileEditCtrl {
    private final PrimaryCtrl pc;
    private final DefaultCollectionService defaultCollectionService;
    private final FileStorageServerUtils fileStorageServerUtils;
    private final DefaultCtrl defaultCtrl;

    @FXML
    private TextField fileName;
    @FXML
    private Button renameButton;
    @FXML
    private Button doneButton;
    @FXML
    private GridPane gridPane;
    @FXML
    private Label fileRename;

    /**
     * Constructor for the FileEditCtrl.
     *
     * @param pc                         The Primary Controller to switch between scenes
     * @param defaultCollectionService   The default collection service
     * @param fileStorageServerUtils     The file storage server utilities
     * @param defaultCtrl                The Default Controller
     */
    @Inject
    public FileEditCtrl(PrimaryCtrl pc,
                        DefaultCollectionService defaultCollectionService,
                        FileStorageServerUtils fileStorageServerUtils,
                        DefaultCtrl defaultCtrl) {
        this.pc = pc;
        this.defaultCollectionService = defaultCollectionService;
        this.fileStorageServerUtils = fileStorageServerUtils;
        this.defaultCtrl = defaultCtrl;
    }

    /**
     * This method is executed when the doneButton is clicked.
     * It switches back to the defaultScene.
     */
    public void returnToDefaultScene() {
        pc.showDefaultScene();
    }

    /**
     * This method is executed when the renameButton is clicked.
     * It renames the selected file to the value entered in the TextField.
     */
    public void renameFile() {
        String newFileName = fileName.getText();

        File oldFile = defaultCollectionService.getSelectedFile();
        Note note = defaultCollectionService.getSelectedNote();

        if(note == null) {
            defaultCtrl.messageFromOtherScene("No note selected!", true);
            return;
        }

        if (oldFile == null) {
            defaultCtrl.messageFromOtherScene("No file selected!", true);
            return;
        }

        if(note.getFiles().stream().map(File::getFileName).toList().contains(newFileName)) {
            defaultCtrl.messageFromOtherScene("File already exists!", true);
            return;
        }

        File newFile = new File(newFileName, oldFile.getContentType(), oldFile.getNote());

        fileStorageServerUtils.renameFile(oldFile.getId(), newFile);

        pc.showDefaultScene();
        defaultCtrl.messageFromOtherScene("File successfully renamed", false);
    }

    /**
     * This method sets the filename of the selected file as the default value for the TextField.
     */
    public void refresh() {
        fileName.setText(defaultCollectionService.getSelectedFile().getFileName());
    }

    /**
     * Configures the keyboard shortcut (Ctrl + K or Cmd + K) to navigate to the default scene.
     * This allows users to quickly return to the main scene using the keyboard.
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
                fileName.requestFocus();
                event.consume();
            }
            if ((event.getCode() == KeyCode.ENTER)){
                System.out.println("Enter pressed");
                try {
                    returnToDefaultScene();
                    event.consume();
                } catch (Exception e) {
                    System.err.println("An error occurred while creating a note: "
                            + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
        fileName.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                System.out.println("Enter pressed");
                try {
                    returnToDefaultScene();
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
     * changes language
     * @param resourceBundle the language
     */
    public void changeLanguage(ResourceBundle resourceBundle) {
        renameButton.setText(resourceBundle.getString("rename.button"));
        doneButton.setText(resourceBundle.getString("done.button"));
        fileRename.setText(resourceBundle.getString("file.rename.text"));
    }
}
