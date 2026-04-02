package UI.components;

import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.Cursor;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

/**
 * Displays a single card image on the board or in a popup.
 * This class loads the card image, stores the card ID,
 * and adds a small hover animation for visual feedback.
 */
public class CardView extends StackPane {
    private final String cardId;
    private final ImageView imageView;

    /**
     * Creates a card view with the given image and display size.
     *
     * @param cardId the ID of the card being displayed
     * @param imagePath the path to the card image
     * @param width the display width of the card
     * @param height the display height of the card
     */
    public CardView(String cardId, String imagePath, double width, double height) {
        this.cardId = cardId;

        var stream = getClass().getResourceAsStream(imagePath);
        if (stream == null) {
            throw new IllegalArgumentException("Could not find card image: " + imagePath);
        }

        Image image = new Image(stream);
        imageView = new ImageView(image);
        imageView.setFitWidth(width);
        imageView.setFitHeight(height);
        imageView.setPreserveRatio(false);

        getChildren().add(imageView);
        setUserData(cardId);
        setCursor(Cursor.HAND);

        // Add a small hover animation to make cards feel more interactive.
        setOnMouseEntered(e -> animateTo(1.05, -4));
        setOnMouseExited(e -> animateTo(1.0, 0));
    }

    /**
     * Returns the ID of the card shown in this view.
     *
     * @return the card ID
     */
    public String getCardId() {
        return cardId;
    }


    /**
     * Animates the card to the given scale and vertical offset.
     *
     * @param scale the target scale of the card
     * @param y the target vertical position offset
     */
    private void animateTo(double scale, double y) {
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(120), this);
        scaleTransition.setToX(scale);
        scaleTransition.setToY(scale);

        TranslateTransition translateTransition = new TranslateTransition(Duration.millis(120), this);
        translateTransition.setToY(y);

        scaleTransition.play();
        translateTransition.play();
    }
}