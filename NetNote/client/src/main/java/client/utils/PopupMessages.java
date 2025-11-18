package client.utils;

import javafx.animation.FadeTransition;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

import java.util.Optional;

public class PopupMessages {

    /**
     * creates a fading alert
     * @param ap the anchor pane where to create it
     * @param message the message to be displayed
     */
    public void showFadeInAlert(AnchorPane ap, String message) {
        Label fadeInAlert = new Label(message);
        fadeInAlert.setStyle(
                """
                -fx-background-color: rgba(255,90,90,0.8);
                -fx-text-fill: #000000;
                -fx-padding: 10;
                -fx-border-color: darkred;
                -fx-border-width: 2;
                -fx-border-radius: 5;
                -fx-background-radius: 5;
                -fx-font-size: 14;
                """
        );

        createAnimation(ap, fadeInAlert);
    }

    /**
     * creates a fading message
     * @param ap the anchor pane where to create it
     * @param message the message to be displayed
     */
    public void showFadeInMessage(AnchorPane ap, String message) {
        Label fadeInAlert = new Label(message);
        fadeInAlert.setStyle(
                """
                -fx-background-color: rgba(176,255,143,0.8);
                -fx-text-fill: #000000;
                -fx-padding: 10;
                -fx-border-color: #146800;
                -fx-border-width: 2;
                -fx-border-radius: 5;
                -fx-background-radius: 5;
                -fx-font-size: 14;
                """
        );

        createAnimation(ap, fadeInAlert);
    }

    /**
     * generates an animation
     * @param ap the anchor pane
     * @param fadeInAlert the label
     */
    private void createAnimation(AnchorPane ap, Label fadeInAlert) {
        ap.getChildren().add(fadeInAlert);
        AnchorPane.setBottomAnchor(fadeInAlert, 10.0);
        AnchorPane.setLeftAnchor(fadeInAlert, 30.0);
        AnchorPane.setRightAnchor(fadeInAlert, 30.0);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(2500), fadeInAlert);
        fadeIn.setFromValue(1);
        fadeIn.setToValue(0);

        fadeIn.playFromStart();
        fadeIn.setOnFinished(event -> {
            ap.getChildren().remove(fadeInAlert);
        });
    }

    /**
     * Show a delete confirmation dialog
     * @param message The message to present to the user.
     * @return The choice of the user
     */
    public boolean showDeleteConfirmationDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Confirmation");
        alert.setHeaderText(message);
        alert.setContentText("This action cannot be undone.");

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            return true;
        } else {
            // Cancel the delete operation
            return false;
        }
    }
}
