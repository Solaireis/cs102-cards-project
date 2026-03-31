package UI.components;

import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.Cursor;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class CardView extends StackPane {
    private final String cardId;
    private final ImageView imageView;
    private boolean selected = false;

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

        setOnMouseEntered(e -> {
            if (!selected) {
                animateTo(1.05, -4);
            }
        });

        setOnMouseExited(e -> {
            if (!selected) {
                animateTo(1.0, 0);
            }
        });
    }

    public String getCardId() {
        return cardId;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;

        if (selected) {
            animateTo(1.08, -10);
            setStyle(
                "-fx-border-color: white;" +
                "-fx-border-width: 3;" +
                "-fx-border-radius: 8;" +
                "-fx-background-radius: 8;"
            );
            setEffect(new DropShadow());
        } else {
            animateTo(1.0, 0);
            setStyle("");
            setEffect(null);
        }
    }

    public boolean isSelected() {
        return selected;
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