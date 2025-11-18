package client;

import client.config.Config;
import client.config.ConfigManager;
import client.utils.MyStompClient;
import client.utils.FileStorageServerUtils;
import client.utils.ServerUtils;
import client.utils.*;
import commons.Note;
import commons.NoteCollection;
import jakarta.inject.Inject;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;

import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javafx.util.Duration;
import netscape.javascript.JSObject;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import commons.wsmessage.Message;
import commons.wsmessage.UpdateType;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/*
 * -------------------------------------------------------------
 * | JAVA DOC comments are generated with ChatGPT              |
 * -------------------------------------------------------------
 */

/**
 * The Markdown Controller Class is responsible for fetching text from the TextArea
 * and rendering the text in markdown on a WebView object.
 */
public class DefaultCtrl implements Initializable {

    private final PrimaryCtrl pc;
    private final ServerUtils serverUtils;
    private final FileStorageServerUtils fileStorageServerUtils;
    private final DefaultCollectionService defaultCollectionService;
    private final ValidationUtils validationUtils;
    private final PopupMessages popupMessages;
    private final ConfigManager configManager;
    private Config config;

    @FXML
    private Button createNote;
    @FXML
    private Button refreshNote;
    @FXML
    private Button editNote;
    @FXML
    private Button deleteNote;
    @FXML
    private Button collectionSceneButton;
    @FXML
    private Label filesLabel;

    /// Tools for markdownInput
    @FXML
    private TextArea markdownInput;
    @FXML
    private WebView htmlOutput;
    private WebEngine webEngine;

    /// Tools for sidebar
    @FXML
    private ListView<Note> noteListView;

    /// Tools for searchbar
    @FXML
    private TextField searchbar;
    @FXML
    private ScrollPane notepadTitles;


    /// Tools for tags
    @FXML
    private ScrollBar scrollBar;
    @FXML
    private Button clearTagsButton;
    @FXML
    private AnchorPane tagPane;
    private TagList tags;

    /// Pane used for error and confirmation messages
    @FXML
    private AnchorPane textPane;

    private ObservableList<Note> notes = FXCollections.observableArrayList();
    private ObservableList<String> collectionTitles = FXCollections.observableArrayList();

    /// Tools for markdown render
    private final Parser markdownParser = Parser.builder().build();
    private final HtmlRenderer htmlRenderer = HtmlRenderer.builder().build();

    /// Tools for dropdown menu
    @FXML
    private ComboBox<String> collectionTitleComboBox;

    /// Tools for keyboard shortcuts
    @FXML
    private GridPane gridPane;

    /// Tools for web sockets:
    private MyStompClient stompClient;
    /// Tools for embedded files
    @FXML
    private Button uploadButton;
    @FXML
    private Button fileRefreshButton;
    @FXML
    private Button fileDeleteButton;
    @FXML
    private Button fileSaveButton;
    @FXML
    private Button fileEditButton;
    @FXML
    private HBox fileBar;

    /**
     * Constructs a new DefaultCtrl instance, initializing the controller with
     * references to a PrimaryCtrl and ServerUtils. It sets up the initial state
     * by retrieving all collections and selecting the first collection and its
     * first note, if available. It also initializes update mechanisms and sets
     * the note list view.
     *
     * @param pc         The PrimaryCtrl instance responsible for managing the
     *                   application's primary stage and scenes.
     * @param serverUtils The ServerUtils instance for handling server-related
     *                   operations, including retrieving collections and notes.
     * @param defaultCollectionService the class that manages the default collection
     */
    @Inject
    private DefaultCtrl(PrimaryCtrl pc,
                        ServerUtils serverUtils,
                        DefaultCollectionService defaultCollectionService,
                        FileStorageServerUtils fileStorageServerUtils,
                        ConfigManager configManager,
                        Config config,
                        ValidationUtils validationUtils,
                        PopupMessages popupMessages)
            throws ExecutionException, InterruptedException {
        stompClient = new MyStompClient(this);
        stompClient.sendMessage(new Message(UpdateType.UNKNOWN, "Hello world!", 1));

        this.pc = pc;
        this.serverUtils = serverUtils;
        this.fileStorageServerUtils = fileStorageServerUtils;
        this.defaultCollectionService = defaultCollectionService;
        this.configManager = configManager;
        this.config = config;
        this.validationUtils = validationUtils;
        this.popupMessages = popupMessages;
    }



