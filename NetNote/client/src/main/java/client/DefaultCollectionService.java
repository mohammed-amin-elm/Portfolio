package client;

import commons.Note;
import commons.NoteCollection;
import commons.File;

import java.util.Optional;

//  Comments are generated with the help of ChatGPT \\

/**
 * The `DefaultCollectionService` class provides functionality to manage a default `NoteCollection`
 * and a currently selected `Note`. It uses JavaFX `ObjectProperty` to allow for observation
 * of property changes.
 */
public class DefaultCollectionService {
    private NoteCollection defaultCollectionProperty;
    private NoteCollection collectionToEdit;
    private Note selectedNoteProperty;
    private File selectedFile;


    /**
     * Simple constructor.
     * Initializes the properties for the default `NoteCollection` and the selected `Note`.
     */
    public DefaultCollectionService() {
    }

    /**
     * Retrieves the current default `NoteCollection`.
     *
     * @return The currently set default `NoteCollection`, or null if none is set.
     */
    public Optional<NoteCollection> getDefaultCollection() {
        return Optional.ofNullable(defaultCollectionProperty);
    }

    /**
     * getter for the collection to be edited
     * @return collectionToEdit
     */
    public Optional<NoteCollection> getCollectionToEdit() {
        return Optional.ofNullable(collectionToEdit);
    }

    /**
     * Setter for the collection to be edited
     * @param collectionToEdit collectionToEdit
     */
    public void setCollectionToEdit(NoteCollection collectionToEdit) {
        this.collectionToEdit = collectionToEdit;
    }

    /**
     * Sets the default `NoteCollection` to the specified collection. This will
     * notify any listeners observing the `defaultCollectionProperty`.
     *
     * @param defaultCollection The `NoteCollection` to set as the default.
     */
    public void setDefaultCollection(NoteCollection defaultCollection) {
        defaultCollectionProperty = defaultCollection;
    }


    /**
     * Retrieves the currently selected `Note`.
     *
     * @return The currently selected `Note`, or null if none is selected.
     */
    public Note getSelectedNote() {
        return selectedNoteProperty;
    }

    /**
     * Sets the currently selected `Note`. This will notify any listeners observing
     * the `selectedNoteProperty`.
     *
     * @param selectedNote The `Note` to set as selected.
     */
    public void setSelectedNote(Note selectedNote) {
        selectedNoteProperty = selectedNote;
    }

    /**
     * Retrieves the currently selected 'File'
     *
     * @return The currently selected 'File', or null if none is selected.
     */
    public File getSelectedFile() {
        return selectedFile;
    }

    /**
     * Sets the currently selected 'File'. This will notify any listeners
     * observing the 'selectedNoteProperty'.
     *
     * @param selectedFile The 'File' to set as selected.
     */
    public void setSelectedFile(File selectedFile) {
        this.selectedFile = selectedFile;
    }


    /**
     * Returns a string representation of the `DefaultCollectionService`, including
     * the current value of the `defaultCollectionProperty`.
     *
     * @return A string describing the `DefaultCollectionService`.
     */
    @Override
    public String toString() {
        return "DefaultCollectionService{" +
                "defaultCollectionProperty=" + defaultCollectionProperty +
                ", selectedNoteProperty=" + selectedNoteProperty +
                ", selectedFile=" + selectedFile +
                '}';
    }
}