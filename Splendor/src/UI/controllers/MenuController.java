package UI.controllers;

import java.util.ArrayList;
import java.util.List;

import Test.GameLogic;
import Properties.Reader;

import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MenuController {

    @FXML private AnchorPane root;

    // Layers
    @FXML private ImageView sky;
    @FXML private Pane cloudLayer;
    @FXML private ImageView bigCloudA;
    @FXML private ImageView bigCloudB;
    @FXML private ImageView smallCloudA;
    @FXML private ImageView smallCloudB;
    @FXML private ImageView trees;
    @FXML private ImageView sign;

    // UI
    @FXML private VBox menuBox;

    // Buttons
    @FXML private Button playBtn;
    @FXML private Button quitBtn;

    private static final double BASE_W = 1400;
    private static final double BASE_H = 900;

    // Parallax speeds (px/sec)
    private static final double FAR_SPEED  = 10.0;  // big cloud = slower (far)
    private static final double NEAR_SPEED = 22.0;  // small cloud = faster (near)

    private AnimationTimer cloudTimer;

    @FXML
    public void initialize() {
        // Stretch all full-screen layers with window size
        bindFullScreen(sky);
        bindFullScreen(trees);
        bindFullScreen(sign);
        cloudLayer.prefWidthProperty().bind(root.widthProperty());
        cloudLayer.prefHeightProperty().bind(root.heightProperty());

        // Scale the menu (buttons) uniformly with window size
        var scale = Bindings.createDoubleBinding(
                () -> Math.min(root.getWidth() / BASE_W, root.getHeight() / BASE_H),
                root.widthProperty(), root.heightProperty()
        );
        menuBox.scaleXProperty().bind(scale);
        menuBox.scaleYProperty().bind(scale);

        // Gentle bob for the sign
        bobSign();

        // Start cloud parallax loop
        startCloudParallax();

        // Button hover effects
        addHoverEffects(playBtn);
        addHoverEffects(quitBtn);
    }

    private void bindFullScreen(ImageView v) {
        v.fitWidthProperty().bind(root.widthProperty());
        v.fitHeightProperty().bind(root.heightProperty());
        v.setPreserveRatio(false);
        v.setSmooth(true);
    }

    private void bobSign() {
        TranslateTransition bob = new TranslateTransition(Duration.seconds(1.8), sign);
        bob.setFromY(0);
        bob.setToY(-10);
        bob.setAutoReverse(true);
        bob.setCycleCount(Animation.INDEFINITE);
        bob.play();
    }

    private void startCloudParallax() {
        // Optional: opacity helps depth
        bigCloudA.setOpacity(0.65);
        bigCloudB.setOpacity(0.65);
        smallCloudA.setOpacity(0.90);
        smallCloudB.setOpacity(0.90);

        cloudTimer = new AnimationTimer() {
            long last = 0;

            @Override
            public void handle(long now) {
                if (last == 0) { last = now; return; }
                double dt = (now - last) / 1_000_000_000.0;
                last = now;

                // Use current window width for seamless wrapping
                double w = root.getWidth();
                if (w <= 0) return;

                scrollPair(bigCloudA, bigCloudB, FAR_SPEED * dt, w);
                scrollPair(smallCloudA, smallCloudB, NEAR_SPEED * dt, w);
            }
        };
        cloudTimer.start();
    }

    /**
     * Scroll two full-width cloud layers to the right and wrap seamlessly.
     * Works even if the window is resized, as long as your "tile" images are full-screen.
     */
    private void scrollPair(ImageView a, ImageView b, double dx, double w) {
        a.setLayoutX(a.getLayoutX() + dx);
        b.setLayoutX(b.getLayoutX() + dx);

        // When an image has fully moved past the right edge, jump it back left by 2 widths
        if (a.getLayoutX() >= w) a.setLayoutX(a.getLayoutX() - 2 * w);
        if (b.getLayoutX() >= w) b.setLayoutX(b.getLayoutX() - 2 * w);
    }

    private void addHoverEffects(Button btn) {
        // Make sure scale starts normal
        btn.setScaleX(1.0);
        btn.setScaleY(1.0);
        
        // A subtle glow/shadow (choose one)
        DropShadow shadow = new DropShadow();
        shadow.setRadius(18);
        shadow.setSpread(0.15);
        shadow.setColor(Color.rgb(255, 255, 255, 0.55));
        
        // Smooth hover in/out scale animations
        ScaleTransition scaleUp = new ScaleTransition(Duration.millis(120), btn);
        scaleUp.setToX(1.08);
        scaleUp.setToY(1.08);
        
        ScaleTransition scaleDown = new ScaleTransition(Duration.millis(120), btn);
        scaleDown.setToX(1.0);
        scaleDown.setToY(1.0);
        
        btn.setOnMouseEntered(e -> {
            scaleDown.stop();
            btn.setEffect(shadow);
            scaleUp.playFromStart();
        });
    
        btn.setOnMouseExited(e -> {
            scaleUp.stop();
            btn.setEffect(null);
            scaleDown.playFromStart();
        });
    
        // Optional: make it feel clickable
        btn.setOnMousePressed(e -> {
            btn.setScaleX(0.98);
            btn.setScaleY(0.98);
        });
    
        btn.setOnMouseReleased(e -> {
            // if mouse is still over button, return to hover scale; else normal
            boolean hovering = btn.isHover();
            btn.setScaleX(hovering ? 1.08 : 1.0);
            btn.setScaleY(hovering ? 1.08 : 1.0);
        });
    }

    // @FXML
    // private void onPlay() {
    //     try {

    //         List<String> playerNames = new ArrayList<>();
    //         playerNames.add("Player 1");
    //         playerNames.add("Player 2");

    //         int winningCondition = 15;
    //         GameLogic gameLogic = new GameLogic(playerNames, winningCondition);

    //         FXMLLoader loader = new FXMLLoader(getClass().getResource("/UI/views/game.fxml"));
    //         Parent gameRoot = loader.load();

    //         Controller gameController = loader.getController();
    //         gameController.setGameLogic(gameLogic);

    //         Stage stage = (Stage) root.getScene().getWindow();
    //         stage.setScene(new Scene(gameRoot, BASE_W, BASE_H));
    //         stage.show();

    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }
    // }

    @FXML
    private void onPlay() {
        try {
            Reader reader = new Reader();

            int numOfPlayers = reader.getNumOfPlayers();
            int winningCondition = reader.getPrestigePointToWin();

            List<String> playerNames = new ArrayList<>();
            for (int i = 1; i <= numOfPlayers; i++) {
                playerNames.add("Player " + i);
            }
            System.out.println("numOfPlayers from config = " + numOfPlayers);
            System.out.println("playerNames = " + playerNames);
            System.out.println("Winning Condition: " + winningCondition);

            GameLogic gameLogic = new GameLogic(playerNames, winningCondition);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UI/views/game.fxml"));
            Parent gameRoot = loader.load();

            Controller gameController = loader.getController();
            gameController.setGameLogic(gameLogic);

            Stage stage = (Stage) root.getScene().getWindow();
            stage.setScene(new Scene(gameRoot, BASE_W, BASE_H));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onQuit() {
        Platform.exit();
    }
}