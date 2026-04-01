package UI.components;

import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.Cursor;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class CardView extends StackPane {
    private final String cardId;
    private final ImageView imageView;

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

        setOnMouseEntered(e -> animateTo(1.05, -4));
        setOnMouseExited(e -> animateTo(1.0, 0));
    }

    public String getCardId() {
        return cardId;
    }

    public void setSelected(boolean selected) {
        
    }

    public boolean isSelected() {
        return false;
    }

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