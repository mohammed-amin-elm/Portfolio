package client.testFunctionality;


import java.util.Locale;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import java.util.ResourceBundle;


/**
 * the class LanguageCtrl is for the language switch button
 * which allows the user to switch languages easily
 */
public class LanguageCtrl {

    private final StringProperty buttonText = new SimpleStringProperty();
    private final Locale defaultLocale = Locale.ENGLISH;

    @FXML
    private ChoiceBox<String> languageBox;

    @FXML
    private Button testButton;

    /**
     * Binds the text of testButton to the string buttonText to dynamically change languages
     * Sets English as locale
     * Initializes the LanguageCtrl, setting up the languages to choose from
     */
    @FXML
    public void initialize() {
        testButton.textProperty().bind(buttonText);

        setLocale(defaultLocale);

        languageBox.getItems().addAll("English", "Dutch");
        languageBox.setValue("English");

        languageBox.setOnAction(onClick -> onLanguageClick());
    }

    /**
     * Sees if a new language is selected
     * Changes currentLanguage to that language
     * Calls the switchLanguage method
     */
    public void onLanguageClick() {
        String currentLanguage = languageBox.getValue();
        switchLanguage(currentLanguage);
    }

    /**
     * Takes the currentLanguage and switches to the case of that language
     *
     * @param currentLanguage the current language selected
     */
    private void switchLanguage(String currentLanguage) {
        switch (currentLanguage) {
            case "English"-> setEn();
            case "Dutch" -> setNl();
        }
    }

    /**
     * Sets locale to English by calling the setLocale method with English as locale
     */
    private void setEn() {
        setLocale(Locale.ENGLISH);
    }

    /**
     * Sets locale to Dutch by calling the setLocale method with Dutch as locale
     */
    private void setNl() {
        setLocale(Locale.forLanguageTag("nl-NL"));
    }

    /**
     * Depending on which method calls this method locale is a different language
     * By taking the value for "text.button" from the messages file in the resource bundle
     * the value of buttonText is changed to a different language
     *
     * @param locale the language of the method that called setLocale
     */
    private void setLocale(Locale locale) {
        var rb = ResourceBundle.getBundle("messages", locale);
        var text = rb.getString("text.button");
        buttonText.setValue(text);
    }



}
