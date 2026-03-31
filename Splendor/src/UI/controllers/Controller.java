package UI.controllers;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;

import java.util.ArrayList;
import java.util.List;

import java.net.URL;

import UI.components.BoardView;
import Cards.DevelopmentCard.DevelopmentCardDeck;
import Cards.DevelopmentCard.DevelopmentCardFaceUP;
import Cards.Noble.NobleDeck;
import Cards.Noble.NobleFaceUP;

public class Controller {

    @FXML private AnchorPane root;

    // Background
    @FXML private ImageView gameSky;
    @FXML private ImageView gameCloud1A;
    @FXML private ImageView gameCloud1B;
    @FXML private ImageView gameCloud2A;
    @FXML private ImageView gameCloud2B;
    @FXML private ImageView gameCloud3A;
    @FXML private ImageView gameCloud3B;
    @FXML private ImageView gameCloud4A;
    @FXML private ImageView gameCloud4B;
    @FXML private ImageView gameCloud5A;
    @FXML private ImageView gameCloud5B;

    // Tokens
    @FXML private VBox tokenBox;
    @FXML private Button goldBtn;
    @FXML private Button greenBtn;
    @FXML private Button whiteBtn;
    @FXML private Button blackBtn;
    @FXML private Button redBtn;
    @FXML private Button blueBtn;
    @FXML private ImageView goldTokenView;
    @FXML private ImageView greenTokenView;
    @FXML private ImageView whiteTokenView;
    @FXML private ImageView blackTokenView;
    @FXML private ImageView redTokenView;
    @FXML private ImageView blueTokenView;

    // Board (4 rows of cards)
    @FXML private StackPane boardContainer;
    private BoardView boardView;

    private static final double BASE_W = 1400;
    private static final double BASE_H = 900;

    // px/sec: slower = farther, faster = closer
    private static final double LAYER1_SPEED = 10.0;
    private static final double LAYER2_SPEED = 14.0;
    private static final double LAYER3_SPEED = 20.0;
    private static final double LAYER4_SPEED = 25.0;
    private static final double LAYER5_SPEED = 16.0;

    private AnimationTimer cloudTimer;

    @FXML
    public void initialize() {
        // Cloud backgrounds
        loadBackgrounds();
        setupLayers();
        startCloudScroll();

        // Load tokens
        loadTokenImages();

        boardView = new BoardView();
        boardContainer.getChildren().add(boardView);

        DevelopmentCardDeck deck = new DevelopmentCardDeck();
        DevelopmentCardFaceUP upDeck = new DevelopmentCardFaceUP(deck);

        NobleDeck nobleDeck = new NobleDeck();
        NobleFaceUP upNobles = new NobleFaceUP(nobleDeck, 2);

        boardView.loadNobles(upNobles.getFaceUp());
        
        boardView.loadTier1(upDeck.getFaceUp(1));
        boardView.loadTier2(upDeck.getFaceUp(2));
        boardView.loadTier3(upDeck.getFaceUp(3));
    }

    private void loadBackgrounds() {
        gameSky.setImage(loadBackgroundImage("gameSky.png"));

        gameCloud1A.setImage(loadBackgroundImage("gameCloud1.png"));
        gameCloud1B.setImage(loadBackgroundImage("gameCloud1.png"));

        gameCloud2A.setImage(loadBackgroundImage("gameCloud2.png"));
        gameCloud2B.setImage(loadBackgroundImage("gameCloud2.png"));

        gameCloud3A.setImage(loadBackgroundImage("gameCloud3.png"));
        gameCloud3B.setImage(loadBackgroundImage("gameCloud3.png"));

        gameCloud4A.setImage(loadBackgroundImage("gameCloud4.png"));
        gameCloud4B.setImage(loadBackgroundImage("gameCloud4.png"));

        gameCloud5A.setImage(loadBackgroundImage("gameCloud5.png"));
        gameCloud5B.setImage(loadBackgroundImage("gameCloud5.png"));
    }