    /**
     * Initializes and starts a background thread that periodically updates the
     * currently selected note's content in both the backend and frontend. The thread
     * runs an infinite loop with a 5-second sleep interval between updates. If no note
     * is selected, it skips the update process. When a note is selected, it constructs
     * a new note object with updated content from the markdown input, sends this updated
     * note to the server to reflect the changes in the backend, and then updates the
     * selected note in the frontend to display the new content.
     */
    private void initUpdateThread() {
        Thread updateThread = new Thread(() -> {
            while (true) {

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Note selectedNote = defaultCollectionService
                        .getSelectedNote();

                if(selectedNote == null)
                    continue;

                Note newNote = new Note (
                        selectedNote.getTitle(),
                        markdownInput.getText(),
                        defaultCollectionService.getDefaultCollection().orElse(null)
                );

                System.out.println("HELLO. UPDATING " + selectedNote
                        + " with id " + selectedNote.getId());

                // this updates in the backend
                serverUtils.updateNote(selectedNote.getId(), newNote);

                // this updated in the frontend
                selectedNote.updateNote(newNote);
            }
        });
        updateThread.setDaemon(true);
        updateThread.start();
    }

    private String getStyledMarkdown(String htmlContent) {
        // Path to your local CSS file
        String cssFilePath = Paths.get("config.css").toUri().toString();
        System.out.println("cssFilePath: " + cssFilePath);

        // Embed CSS into HTML
        return  """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Note</title>
                    <link rel="stylesheet" type="text/css" href="%s">
                </head>
                <body>
                    %s
                </body>
                </html>
                """.formatted(cssFilePath, htmlContent);
    }

    String getScript() {
        return
                """
                document.addEventListener('click', function(event) {
                    var target = event.target;
                    while (target != null) {
                        if (target.tagName === 'A') {
                            event.preventDefault();
                            var href = target.getAttribute("href");
                            window.javaApp.onLinkClick(href);
                            break;
                        }
                        if(target.tagName === 'BUTTON') {
                            event.preventDefault();
                            var href = target.getAttribute("href");
                            window.javaApp.onButtonClick(href);
                            break;
                        }
                        target = target.parentElement;
                    }
                });
                """;
    }


