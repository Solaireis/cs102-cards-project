package UI.components;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;


public class CardView extends StackPane {
    private final String cardId;

    public CardView(String cardId, String imagePath, double width, double height) {
        this.cardId = cardId;
    
        var stream = getClass().getResourceAsStream(imagePath);
        if (stream == null) {
            throw new IllegalArgumentException("Could not find card image: " + imagePath);
        }
    
        Image image = new Image(stream);
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(width);
        imageView.setFitHeight(height);
        imageView.setPreserveRatio(false);
    
        getChildren().add(imageView);
        setUserData(cardId);

        setOnMouseClicked(e -> {
            System.out.println("Clicked card: " + cardId);
        });
    }

    public String getCardId() {
        return cardId;
    }
}