    // Layering cloud backgrounds
    private void setupLayers() {
        bindFullScreen(gameSky);

        bindFullScreen(gameCloud1A);
        bindFullScreen(gameCloud1B);
        bindFullScreen(gameCloud2A);
        bindFullScreen(gameCloud2B);
        bindFullScreen(gameCloud3A);
        bindFullScreen(gameCloud3B);
        bindFullScreen(gameCloud4A);
        bindFullScreen(gameCloud4B);
        bindFullScreen(gameCloud5A);
        bindFullScreen(gameCloud5B);

        // Position B copy just to the left of A
        resetPair(gameCloud1A, gameCloud1B, BASE_W);
        resetPair(gameCloud2A, gameCloud2B, BASE_W);
        resetPair(gameCloud3A, gameCloud3B, BASE_W);
        resetPair(gameCloud4A, gameCloud4B, BASE_W);
        resetPair(gameCloud5A, gameCloud5B, BASE_W);

        // depth effect
        gameCloud1A.setOpacity(0.55);
        gameCloud1B.setOpacity(0.55);
        gameCloud2A.setOpacity(0.65);
        gameCloud2B.setOpacity(0.65);
        gameCloud3A.setOpacity(0.70);
        gameCloud3B.setOpacity(0.70);
        gameCloud4A.setOpacity(0.78);
        gameCloud4B.setOpacity(0.78);
        gameCloud5A.setOpacity(0.90);
        gameCloud5B.setOpacity(0.90);
    }

    private void bindFullScreen(ImageView v) {
        v.fitWidthProperty().bind(root.widthProperty());
        v.fitHeightProperty().bind(root.heightProperty());
        v.setPreserveRatio(false);
        v.setSmooth(true);
        v.setMouseTransparent(true);
    }

    private void resetPair(ImageView a, ImageView b, double width) {
        a.setLayoutX(0);
        b.setLayoutX(-width);
    }

    private void startCloudScroll() {
        cloudTimer = new AnimationTimer() {
            long last = 0;

            @Override
            public void handle(long now) {
                if (last == 0) {
                    last = now;
                    return;
                }

                double dt = (now - last) / 1_000_000_000.0;
                last = now;

                double w = root.getWidth();
                if (w <= 0) return;

                scrollPair(gameCloud1A, gameCloud1B, LAYER1_SPEED * dt, w);
                scrollPair(gameCloud2A, gameCloud2B, LAYER2_SPEED * dt, w);
                scrollPair(gameCloud3A, gameCloud3B, LAYER3_SPEED * dt, w);
                scrollPair(gameCloud4A, gameCloud4B, LAYER4_SPEED * dt, w);
                scrollPair(gameCloud5A, gameCloud5B, LAYER5_SPEED * dt, w);
            }
        };

        cloudTimer.start();
    }

    private void scrollPair(ImageView a, ImageView b, double dx, double w) {
        a.setLayoutX(a.getLayoutX() + dx);
        b.setLayoutX(b.getLayoutX() + dx);

        if (a.getLayoutX() >= w) {
            a.setLayoutX(a.getLayoutX() - 2 * w);
        }
        if (b.getLayoutX() >= w) {
            b.setLayoutX(b.getLayoutX() - 2 * w);
        }
    }

    private Image loadBackgroundImage(String fileName) {
        String path = "/UI/images/backgrounds/gameBackgrounds/" + fileName;
        URL resource = getClass().getResource(path);

        if (resource == null) {
            throw new IllegalArgumentException("Could not find image: " + path);
        }

        return new Image(resource.toExternalForm());
    }

    private void loadTokenImages() {
        goldTokenView.setImage(loadTokenImage("gold.png"));
        greenTokenView.setImage(loadTokenImage("greenGem.png"));
        whiteTokenView.setImage(loadTokenImage("whiteGem.png"));
        blackTokenView.setImage(loadTokenImage("blackGem.png"));
        redTokenView.setImage(loadTokenImage("redGem.png"));
        blueTokenView.setImage(loadTokenImage("blueGem.png"));
    }

    private Image loadTokenImage(String fileName) {
        String path = "/UI/images/tokens/" + fileName;
        URL resource = getClass().getResource(path);

        if (resource == null) {
            throw new IllegalArgumentException("Could not find token image: " + path);
        }

        return new Image(resource.toExternalForm());
    }

    @FXML
    private void onGoldTokenClick() {
        System.out.println("Gold token clicked");
    }

    @FXML
    private void onGreenTokenClick() {
        System.out.println("Green token clicked");
    }

    @FXML
    private void onWhiteTokenClick() {
        System.out.println("White token clicked");
    }

    @FXML
    private void onBlackTokenClick() {
        System.out.println("Black token clicked");
    }

    @FXML
    private void onRedTokenClick() {
        System.out.println("Red token clicked");
    }

    @FXML
    private void onBlueTokenClick() {
        System.out.println("Blue token clicked");
    }
}
