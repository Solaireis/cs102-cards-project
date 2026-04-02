// AI-assisted: This class is based primarily on a JavaFX application/template structure.
// ChatGPT assistance was limited to minor setup and integration guidance.
// The team reviewed and finalized the implementation.
package UI;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

/**
 * Launches the JavaFX application for the game.
 * This class loads the main menu screen and shows the primary stage.
 */
public class GameApp extends Application {

    /**
     * Starts the JavaFX application and loads the main menu screen.
     *
     * @param stage the primary application stage
     * @throws Exception if the FXML file cannot be loaded
     */
    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("Splendor");

        Parent root = FXMLLoader.load(
                Objects.requireNonNull(getClass().getResource("/UI/views/menu.fxml"))
        );

        stage.setScene(new Scene(root, 1400, 900));
        stage.show();
    }

    /**
     * Launches the JavaFX application.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}