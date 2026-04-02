// AI-assisted: Parts of this class, including menu UI behavior,
// animated background/cloud setup, and event-handling logic, were developed
// with help from ChatGPT. The team reviewed, tested, and modified the final implementation.
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

/**
 * Controls the main menu screen of the game.
 * This class handles menu animations, button effects,
 * and starting or quitting the game.
 */
public class MenuController {

    @FXML private AnchorPane root;

    // Background layers
    @FXML private ImageView sky;
    @FXML private Pane cloudLayer;
    @FXML private ImageView bigCloudA;
    @FXML private ImageView bigCloudB;
    @FXML private ImageView smallCloudA;
    @FXML private ImageView smallCloudB;
    @FXML private ImageView trees;
    @FXML private ImageView sign;

    // Main menu container
    @FXML private VBox menuBox;

    // Menu buttons
    @FXML private Button playBtn;
    @FXML private Button quitBtn;

    private static final double BASE_W = 1400;
    private static final double BASE_H = 900;

    // Parallax cloud speeds in pixels per second
    private static final double FAR_SPEED  = 10.0;
    private static final double NEAR_SPEED = 22.0;

    private AnimationTimer cloudTimer;

    /**
     * Initializes the menu screen after the FXML elements are loaded.
     * This sets up scaling, background animation, and button hover effects.
     */
    @FXML
    public void initialize() {
        // Stretch full-screen background layers with the window
        bindFullScreen(sky);
        bindFullScreen(trees);
        bindFullScreen(sign);
        cloudLayer.prefWidthProperty().bind(root.widthProperty());
        cloudLayer.prefHeightProperty().bind(root.heightProperty());

        // Scale the menu buttons and layout with the window size
        var scale = Bindings.createDoubleBinding(
                () -> Math.min(root.getWidth() / BASE_W, root.getHeight() / BASE_H),
                root.widthProperty(), root.heightProperty()
        );
        menuBox.scaleXProperty().bind(scale);
        menuBox.scaleYProperty().bind(scale);

        // Add sign bobbing animation
        bobSign();

        // Start cloud parallax animation
        startCloudParallax();

        // Add hover effects to menu buttons
        addHoverEffects(playBtn);
        addHoverEffects(quitBtn);
    }

    /**
     * Binds an image view so it stretches to fill the root pane.
     *
     * @param v the image view to bind
     */
    private void bindFullScreen(ImageView v) {
        v.fitWidthProperty().bind(root.widthProperty());
        v.fitHeightProperty().bind(root.heightProperty());
        v.setPreserveRatio(false);
        v.setSmooth(true);
    }

    /**
     * Adds a gentle up-and-down bobbing animation to the menu sign.
     */
    private void bobSign() {
        TranslateTransition bob = new TranslateTransition(Duration.seconds(1.8), sign);
        bob.setFromY(0);
        bob.setToY(-10);
        bob.setAutoReverse(true);
        bob.setCycleCount(Animation.INDEFINITE);
        bob.play();
    }

    /**
     * Starts the cloud parallax animation in the background.
     * The two cloud layers move at different speeds to create depth.
     */
    private void startCloudParallax() {
        // Use opacity differences to create a depth effect
        bigCloudA.setOpacity(0.65);
        bigCloudB.setOpacity(0.65);
        smallCloudA.setOpacity(0.90);
        smallCloudB.setOpacity(0.90);

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

                // Use the current window width for seamless wrapping
                double w = root.getWidth();
                if (w <= 0) return;

                scrollPair(bigCloudA, bigCloudB, FAR_SPEED * dt, w);
                scrollPair(smallCloudA, smallCloudB, NEAR_SPEED * dt, w);
            }
        };
        cloudTimer.start();
    }

    /**
     * Scrolls a pair of cloud images horizontally and wraps them when needed.
     *
     * @param a the first image
     * @param b the second image
     * @param dx the amount to move horizontally
     * @param w the current window width
     */
    private void scrollPair(ImageView a, ImageView b, double dx, double w) {
        a.setLayoutX(a.getLayoutX() + dx);
        b.setLayoutX(b.getLayoutX() + dx);

        // When one image moves fully past the right edge, wrap it back to the left
        if (a.getLayoutX() >= w) a.setLayoutX(a.getLayoutX() - 2 * w);
        if (b.getLayoutX() >= w) b.setLayoutX(b.getLayoutX() - 2 * w);
    }

    /**
     * Adds hover and click animation effects to a menu button.
     *
     * @param btn the button to style and animate
     */
    private void addHoverEffects(Button btn) {
        // Start from normal scale
        btn.setScaleX(1.0);
        btn.setScaleY(1.0);

        DropShadow shadow = new DropShadow();
        shadow.setRadius(18);
        shadow.setSpread(0.15);
        shadow.setColor(Color.rgb(255, 255, 255, 0.55));

        // Create smooth scale animations for hover in and out
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

        btn.setOnMousePressed(e -> {
            btn.setScaleX(0.98);
            btn.setScaleY(0.98);
        });

        btn.setOnMouseReleased(e -> {
            boolean hovering = btn.isHover();
            btn.setScaleX(hovering ? 1.08 : 1.0);
            btn.setScaleY(hovering ? 1.08 : 1.0);
        });
    }

    /**
     * Handles the Play button.
     * This reads the game settings, creates the game logic,
     * loads the game screen, and switches to it.
     */
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

            // Debug print statements for quick testing
            System.out.println("numOfPlayers from config = " + numOfPlayers);
            System.out.println("playerNames = " + playerNames);
            System.out.println("Winning Condition: " + winningCondition);
            System.out.println("Quick Testing Hotfix");
            System.out.println("1: White Bonus + 1");
            System.out.println("2: Blue Bonus + 1");
            System.out.println("3: Green Bonus + 1");
            System.out.println("4: Red Bonus + 1");
            System.out.println("5: Black Bonus + 1");

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

    /**
     * Handles the Quit button by closing the application.
     */
    @FXML
    private void onQuit() {
        Platform.exit();
    }
}