    /**
     * Initializes listeners and UI components for the scene.
     * Sets up key event shortcuts, markdown input handling,
     * search bar behavior, and default collection.
     *
     * @param url The URL location of the FXML file.
     * @param rb The resource bundle for the FXML file.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        System.out.println(stompClient.getSessionId());
        gridPane.setFocusTraversable(true);
        markdownInput.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                stompClient.sendMessage(
                        new Message(
                                UpdateType.CONTENT_CHANGE,
                                newValue,
                                defaultCollectionService.getSelectedNote().getId()
                        )
                );
            } catch (Exception e) {}
        });
        webEngine = htmlOutput.getEngine();
        webEngine.getLoadWorker().stateProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue == Worker.State.SUCCEEDED) {
                        JSObject window = (JSObject) webEngine.executeScript("window");
                        window.setMember("javaApp", this);
                        String script = getScript();

                        webEngine.executeScript(script);
                    }
                }
        );

        configManager.loadFromFileInto(config);
        //System.out.println(config);

        tags = new TagList(this, defaultCollectionService, config,
                serverUtils, tagPane, scrollBar, clearTagsButton);

        if(defaultCollectionService.getDefaultCollection().isPresent()) {
            refreshClick();
        }
        noteListView.setItems(notes);
        initUpdateThread();
        this.searchbar.focusedProperty().addListener((observableValue, oldFocused, newFocused) -> {

            List<Node> focusedButtons = notepadTitles.getChildrenUnmodifiable().stream()
                    .filter(Node::isFocused)
                    .toList();

            if(!newFocused && focusedButtons.isEmpty() && notepadTitles.isFocused())
                closeSearchbarDropDown();
        });
        configureKeyboardShortcuts();

        collectionTitles = FXCollections.observableArrayList(
                config.getNoteCollections().stream()
                        .map(NoteCollection::getTitle)
                        .toList()
        );
        collectionTitleComboBox.setItems(collectionTitles);

        tags.setNotes(notes);
    }

    /**
     * Configures the key press events for the gridPane.
     */
    private void configureKeyboardShortcuts() {
        gridPane.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                searchbar.requestFocus();
                event.consume();
            }
            if (event.getCode() == KeyCode.ENTER) {
                markdownInput.requestFocus();
                event.consume();
            }
            if ((event.isControlDown() || event.isMetaDown()) && !event.isShiftDown()
                    && event.getCode() == KeyCode.N) {
                createClick();
                event.consume();
            }
            if ((event.isControlDown() || event.isMetaDown()) && event.isShiftDown()
                    && event.getCode() == KeyCode.N) {
                onCollectionClick();
                event.consume();
            }
            if ((event.isControlDown() || event.isMetaDown()) && event.getCode()
                    == KeyCode.EQUALS) {
                moveToNextNote();
                event.consume();
            }
            if ((event.isControlDown() || event.isMetaDown()) && event.getCode() == KeyCode.E) {
                onEditClick();
                event.consume();
            }
            if ((event.isControlDown() || event.isMetaDown()) && event.getCode() == KeyCode.D) {
                try { deleteClick(); } 
                catch (Exception e) { e.printStackTrace(); }
                event.consume();
            }
            if ((event.isControlDown() || event.isMetaDown()) && event.getCode() == KeyCode.R) {
                refreshClick();
                event.consume();
            }
            if ((event.isControlDown() || event.isMetaDown()) && event.getCode() == KeyCode.MINUS) {
                moveToPreviousNote();
                event.consume();
            }
            if ((event.isControlDown() || event.isMetaDown()) && event.getCode() == KeyCode.M) {
                onMenuClick();
                event.consume();
            }
        });
        noteListView.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                searchbar.requestFocus();
                event.consume();
            }
        });
    }
    /**
     * Selects the next note in the list and triggers the action for the selected note.
     */
    private void moveToNextNote() {
        int currentIndex = noteListView.getSelectionModel().getSelectedIndex();
        int nextIndex = (currentIndex + 1) % notes.size();
        System.out.println(nextIndex + " plus");
        noteListView.getSelectionModel().select(nextIndex);
        Note note = notes.get(nextIndex);
        onNoteButtonPressed(note);
    }

    /**
     * Selects the previous note in the list and triggers the action for the selected note.
     */
    private void moveToPreviousNote() {
        int currentIndex = noteListView.getSelectionModel().getSelectedIndex();
        int previousIndex = (currentIndex - 1 + notes.size()) % notes.size();
        System.out.println(previousIndex + " minus");
        noteListView.getSelectionModel().select(previousIndex);
        Note note = notes.get(previousIndex);
        onNoteButtonPressed(note);
    }

    /**
     * This method takes a match result of a file pattern "![embedded](filename){}"
     * and gives the file content if it exists.
     * @param matchResult The match result of the filePattern
     * @return the string based on the match result
     */
    private String handleFilePattern(MatchResult matchResult) {
        List<commons.File> fileList = serverUtils.getNote(
                defaultCollectionService.getSelectedNote().getId()
        ).getFiles();
        List<String> fileNames = fileList.stream()
                .map(commons.File::getFileName).toList();

        String fileName = matchResult.group(2);

        if(!(fileNames.contains(fileName)))
            return "<p style='color:red'>FILE NOT UPLOADED</p>";

        commons.File file = fileList.get(fileNames.indexOf(fileName));
        Optional<String> fileContent = serverUtils.getFileContent(
                defaultCollectionService.getSelectedNote().getId(),
                fileName
        );

        if(fileContent.isEmpty())
            return "<p style='color:red'>FILE NOT FOUND ON SERVER</p>";

        if(matchResult.group(3) == null)
            return fileContent.get();

        String size = matchResult.group(3);
        if(!(size.contains(";"))) {
            return new StringBuilder(fileContent.get())
                    .insert(fileContent.get().length()-2, " style='width:" + size + "px' ")
                    .toString();
        }

        String[] sizes = size.split(";");
        return new StringBuilder(fileContent.get())
                .insert(fileContent.get().length()-2,
                        " style='width:" + sizes[0] + "px;height:" + sizes[1] + "px' ")
                .toString();
    }

    /**
     * The renderMarkdown method takes in a string and parses the string from markdown to html.
     * Then it renders the html on a WebView object.
     *
     * @param markdown The markdown string to parse
     */
    private void renderMarkdown(String markdown) {

        Pattern linkPattern = Pattern.compile("\\[\\[(.*?)]]");
        Pattern tagPattern = Pattern.compile("#(\\w+)");
        Pattern filePattern = Pattern
                .compile("!\\[([^]]*)]\\(([^)]+)\\)(?:\\{([0-9]+(?:;[0-9]+)?)})?");

        Matcher linkMatcher = linkPattern.matcher(markdown);

        String markdownWithLinks = linkMatcher.replaceAll(matchResult -> {
            String linkedNote = matchResult.group(1);

            if(!validationUtils.sameCollection(linkedNote,
                    defaultCollectionService.getSelectedNote(),
                    serverUtils.getAllCollections())) {
                return "<strong> <i style='color:red'>" + linkedNote + "</i> </strong>";
            }

            return "<a href='note:" + linkedNote + "'>" + linkedNote + "</a>";
        });

        Matcher tagMatcher = tagPattern.matcher(markdownWithLinks);
        String linkTagMarkdown = tagMatcher.replaceAll(matchResult -> {
            String tag = matchResult.group(1);
            return "<button href='" + tag + "'>" + tag + "</button>";
        });

        Matcher fileMatcher = filePattern.matcher(linkTagMarkdown);
        String finalMarkdown = fileMatcher.replaceAll(this::handleFilePattern);

        String html = getStyledMarkdown(htmlRenderer.render(markdownParser.parse(finalMarkdown)));

        webEngine.loadContent(html);
        //markdownInput.positionCaret(markdownInput.getText().length());
    }

    /**
     * When a link is clicked, the javascript code will
     * call this java function
     * @param link the href link
     */
    public void onLinkClick(String link) {
        System.out.println("Link to " + link + " clicked");
        if(link == null || !link.startsWith("note:"))
            return;

        String title = link.split(":")[1];
        openNoteByTitle(title);
    }

    /**
     * When a tag is clicked, the javascript code will call this java function
     * @param button the href link
     */
    public void onButtonClick(String button) {
        System.out.println("Button " + button + " clicked");
        tags.select(button);
    }


    /**
     * Opens a note by its title, used for note links
     * @param title the title of the note
     */
    public void openNoteByTitle(String title) {
        Note note = serverUtils.getNotes().stream()
                .filter(x -> x.getTitle().equals(title))
                .findFirst()
                .orElse(null);


        if(note == null)
            return;


        note.setNoteCollection(
                serverUtils.getAllCollections().stream()
                        .filter(c -> {
                            for(Note n: c.getNotes())
                                if(n.getId() == note.getId())
                                    return true;
                            return false;
                        })
                        .findFirst()
                        .orElse(null)
        );

        openNote(note);
    }

    /**
     * Method to display a note
     * @param selectedNote note to be displayed
     */
    public void openNote(Note selectedNote) {
        if(selectedNote == null)
            return;

        serverUtils.addCollectionToNote(selectedNote);

        defaultCollectionService.setSelectedNote(selectedNote);
        configManager.saveToFile(config);

        markdownInput.setText(selectedNote.getContent());
        renderMarkdown(markdownInput.getText());
        refreshFiles();
        System.out.println(selectedNote.getCollection());

        if(defaultCollectionService.getDefaultCollection().isEmpty())
            return;

        if(defaultCollectionService.getDefaultCollection().get().getId()
                != selectedNote.getCollection().getId()) {
            defaultCollectionService.setDefaultCollection(selectedNote.getCollection());
            tags.clearTags();
            refreshClick();
        }
    }

    /**
     * This method is executed everytime a key is typed on the TextArea object. The entire text
     * inside the TextArea is the rendered on the WebView.
     */
    public void onTextAreaKeyTyped() {
        renderMarkdown(markdownInput.getText());
    }

    /**
     * Handler for the Create button. This is supposed to open the note creating scene.
     */
    @FXML
    public void createClick() {
        if(defaultCollectionService.getDefaultCollection().isEmpty()) {
            popupMessages.showFadeInAlert(textPane, "Please select a collection");
            return;
        }

        fileBar.getChildren().clear();
        pc.showNoteCreationScene();
    }

    /**
     * Handler for the Refresh button. Clears the notes and refreshes the ListView.
     */
    @FXML
    public void refreshClick() {
        tags.setTags();
        if(defaultCollectionService.getDefaultCollection().isPresent())
            defaultCollectionService.setDefaultCollection(
                    serverUtils.getNoteCollectionById(
                            defaultCollectionService.getDefaultCollection().get().getId()
                    )
            );

        defaultCollectionService.setSelectedNote(null);
        configManager.saveToFile(config);
        markdownInput.setText("");
        htmlOutput.getEngine().loadContent("");

        System.out.println("Before refresh: " + notes);

        notes.setAll(
                defaultCollectionService.getDefaultCollection()
                        .map(NoteCollection::getNotes)
                        .orElse(serverUtils.getNotes())
        );

        List<NoteCollection> collections = serverUtils.getAllCollections();

        collections.forEach(noteCollection -> noteCollection
                        .getNotes()
                        .forEach(note -> note.setNoteCollection(noteCollection))
                );

        config.setNoteCollections(collections);
        configManager.saveToFile(config);

        System.out.println("After refresh: " + notes);
    }

    /**
     * refreshes the scene on enter
     */
    @FXML
    public void refresh() {
        refreshClick();
        tags.setTags();
        tags.addTagBox();
    }

    /**
     * Handler for the Delete button.
     * Deletes the selected note from the list
     */
    @FXML
    public void deleteClick() throws ExecutionException, InterruptedException {
        if(defaultCollectionService.getSelectedNote() == null){
            popupMessages.showFadeInAlert(textPane, "Please select a note first");
            return;
        }

        boolean confirmed = popupMessages.showDeleteConfirmationDialog(
                "Are you sure you want to delete this note?");

        if(confirmed) {
            Note selectedNote = noteListView.getSelectionModel().getSelectedItem();

            fileStorageServerUtils.deleteNoteDirectory(selectedNote.getId());
            serverUtils.deleteNote(selectedNote.getId());

            stompClient.sendMessage(
                    new Message(UpdateType.DELETED, "", selectedNote.getId())
            );
            refreshClick();
            popupMessages.showFadeInMessage(textPane, "Note deleted successfully");
        }
    }

    /**
     * Event that fires when a key has been pressed, while the searchbar is focused.
     * It then shows the scrolling bar for the results. If the searchbar is empty,
     * the dropdown menu will close.
     */
    @FXML
    public void onSearchbarKeyTyped() {

        String searchKeyword = searchbar.getText();

        if (searchbar.getText().isEmpty())
            closeSearchbarDropDown();
        else
            openSearchbarDropDown();

        List<Note> searchedNotes = sortSelectableNotes(notes, searchKeyword);

        if (searchedNotes.isEmpty()) {
            closeSearchbarDropDown();
            return;
        }

        createSelectableNoteList(searchedNotes);
    }

    /**
     * This function filters the given list based on title and context. After
     * that it adds them together and removes and duplicates.
     *
     * @param notes   The list of notes that needs to get filtered
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
     *
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
     * Makes the resulting notes of the searchbar invisible and makes you unable
     * to interact with it.
     */
    private void closeSearchbarDropDown() {
        notepadTitles.setDisable(true);
        notepadTitles.setVisible(false);
    }

    /**
     * Makes the resulting notes of the searchbar visible and interactable
     */
    private void openSearchbarDropDown() {
        notepadTitles.setDisable(false);
        notepadTitles.setVisible(true);
    }

    /**
     * Handles the event that fires when the note button has been pressed.
     * It clears the search bar and opens the selected note.
     *
     * @param note The note on the button that the user has pressed
     */
    private void onNoteButtonPressed(Note note) {

        searchbar.clear();
        closeSearchbarDropDown();

        markdownInput.setText(note.getContent());
        renderMarkdown(markdownInput.getText());

        defaultCollectionService.setSelectedNote(note);
        configManager.saveToFile(config);
        System.out.println(note.getContent());
    }

    /**
     * Handles the event triggered when a sidebar title is pressed.
     * Updates the currently selected note based on the title selected in the list.
     * Displays the content of the selected note in the markdown input
     * area and renders the markdown to HTML.
     *
     * @param mouseEvent The MouseEvent that triggers this method, typically
     *                   representing a click action on a sidebar title.
     */
    public void onSidebarTitlePressed(MouseEvent mouseEvent) {
        Note selectedNote = noteListView.getSelectionModel().getSelectedItem();

        openNote(selectedNote);
    }

    /**
     * Handler for the collections button
     * Takes the user to the collection editor
     */
    public void onCollectionClick() {
        refreshClick();
        fileBar.getChildren().clear();
        pc.showCollectionEditSelectionScene();
    }

    /**
     * Handler for the edit button
     * Takes the user to the edit note scene
     */
    public void onEditClick() {
        if(defaultCollectionService.getSelectedNote() == null) {
            messageFromOtherScene("Please select a note", true);
            return;
        }

        fileBar.getChildren().clear();
        pc.showNoteEditScene();
    }

    /**
     * Handles the event triggered when the menu button is clicked.
     * It retrieves all available collections from the server
     * and populates the ComboBox with their titles.
     * If the ComboBox is not visible, it will be shown.
     */
    public void onMenuClick(){
        collectionTitles.clear();
        List<NoteCollection> collections = serverUtils.getAllCollections();

        collectionTitles.add("All Notes");
        for (NoteCollection collection : collections) {
            collectionTitles.add(collection.getTitle());
        }
        if (!collectionTitleComboBox.isShowing()) {
            collectionTitleComboBox.show();
        }
    }

    /**
     * Handles the event triggered when a collection is selected from the ComboBox.
     * It updates the current collection to the selected one
     * and refreshes the list of notes displayed
     * in the note list view.
     */
    public void onCollectionSelected() {

        String selectedNoteCollectionTitle = collectionTitleComboBox.getValue();
        if(selectedNoteCollectionTitle == null || selectedNoteCollectionTitle.isEmpty())
            return;
        if(selectedNoteCollectionTitle.equals("All Notes")) {
            defaultCollectionService.setDefaultCollection(null);
            tags.clearTags();
            refreshClick();
            return;
        }

        NoteCollection selectedNoteCollection = null;
        List<NoteCollection> allNotes = serverUtils.getAllCollections();
        for (NoteCollection collection : allNotes) {
            String title = collection.getTitle();
            if (title.equals(selectedNoteCollectionTitle)) {
                selectedNoteCollection = collection;
                break;
            }
        }
        if (selectedNoteCollection != null) {
            refreshClick();
            defaultCollectionService.setDefaultCollection(selectedNoteCollection);
            List<Note> newNotes = selectedNoteCollection.getNotes();
            notes.setAll(newNotes);
            noteListView.setItems(notes);
        }
        tags.clearTags();
    }

    /**
     * Update function for when the tags change
     */
    public void tagsChanged() {
        List<String> selectedTags = tags.getSelectedTags();
        refreshClick();
        notes.setAll(
                notes.stream()
                        .filter(n -> {
                            Pattern tagPattern = Pattern.compile("#(\\w+)");
                            Matcher tagMatcher = tagPattern.matcher(n.getContent());
                            List<String> noteTags = new ArrayList<>();
                            while(tagMatcher.find()) {
                                String tag = tagMatcher.group(1);
                                noteTags.add(tag);
                            }
                            return new HashSet<>(noteTags).containsAll(selectedTags);
                        })
                        .collect(Collectors.toList())
        );
        tags.setNotes(notes);
    }

    /**
     * displays a message
     * @param message message
     * @param alert if it's an alert or not
     */
    public void messageFromOtherScene(String message, boolean alert) {
        if(alert) {
            popupMessages.showFadeInAlert(textPane, message);
            return;
        }
        popupMessages.showFadeInMessage(textPane, message);
    }


    /**
     * Receives a websocket message from another scene
     * which needs to be sent to the server
     * @param message the received message
     * @throws ExecutionException ExecutionException
     * @throws InterruptedException InterruptedException
     */
    public void webSocketMessageFromOtherScene(Message message)
            throws ExecutionException, InterruptedException {
        stompClient.sendMessage(message);
    }

    /**
     * Handles received messages from the server by
     * updating the UI
     * @param message the message
     */
    public void handleWebSocketMessage(Message message) {
        System.out.println(message);
        if(Objects.equals(message.getSessionId(), stompClient.getSessionId()))
            return;

        Platform.runLater(() -> {
            switch (message.getType()) {
                case ADDED -> handleAddedChange(message);
                case DELETED -> handleDeletedChange(message);
                case CONTENT_CHANGE -> handleContentChange(message);
                case TITLE_CHANGE -> handleTitleChange(message);
            }
        });
    }

    /**
     * handles the case in which content changed
     * @param message the message
     */
    private void handleContentChange(Message message) {
        if(defaultCollectionService.getSelectedNote() == null ||
                defaultCollectionService.getSelectedNote().getId() != message.getNoteSenderId())
            return;

        if(defaultCollectionService.getSelectedNote().getContent().equals(message.getMessage()))
            return;

        defaultCollectionService
                .getSelectedNote()
                .setContent(message.getMessage());

        if(markdownInput.getText().equals(defaultCollectionService.getSelectedNote().getContent()))
            return;
        markdownInput.setText(defaultCollectionService.getSelectedNote().getContent());
        renderMarkdown(markdownInput.getText());
    }

    /**
     * handles the case in which a note was deleted
     * @param message the message
     */
    private void handleDeletedChange(Message message) {
        notes.removeIf(n -> n.getId() == message.getNoteSenderId());
    }

    /**
     * handles the case in which a note was added
     * @param message the message
     */
    private void handleAddedChange(Message message) {
        if(defaultCollectionService.getDefaultCollection().isEmpty()) {
            notes.setAll(serverUtils.getNotes());
            return;
        }

        defaultCollectionService.setDefaultCollection(
                serverUtils.getNoteCollectionById(
                        defaultCollectionService.getDefaultCollection()
                                .get()
                                .getId()
                )
        );

        notes.setAll(
                defaultCollectionService
                        .getDefaultCollection()
                        .get()
                        .getNotes()
        );
    }

    /**
     * handles the case in which a title was changed
     * @param message message
     */
    private void handleTitleChange(Message message) {
        notes.replaceAll(n -> {
            if (n.getId() == message.getNoteSenderId())
                n.setTitle(message.getMessage());

            return n;
        });
    }

    @FXML
    private void handleFileChooser(javafx.event.ActionEvent event) {
        if(defaultCollectionService.getSelectedNote() == null) {
            messageFromOtherScene("No note selected!", true);
            return;
        }
        long noteId = defaultCollectionService.getSelectedNote().getId();
        Note selectedNote = serverUtils.getNote(noteId);

        if(selectedNote == null) {
            messageFromOtherScene("No note selected", true);
            return;
        }

        FileChooser fileChooser = new FileChooser();

        // Set extension filters
        FileChooser.ExtensionFilter textFilter =
                new FileChooser.ExtensionFilter("Text Files (*.txt)", "*.txt");
        FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter(
                "Image Files (*.jpg, *.jpeg, *.png, *.gif)", "*.jpg", "*.jpeg", "*.png", "*.gif");
        FileChooser.ExtensionFilter pdfFilter =
                new FileChooser.ExtensionFilter("PDF Files (*.pdf)", "*.pdf");

        fileChooser.getExtensionFilters().addAll(textFilter, imageFilter, pdfFilter);

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            // Handle the selected file (e.g., display its path or load it)
            System.out.println("Selected file: " + selectedFile.getAbsolutePath());

            List<String> fileNames = selectedNote.getFiles().stream()
                    .map(commons.File::getFileName).toList();

            if(fileNames.contains(selectedFile.getName())) {
                messageFromOtherScene("Filename is not unique!", true);
                return;
            }

            // upload the selected file to the server
            fileStorageServerUtils.uploadFile(selectedFile, noteId);
            messageFromOtherScene("File successfully uploaded!", false);
            refreshFiles();
            return;
        }

        messageFromOtherScene("File upload canceled", true);
    }

    /**
     * Refreshes the file menu bar. First it removes all the files in the bar. Then
     * it fetches all the available files on the server, and writes those to the UI bar.
     */
    public void refreshFiles() {
        if(defaultCollectionService.getSelectedNote() == null) {
            messageFromOtherScene("No note selected!", true);
            return;
        }

        fileBar.getChildren().clear();

        long noteId = defaultCollectionService.getSelectedNote().getId();
        Note selectedNote = serverUtils.getNote(noteId);

        List<commons.File> files = selectedNote.getFiles();
        System.out.println(files);

        for(commons.File file : files) {
            Button button = new Button(file.getFileName());
            button.getStyleClass().add("strongButton");
            button.setOnAction(event -> onFileButtonClick(file));

            Tooltip tooltip = new Tooltip("File name: " + file.getFileName() +
                    "\nContent type: " + file.getContentType() +
                    "\nSize: " + file.getSize() + " bytes" +
                    "\nDate added: " + file.getDateAdded()
                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            tooltip.setShowDuration(Duration.seconds(15));
            button.setTooltip(tooltip);

            fileBar.getChildren().add(button);
        }
    }

    private void onFileButtonClick(commons.File file) {
        defaultCollectionService.setSelectedFile(file);
        System.out.println("You clicked on" + file);
    }

    @FXML
    private void deleteFile() {
        commons.File selectedFile = defaultCollectionService.getSelectedFile();
        if(selectedFile == null) {
            messageFromOtherScene("No file selected!", true);
            return;
        }

        boolean confirmed = popupMessages.showDeleteConfirmationDialog(
                "Are you sure you want to delete the file?");

        if(confirmed) {
            fileStorageServerUtils.deleteFile(selectedFile.getId());

            messageFromOtherScene("File successfully deleted!", false);
            refreshFiles();
        }
    }

    @FXML
    private void saveFile(javafx.event.ActionEvent event) {
        commons.File selectedFile = defaultCollectionService.getSelectedFile();
        if(selectedFile == null) {
            messageFromOtherScene("No file selected!", true);
            return;
        }

        byte[] fileContent = fileStorageServerUtils.getContent(selectedFile.getId());

        FileChooser fileChooser = new FileChooser();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            // Write the byte array to the selected file
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(fileContent);
                System.out.println("File saved successfully to: " + file.getAbsolutePath());
                messageFromOtherScene("File successfully saved!", false);

            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Error saving file: " + e.getMessage());
            }
        } else {
            System.out.println("Save operation was cancelled.");
        }
    }

    @FXML
    private void editFile() {
        if(defaultCollectionService.getSelectedFile() == null) {
            messageFromOtherScene("No file selected!", true);
            return;
        } else if(defaultCollectionService.getSelectedNote() == null) {
            messageFromOtherScene("No note selected!", true);
            return;
        }
        fileBar.getChildren().clear();
        pc.showFileEditScene();
    }

    /**
     * changes the language of this scene
     * @param resourceBundle the language
     */
    public void changeLanguage(ResourceBundle resourceBundle) {
        deleteNote.setText(resourceBundle.getString("delete.button"));
        editNote.setText(resourceBundle.getString("edit.button"));
        refreshNote.setText(resourceBundle.getString("refresh.button"));
        createNote.setText(resourceBundle.getString("create.button"));
        fileDeleteButton.setText(resourceBundle.getString("delete.button"));
        fileEditButton.setText(resourceBundle.getString("edit.button"));
        fileRefreshButton.setText(resourceBundle.getString("refresh.button"));
        fileSaveButton.setText(resourceBundle.getString("save.button"));
        uploadButton.setText(resourceBundle.getString("upload.button"));
        clearTagsButton.setText(resourceBundle.getString("clear.button"));
        collectionSceneButton.setText(resourceBundle.getString("collections.label"));
        filesLabel.setText(resourceBundle.getString("files.label"));
        collectionTitleComboBox.setPromptText(resourceBundle.getString("collections.prompt"));
    }
}

