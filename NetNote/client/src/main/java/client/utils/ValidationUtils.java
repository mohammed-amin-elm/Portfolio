package client.utils;

import commons.Note;
import commons.NoteCollection;
import jakarta.inject.Inject;
import javafx.scene.layout.AnchorPane;

import java.util.List;

public class ValidationUtils {

    private final PopupMessages popupMessages;

    /**
     * constructor
     * @param popupMessages popupMessages
     */
    @Inject
    public ValidationUtils(PopupMessages popupMessages) {
        this.popupMessages = popupMessages;
    }
    /**
     * This method takes in a name and returns a boolean indicating whether
     * the name of the note or collection is valid or not. If the name is invalid
     * a message pops up.
     * @param name The name of the note or collection
     * @param ap the anchorpane on which the error message will be displayed
     * @return a boolean indicating whether it's valid or not
     */
    public boolean isValidName(String name, AnchorPane ap) {

        if(name == null) {
            return false;
        }

        if(name.isEmpty()) {
            if (ap != null)
                popupMessages.showFadeInAlert(ap, "You haven't provided a name.");
            return false;
        }
        if(name.charAt(0) == ' ' ||
                name.charAt(name.length() - 1) == ' ') {

            if (ap != null)
                popupMessages.showFadeInAlert(ap, "There are spaces at the " +
                        "end or beginning.");
            return false;
        }
        return true;
    }

    /**
     * This method receives a title, a list of titles and an anchor pane. It
     * checks whether the title is in the list and if it is a message pops up.
     * @param title the title which has to be compared
     * @param allTitles a list full of titles (both notes and collections)
     * @param ap the anchor pane on which the message pops up
     * @param message the message to be displayed
     * @return a boolean indicating whether the title is inside the list
     */
    public boolean isDuplicateTitle(String title, List<String> allTitles,
                                    AnchorPane ap, String message) {

        if(allTitles.contains(title)) {
            if(ap != null)
                popupMessages.showFadeInAlert(ap, message);
            return true;
        }

        return false;
    }

    /**
     * This method checks whether the title is in the collection and if it is a message pops up.
     * @param title the title which has to be compared
     * @param collection a note collection for which we want to check
     * @param ap the anchor pane on which the message pops up
     * @param message the message to be displayed
     * @return a boolean indicating whether the title is inside the list
     */
    public boolean isDuplicateNoteTitle(String title, NoteCollection collection,
                                               AnchorPane ap, String message) {
        List<String> allTitles = collection
                .getNotes()
                .stream()
                .map(Note::getTitle)
                .toList();

        return isDuplicateTitle(title, allTitles, ap, message);
    }

    /**
     * Checks if a note with title noteTitle is in the same collection with another specified note
     * @param noteTitle noteTitle
     * @param note note to compare
     * @param collections all collections
     * @return true/false
     */
    public boolean sameCollection(String noteTitle, Note note,
                                         List<NoteCollection> collections) {
        for(var collection : collections) {
            boolean titleInThisCollection = collection
                    .getNotes()
                    .stream()
                    .anyMatch(n -> n.getTitle().equals(noteTitle));
            boolean noteInThisCollection = collection
                    .getNotes()
                    .stream()
                    .anyMatch(n -> n.getId() == note.getId());

            if(titleInThisCollection && noteInThisCollection)
                return true;
        }
        return false;
    }
}
