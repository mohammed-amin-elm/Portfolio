package client;



import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Pair;
import commons.wsmessage.Message;

import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;

/*
 * -------------------------------------------------------------
 * | JAVA DOC comments are generated with ChatGPT              |
 * -------------------------------------------------------------
 */

/**
 * The PrimaryCtrl class is responsible for setting up and displaying the main
 * application window, including its default scene.
 */
public class PrimaryCtrl {
    private Stage primaryStage;

    private Scene defaultScene;
    private Scene noteCreationScene;
    private Scene collectionEditSelectionScene;
    private Scene noteEditScene;
    private Scene collectionEditScene;
    private Scene fileEditScene;

    private NoteEditCtrl noteEdit;
    private DefaultCtrl defaultCtrl;
    private EditSelectCollectionCtrl collectionEdit;
    private NoteCreationCtrl noteCreation;
    private EditCollectionCtrl editCollection;
    private FileEditCtrl fileEdit;

    private final ObjectProperty<ResourceBundle> language = new SimpleObjectProperty<>();

    /**
     * Sets up the primary stage and applies the default scene.
     * This method receives the primary stage of the application and a Pair containing
     * a DefaultCtrl controller and the root node for the scene. It creates a new scene
     * using the provided root node and displays it on the primary stage.
     *
     * @param primaryStage the main stage of the application
     * @param def          a Pair containing a DefaultCtrl controller
     * @param noteCreation a Pair containing a NoteCreationCtrl controller
     * @param collectionEdit a Pair containing a EditSelectCollectionCtrl
     * @param noteEdit a Pair containing a NoteEditCtrl
     * @param editor a Pair containing a EditCollectionCtrl
     * @param fileEdit a Pair containing a FileEditCtrl
     */
    public void init(Stage primaryStage,
                     Pair<DefaultCtrl, Parent> def,
                     Pair<NoteCreationCtrl, Parent> noteCreation,
                     Pair<EditSelectCollectionCtrl, Parent> collectionEdit,
                     Pair<NoteEditCtrl, Parent> noteEdit,
                     Pair<EditCollectionCtrl, Parent> editor,
                     Pair<FileEditCtrl, Parent> fileEdit
    ) {

        this.primaryStage = primaryStage;

        this.defaultCtrl = def.getKey();
        this.defaultScene = new Scene(def.getValue());
        this.noteCreationScene = new Scene(noteCreation.getValue());
        this.noteCreation = noteCreation.getKey();
        this.collectionEditSelectionScene = new Scene(collectionEdit.getValue());
        this.collectionEdit = collectionEdit.getKey();
        this.noteEditScene = new Scene(noteEdit.getValue());
        this.fileEditScene = new Scene(fileEdit.getValue());
        this.noteEdit = noteEdit.getKey();
        this.editCollection = editor.getKey();
        this.collectionEditScene = new Scene(editor.getValue());
        this.fileEdit = fileEdit.getKey();

        defaultScene.getStylesheets().add(getClass()
                .getResource("/client/style/defaultScene.css").toExternalForm());

        collectionEditSelectionScene.getStylesheets().add(getClass()
                .getResource("/client/style/editSelectCollectionScene.css").toExternalForm());

        fileEditScene.getStylesheets().add(getClass()
                .getResource("/client/style/FileEditScene.css").toExternalForm());

        noteEditScene.getStylesheets().add(getClass()
                .getResource("/client/style/editNoteScene.css").toExternalForm());

        collectionEditScene.getStylesheets().add(getClass()
                .getResource("/client/style/EditCollection.css").toExternalForm());

        noteCreationScene.getStylesheets().add(getClass()
                .getResource("/client/style/createNoteScene.css").toExternalForm());

        language.addListener((observable, oldValue, newValue) -> {
            this.collectionEdit.changeLanguage(newValue);
            this.defaultCtrl.changeLanguage(newValue);
            this.editCollection.changeLanguage(newValue);
            this.noteCreation.changeLanguage(newValue);
            this.noteEdit.changeLanguage(newValue);
            this.fileEdit.changeLanguage(newValue);
        });
        //setLanguage(ResourceBundle.getBundle("messages", Locale.ENGLISH));

        showDefaultScene();
        primaryStage.show();
    }

    /**
     * sets the language for all scenes
     * @param resourceBundle language
     */
    public void setLanguage(ResourceBundle resourceBundle) {
        this.language.set(resourceBundle);
    }

    /**
     * displays the collection name change editor
     */
    public void showCollectionEditScene() {
        primaryStage.setTitle("Collection Edit");
        primaryStage.setScene(collectionEditScene);
        editCollection.refresh();
    }

    /**
     * Displays the note edit scene
     */
    public void showNoteEditScene() {
        primaryStage.setTitle("Note Edit Selection");
        primaryStage.setScene(noteEditScene);
        noteEdit.refresh();
    }

    /**
     * Displays the default scene on the primary stage.
     * Sets the stage title to "Default Scene" and applies the default scene
     * created during initialization.
     */
    public void showDefaultScene() {
        primaryStage.setTitle("Default Scene");
        primaryStage.setScene(defaultScene);
        defaultCtrl.refresh();
    }

    /**
     * Displays the current scene, and also displays a popup with an alert or message
     * @param message the message to be displayed
     * @param alert if the message is an alert or not
     */
    public void showDefaultScene(String message, boolean alert) {
        showDefaultScene();
        defaultCtrl.messageFromOtherScene(message, alert);
    }

    /**
     * Acts as a middle man when another scene has to broadcast a message
     * @param message the message
     * @throws ExecutionException ExecutionException
     * @throws InterruptedException InterruptedException
     */
    public void webSocketMessage(Message message) throws ExecutionException, InterruptedException {
        defaultCtrl.webSocketMessageFromOtherScene(message);
    }

    /**
     * Displays the note creation scene on the primary stage.
     * Sets the stage title to "Note Creation" and applies the note creation scene,
     * allowing users to create a new note.
     * This method does not return any value and directly modifies the stage's state.
     */
    public void showNoteCreationScene() {
        primaryStage.setTitle("Note Creation");
        primaryStage.setScene(noteCreationScene);
        noteCreation.refresh();
    }

    /**
     * switches the scene to the collection editor.
     */
    public void showCollectionEditSelectionScene() {
        primaryStage.setTitle("Collection Edit Selection");
        primaryStage.setScene(collectionEditSelectionScene);
        collectionEdit.refresh();
    }

    /**
     * Switches the scene to the file editor.
     */
    public void showFileEditScene() {
        primaryStage.setTitle("File Edit");
        primaryStage.setScene(fileEditScene);
        fileEdit.refresh();
    }
}
