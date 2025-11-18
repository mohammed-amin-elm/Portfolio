package client.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javafx.scene.layout.AnchorPane;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ValidationUtilsTest {

    private ValidationUtils validationUtils;
    private PopupMessages mockPopupMessages;

    @BeforeEach
    void setup() {
        mockPopupMessages = Mockito.mock(PopupMessages.class);
        validationUtils = new ValidationUtils(mockPopupMessages);
    }

    /**
     * Tests whether the null check returns false
     */
    @Test
    public void isValidName_NullTest() {
        String name = null;
        boolean result = validationUtils.isValidName(name, null);
        assertFalse(result);
    }

    /**
     * Tests if the method returns true when a proper name is inserted.
     */
    @Test
    public void isValidName_ValidNameTest() {
        String name = "TEST";
        boolean result = validationUtils.isValidName(name, null);
        assertTrue(result);
    }

    /**
     * Tests whether the method properly returns false when a name is
     * given that starts with a space.
     */
    @Test
    public void isValidName_StartsWithSpaceTest() {
        String name = " TEST";
        boolean result = validationUtils.isValidName(name, null);
        assertFalse(result);
    }

    /**
     * Tests whether the method properly returns false when a name is
     * given that ends with a space.
     */
    @Test
    public void isValidName_EndsWithSpace() {
        String name = "TEST ";
        boolean result = validationUtils.isValidName(name, null);
        assertFalse(result);
    }

    /**
     * Tests whether the method returns false when an empty string
     * is given.
     */
    @Test
    public void isValidName_EmptyNameTest() {
        String name = "";
        boolean result = validationUtils.isValidName(name, null);
        assertFalse(result);
    }

    /**
     * Tests whether the method returns false if the amount of characters
     * is equal to the amount of spaces inserted.
     */
    @Test
    public void isValidName_MultipleSpaceTest() {
        int times = 20;
        String currName = " ";

        for (int i = 0; i < times; i++) {
            currName += ' ';
            boolean result = validationUtils.isValidName(currName, null);
            assertFalse(result);
        }
    }

    /**
     * Tests whether the method correctly identifies a duplicate title.
     */
    @Test
    public void isDuplicateName_DuplicateTitleExists() {
        List<String> titleList = new ArrayList<>();
        String currTitle = "TEST";
        String title = "TEST";

        for (int i = 0; i < 10; i++) {
            titleList.add(currTitle);
            currTitle += i;
        }

        boolean result = validationUtils.isDuplicateTitle(title, titleList, null, "Duplicate title.");
        assertTrue(result);
    }

    /**
     * Tests whether the method correctly identifies a non-duplicate title.
     */
    @Test
    public void isDuplicateName_NoDuplicateTitle() {
        List<String> titleList = new ArrayList<>();
        String title = "TEST";
        boolean result = validationUtils.isDuplicateTitle(title, titleList, null, "Duplicate title.");
        assertFalse(result);
    }
}
