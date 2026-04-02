// AI-assisted: Parts of this class, including token-action flow, discard popup handling,
// UI state resets, and end-turn integration, were developed with help from ChatGPT-5.
// The team reviewed, tested, and modified the final implementation.
package UI.controllers;

import javafx.animation.AnimationTimer;
import javafx.animation.PauseTransition;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Cards.DevelopmentCard.DevelopmentCard;
import Cards.Noble.Noble;
import Cards.Token.TokenBank;
import Player.Computer;
import Player.ComputerService;
import Player.Player;
import Test.GameLogic;
import Test.MoveResult;
import UI.components.BoardView;
import UI.components.CardView;

/**
 * Controls the main game screen and connects the UI to the game logic.
 * This class handles player actions, board interactions, UI updates,
 * popups, background animation, discard flow, and computer turns.
 */
public class Controller {

    @FXML private AnchorPane root;

    // Background images
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

    // Token controls and bank display
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
    @FXML private Label goldBankCountLabel;
    @FXML private Label greenBankCountLabel;
    @FXML private Label whiteBankCountLabel;
    @FXML private Label blackBankCountLabel;
    @FXML private Label redBankCountLabel;
    @FXML private Label blueBankCountLabel;

    // Turn and current player display
    @FXML private Label currentPlayerLabel;
    @FXML private Label turnLabel;

    // Main action buttons
    @FXML private Button takeThreeTokensButton;
    @FXML private Button takeTwoTokensButton;
    @FXML private Button buyCardButton;
    @FXML private Button reserveCardButton;
    @FXML private Button endTurnButton;

    // Current player stats and info
    @FXML private Label pointsLabel;
    @FXML private VBox currentPlayerTokensBox;
    @FXML private Button viewReservedButton;
    @FXML private Button viewBoughtButton;
    @FXML private Button viewNobleButton;

    // Board display
    @FXML private StackPane boardContainer;

    // Bottom status bar
    @FXML private Label statusBarLabel;
    @FXML private HBox statusBar;
    @FXML private Label statusIcon;

    // Main board UI component and backend game logic
    private BoardView boardView;
    private GameLogic gameLogic;

    // Discard popup UI state
    private Stage discardPopupStage;
    private Label discardPopupInfoLabel;
    private final Map<String, Label> discardCountLabels = new HashMap<>();
    private final Map<String, Button> discardButtons = new HashMap<>();

    /**
     * Tracks the player's current main action selection.
     */
    private enum ActionMode {
        NONE, TAKE_TOKENS, BUY_CARD, RESERVE_CARD
    }

    private ActionMode currentMode = ActionMode.NONE;

    /**
     * Tracks the current token-taking action mode.
     */
    private enum TokenActionMode {
        NONE,
        TAKE_THREE,
        TAKE_TWO_SAME
    }

    private TokenActionMode tokenActionMode = TokenActionMode.NONE;
    private final ArrayList<String> selectedTokenColors = new ArrayList<>();

    // Tracks whether the player may take a gold token after reserving a card
    private boolean canTakeGoldAfterReserve = false;

    // Tracks whether the player has already used their main action this turn
    private boolean turnActionCommitted = false;

    // Prevents the winner popup from being shown multiple times
    private boolean winnerPopupShown = false;

    private static final double BASE_W = 1400;

    // Cloud movement speeds in pixels per second
    private static final double LAYER1_SPEED = 10.0;
    private static final double LAYER2_SPEED = 14.0;
    private static final double LAYER3_SPEED = 20.0;
    private static final double LAYER4_SPEED = 25.0;
    private static final double LAYER5_SPEED = 16.0;

    private AnimationTimer cloudTimer;

    /**
     * Initializes the game screen after the FXML elements are loaded.
     * This sets up the animated background, token images, board view,
     * board click handlers, and debug keyboard shortcuts.
     */
    @FXML
    public void initialize() {

        // Set up animated cloud background
        loadBackgrounds();
        setupLayers();
        startCloudScroll();

        // Load token images
        loadTokenImages();

        // Create and attach the main board view
        boardView = new BoardView();
        boardContainer.getChildren().add(boardView);

        // Connect board clicks to controller handlers
        boardView.setOnFaceUpCardClick((tier, index) -> handleFaceUpCardClick(tier, index));
        boardView.setOnTopDeckClick(this::handleTopDeckClick);
        boardView.setOnNobleClick(this::handleNobleClick);

        // Debug shortcuts for quickly granting bonuses during testing
        root.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.setOnKeyPressed(e -> {
                    if (gameLogic == null) {
                        return;
                    }

                    switch (e.getCode()) {
                        case DIGIT1 -> updateStatus(gameLogic.debugGrantBonus(TokenBank.WHITE, 1));
                        case DIGIT2 -> updateStatus(gameLogic.debugGrantBonus(TokenBank.BLUE, 1));
                        case DIGIT3 -> updateStatus(gameLogic.debugGrantBonus(TokenBank.GREEN, 1));
                        case DIGIT4 -> updateStatus(gameLogic.debugGrantBonus(TokenBank.RED, 1));
                        case DIGIT5 -> updateStatus(gameLogic.debugGrantBonus(TokenBank.BLACK, 1));
                        default -> {
                            return;
                        }
                    }

                    refreshFromGameLogic();
                });
            }
        });
    }

    /* ------------------------------------ Main Action Buttons ------------------------------------ */

    /**
     * Handles the Buy Card button.
     * The player must then click a face-up or reserved card to complete the action.
     */
    @FXML
    private void handleBuyCard() {
        if (isTurnLockedForNewAction()) {
            return;
        }

        currentMode = ActionMode.BUY_CARD;
        resetTokenActionMode();
        canTakeGoldAfterReserve = false;
        updateStatus(MoveResult.success("Click a face-up or reserved card to buy it"));
    }

    /**
     * Handles the Reserve Card button.
     * The player must then click a face-up card or top deck card to reserve.
     */
    @FXML
    private void handleReserveCard() {
        if (isTurnLockedForNewAction()) {
            return;
        }

        currentMode = ActionMode.RESERVE_CARD;
        resetTokenActionMode();
        canTakeGoldAfterReserve = false;
        updateStatus(MoveResult.success("Click a face-up/top-deck card to reserve it, then take a gold coin"));
    }

    /**
     * Handles the Take 3 Tokens button.
     * The player must select 3 different token colors.
     */
    @FXML
    private void handleTakeThreeTokens() {
        if (isTurnLockedForNewAction()) {
            return;
        }

        currentMode = ActionMode.TAKE_TOKENS;
        canTakeGoldAfterReserve = false;
        clearSelectedTokenColors();
        tokenActionMode = TokenActionMode.TAKE_THREE;
        updateStatus(MoveResult.success("Choose 3 different token colors."));
    }

    /**
     * Handles the Take 2 Same Tokens button.
     * The player must select 1 token color to take 2 of.
     */
    @FXML
    private void handleTakeTwoSameTokens() {
        if (isTurnLockedForNewAction()) {
            return;
        }

        currentMode = ActionMode.TAKE_TOKENS;
        canTakeGoldAfterReserve = false;
        clearSelectedTokenColors();
        tokenActionMode = TokenActionMode.TAKE_TWO_SAME;
        updateStatus(MoveResult.success("Choose 1 color to take 2 of."));
    }

    /**
     * Handles the End Turn button.
     * This attempts to end the player's turn, refreshes the UI,
     * checks for a winner, and runs the computer turn if needed.
     */
    @FXML
    private void handleEndTurn() {
        if (gameLogic == null) {
            return;
        }

        currentMode = ActionMode.NONE;
        resetTokenActionMode();
        canTakeGoldAfterReserve = false;

        MoveResult result = gameLogic.endTurn();
        updateStatus(result);

        if (didTurnActuallyEnd(result)) {
            turnActionCommitted = false;
        }

        refreshFromGameLogic();
        maybeShowWinnerPopup();
        maybeRunComputerTurn();
    }

    /* ------------------------------------ Board Click Handlers ------------------------------------ */

    /**
     * Handles clicks on face-up development cards on the board.
     * The result depends on whether the player is currently buying or reserving.
     *
     * @param tier the tier of the clicked card
     * @param index the position of the clicked card in that tier
     */
    private void handleFaceUpCardClick(int tier, int index) {
        if (gameLogic == null) {
            return;
        }

        if (gameLogic.isDiscardMode()) {
            updateStatus(MoveResult.fail(
                "You must discard " + gameLogic.getTokensToDiscard() + " more token(s) first."
            ));
            return;
        }

        if (turnActionCommitted) {
            updateStatus(MoveResult.fail("You already used your turn action. Press End Turn."));
            return;
        }

        MoveResult result;

        switch (currentMode) {
            case BUY_CARD:
                result = gameLogic.buyMarketCard(tier, index);
                finishStandardAction(result);
                break;
            case RESERVE_CARD:
                result = gameLogic.reserveFaceUpCard(tier, index);
                finishReserveAction(result);
                break;
            default:
                updateStatus(MoveResult.fail("Choose Buy Card or Reserve Card first."));
        }
    }

    /**
     * Handles clicks on the top deck of a development card tier.
     * Top deck cards may only be clicked when reserving.
     *
     * @param tier the tier of the clicked top deck
     */
    private void handleTopDeckClick(int tier) {
        if (gameLogic == null) {
            return;
        }

        if (gameLogic.isDiscardMode()) {
            updateStatus(MoveResult.fail(
                "You must discard " + gameLogic.getTokensToDiscard() + " more token(s) first."
            ));
            return;
        }

        if (turnActionCommitted) {
            updateStatus(MoveResult.fail("You already used your turn action. Press End Turn."));
            return;
        }

        if (currentMode != ActionMode.RESERVE_CARD) {
            updateStatus(MoveResult.fail("Can only reserve the top of deck. Choose Reserve Card first."));
            return;
        }

        MoveResult result = gameLogic.reserveTopDeckCard(tier);
        finishReserveAction(result);
    }

    /**
     * Handles clicks on noble cards on the board.
     * A noble can only be chosen when the game is waiting for a noble choice.
     *
     * @param boardIndex the index of the clicked noble on the board
     */
    private void handleNobleClick(int boardIndex) {
        if (gameLogic == null) {
            return;
        }

        if (!gameLogic.isWaitingForNobleChoice()) {
            updateStatus(MoveResult.fail("No noble choice is pending."));
            return;
        }

        Noble clickedNoble = gameLogic.getNobleFaceUp().getFaceUp().get(boardIndex);
        int pendingIndex = gameLogic.getPendingNobleChoices().indexOf(clickedNoble);

        if (pendingIndex == -1) {
            updateStatus(MoveResult.fail("Choose one of the eligible nobles."));
            return;
        }

        MoveResult result = gameLogic.chooseNoble(pendingIndex);

        // Choosing a noble can also finish the turn, so unlock the next player's turn state here.
        if (didTurnActuallyEnd(result)) {
            turnActionCommitted = false;
            currentMode = ActionMode.NONE;
            resetTokenActionMode();
            canTakeGoldAfterReserve = false;
        }

        updateStatus(result);
        refreshFromGameLogic();
        maybeShowWinnerPopup();
        maybeRunComputerTurn();
    }

    /**
     * Handles clicks on a reserved card inside the reserved-card popup.
     * Reserved cards can only be clicked for buying.
     *
     * @param reserveIndex the index of the reserved card
     * @param popupStage the popup window containing the reserved cards
     */
    private void handleReservedCardClick(int reserveIndex, Stage popupStage) {
        if (gameLogic == null) {
            return;
        }

        if (gameLogic.isDiscardMode()) {
            updateStatus(MoveResult.fail(
                "You must discard " + gameLogic.getTokensToDiscard() + " more token(s) first."
            ));
            return;
        }

        if (turnActionCommitted) {
            updateStatus(MoveResult.fail("You already used your turn action. Press End Turn."));
            return;
        }

        if (currentMode != ActionMode.BUY_CARD) {
            updateStatus(MoveResult.fail("Click Buy Card first, then choose a reserved card."));
            return;
        }

        MoveResult result = gameLogic.buyReservedCard(reserveIndex);

        if (!result.isSuccess()) {
            refreshFromGameLogic();
            updateStatus(result);
            return;
        }

        popupStage.close();
        finishStandardAction(result);
    }

    /* ------------------------------------ Game Logic Sync ------------------------------------ */

    /**
     * Attaches the given game logic to this controller and refreshes the UI.
     *
     * @param gameLogic the game logic instance for this game
     */
    public void setGameLogic(GameLogic gameLogic) {
        this.gameLogic = gameLogic;
        refreshFromGameLogic();
    }

    /**
     * Refreshes the board display from the current game state.
     */
    private void refreshBoardFromGameLogic() {
        if (gameLogic == null) {
            return;
        }

        boardView.loadNobles(gameLogic.getNobleFaceUp().getFaceUp());
        boardView.loadTier1(gameLogic.getDevelopmentFaceUp().getFaceUp(1));
        boardView.loadTier2(gameLogic.getDevelopmentFaceUp().getFaceUp(2));
        boardView.loadTier3(gameLogic.getDevelopmentFaceUp().getFaceUp(3));
        boardView.clearSelection();
    }

    /**
     * Refreshes all major UI elements from the current game state.
     * This includes current player info, token counts, button states, board content,
     * and discard popup state.
     */
    private void refreshFromGameLogic() {
        if (gameLogic == null) {
            return;
        }

        Player currentPlayer = gameLogic.getCurrentPlayer();

        currentPlayerLabel.setText(currentPlayer.getName());
        turnLabel.setText("Turn: " + gameLogic.getTurnNumber());
        pointsLabel.setText("Points: " + currentPlayer.getPoints());
        pointsLabel.setStyle("-fx-text-fill: white; -fx-font-size: 15px;");
        viewReservedButton.setText("View Reserved (" + currentPlayer.totalReserves() + ")");
        viewBoughtButton.setText("View Bought (" + currentPlayer.totalDevelopmentCards() + ")");
        viewNobleButton.setText("View Noble (" + currentPlayer.totalNobles() + ")");

        updateCurrentPlayerTokensBox(currentPlayer);
        updateTokenBankCounts();
        updateTokenButtonStates();
        updateActionButtonStates();
        refreshBoardFromGameLogic();

        if (gameLogic.isDiscardMode()) {
            updateStatus(MoveResult.success(
                "Discard " + gameLogic.getTokensToDiscard() + " more token(s)."
            ));
        }

        syncDiscardPopup();
    }

    /**
     * Checks whether the given move result means the turn has actually advanced.
     *
     * @param result the result returned by the game logic
     * @return true if the turn ended, false otherwise
     */
    private boolean didTurnActuallyEnd(MoveResult result) {
        String message = result.getMessage();

        return message.contains("Turn ended.")
            || message.contains("Final round triggered. Finish the current round.")
            || message.contains("Final round complete.")
            || message.contains("Player reached winning condition.");
    }

    /* ------------------------------------ UI Update Helpers ------------------------------------ */

    /**
     * Updates the current player's token and bonus display.
     *
     * @param player the player whose tokens and bonuses should be shown
     */
    private void updateCurrentPlayerTokensBox(Player player) {
        currentPlayerTokensBox.getChildren().clear();

        currentPlayerTokensBox.getChildren().addAll(
            createTokenBonusRow("Gold", player.getTokens("GOLD"), 0),
            createTokenBonusRow("Green", player.getTokens("GREEN"), player.getBonus("GREEN")),
            createTokenBonusRow("White", player.getTokens("WHITE"), player.getBonus("WHITE")),
            createTokenBonusRow("Black", player.getTokens("BLACK"), player.getBonus("BLACK")),
            createTokenBonusRow("Red", player.getTokens("RED"), player.getBonus("RED")),
            createTokenBonusRow("Blue", player.getTokens("BLUE"), player.getBonus("BLUE"))
        );
    }

    /**
     * Creates one row showing a token count and bonus count for a color.
     *
     * @param colorName the display name of the color
     * @param tokenCount the number of tokens the player has
     * @param bonusCount the number of bonuses the player has
     * @return the UI row displaying the token and bonus counts
     */
    private HBox createTokenBonusRow(String colorName, int tokenCount, int bonusCount) {
        Label leftLabel = new Label(colorName + ": " + tokenCount);
        leftLabel.setStyle("-fx-text-fill: white; -fx-font-size: 15px;");

        Label rightLabel = new Label("(+" + bonusCount + ")");
        rightLabel.setStyle("-fx-text-fill: #cbd5e1; -fx-font-weight: bold; -fx-font-size: 15px;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        HBox row = new HBox(8);
        row.getChildren().addAll(leftLabel, spacer, rightLabel);

        return row;
    }

    /**
     * Updates the bottom status bar using the given move result.
     *
     * @param result the result to display
     */
    private void updateStatus(MoveResult result) {
        statusBarLabel.setText(result.getMessage());

        if (result.isSuccess()) {
            statusIcon.setText("✓");
            statusBar.setStyle(
                "-fx-background-color: rgba(75,85,99,0.92);"
                    + "-fx-background-radius: 14 14 0 0;"
                    + "-fx-padding: 0 18 0 18;"
                    + "-fx-border-color: rgba(255,255,255,0.12);"
                    + "-fx-border-width: 1 0 0 0;"
            );
        } else {
            statusIcon.setText("✕");
            statusBar.setStyle(
                "-fx-background-color: rgba(199, 72, 72, 0.92);"
                    + "-fx-background-radius: 14 14 0 0;"
                    + "-fx-padding: 0 18 0 18;"
                    + "-fx-border-color: rgba(255,255,255,0.12);"
                    + "-fx-border-width: 1 0 0 0;"
            );
        }

        statusIcon.setStyle(
            "-fx-background-color: rgba(255,255,255,0.22);"
                + "-fx-background-radius: 999;"
                + "-fx-text-fill: white;"
                + "-fx-font-size: 15px;"
                + "-fx-font-weight: bold;"
        );
    }

    /**
     * Updates the token counts shown for the shared token bank.
     */
    private void updateTokenBankCounts() {
        if (gameLogic == null) {
            return;
        }

        TokenBank bank = gameLogic.getTokenBank();

        goldBankCountLabel.setText(String.valueOf(bank.get(TokenBank.GOLD)));
        greenBankCountLabel.setText(String.valueOf(bank.get(TokenBank.GREEN)));
        whiteBankCountLabel.setText(String.valueOf(bank.get(TokenBank.WHITE)));
        blackBankCountLabel.setText(String.valueOf(bank.get(TokenBank.BLACK)));
        redBankCountLabel.setText(String.valueOf(bank.get(TokenBank.RED)));
        blueBankCountLabel.setText(String.valueOf(bank.get(TokenBank.BLUE)));
    }

    /**
     * Updates the main action button appearance based on whether the player's turn
     * is currently locked by a completed action or by discard mode.
     */
    private void updateActionButtonStates() {
        boolean locked = turnActionCommitted || (gameLogic != null && gameLogic.isDiscardMode());

        String enabledStyle = "";
        String lockedStyle =
            "-fx-opacity: 0.55;"
                + "-fx-background-color: #999999;"
                + "-fx-text-fill: #dddddd;";

        takeThreeTokensButton.setStyle(locked ? lockedStyle : enabledStyle);
        takeTwoTokensButton.setStyle(locked ? lockedStyle : enabledStyle);
        buyCardButton.setStyle(locked ? lockedStyle : enabledStyle);
        reserveCardButton.setStyle(locked ? lockedStyle : enabledStyle);
    }

    /**
     * Enables or disables token buttons based on the current game state.
     * During discard mode, buttons are enabled only for token colors the current player owns.
     * Otherwise, buttons reflect what is available in the shared bank.
     */
    private void updateTokenButtonStates() {
        if (gameLogic == null) {
            return;
        }

        TokenBank bank = gameLogic.getTokenBank();
        Player currentPlayer = gameLogic.getCurrentPlayer();

        if (gameLogic.isDiscardMode()) {
            greenBtn.setDisable(currentPlayer.getTokens(TokenBank.GREEN) == 0);
            whiteBtn.setDisable(currentPlayer.getTokens(TokenBank.WHITE) == 0);
            blackBtn.setDisable(currentPlayer.getTokens(TokenBank.BLACK) == 0);
            redBtn.setDisable(currentPlayer.getTokens(TokenBank.RED) == 0);
            blueBtn.setDisable(currentPlayer.getTokens(TokenBank.BLUE) == 0);
            goldBtn.setDisable(currentPlayer.getTokens(TokenBank.GOLD) == 0);
            return;
        }

        greenBtn.setDisable(bank.get(TokenBank.GREEN) == 0);
        whiteBtn.setDisable(bank.get(TokenBank.WHITE) == 0);
        blackBtn.setDisable(bank.get(TokenBank.BLACK) == 0);
        redBtn.setDisable(bank.get(TokenBank.RED) == 0);
        blueBtn.setDisable(bank.get(TokenBank.BLUE) == 0);
        goldBtn.setDisable(bank.get(TokenBank.GOLD) == 0 && !canTakeGoldAfterReserve);
    }

    /* ------------------------------------ Popup Windows ------------------------------------ */

    /**
     * Shows a popup window containing development cards.
     *
     * @param title the popup title
     * @param cards the cards to display
     * @param reservedPopup true if the popup is showing reserved cards
     */
    private void showCardsPopup(String title, List<DevelopmentCard> cards, boolean reservedPopup) {
        Stage popupStage = new Stage();
        popupStage.initOwner(root.getScene().getWindow());
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle(title);

        FlowPane cardPane = new FlowPane();
        cardPane.setHgap(16);
        cardPane.setVgap(16);
        cardPane.setPrefWrapLength(700);
        cardPane.setAlignment(Pos.TOP_LEFT);
        cardPane.setStyle("-fx-padding: 20; -fx-background-color: #1f2937;");

        if (cards == null || cards.isEmpty()) {
            Label emptyLabel = new Label("No cards to show.");
            emptyLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18px;");
            cardPane.getChildren().add(emptyLabel);
        } else {
            for (int i = 0; i < cards.size(); i++) {
                cardPane.getChildren().add(createCardPopupNode(cards.get(i), i, popupStage, reservedPopup));
            }
        }

        ScrollPane scrollPane = new ScrollPane(cardPane);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #1f2937; -fx-background-color: #1f2937;");

        Scene scene = new Scene(scrollPane, 800, 550);
        popupStage.setScene(scene);
        popupStage.show();
    }

    /**
     * Creates one card node for a development card popup.
     *
     * @param card the card to display
     * @param reserveIndex the index of the card if it is a reserved card
     * @param popupStage the popup window containing the card
     * @param reservedPopup true if the popup is for reserved cards
     * @return the UI node displaying the card
     */
    private VBox createCardPopupNode(DevelopmentCard card, int reserveIndex, Stage popupStage, boolean reservedPopup) {
        VBox box = new VBox(8);
        box.setAlignment(Pos.CENTER);

        String imagePath = "/UI/images/cards/devCards/tier" + card.getLevel() + "/" + card.getID() + ".png";

        CardView cardView = new CardView(card.getID(), imagePath, 140, 200);
        box.getChildren().add(cardView);

        if (reservedPopup) {
            cardView.setOnMouseClicked(e -> handleReservedCardClick(reserveIndex, popupStage));
        }

        Label idLabel = new Label(card.getID());
        idLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");
        box.getChildren().add(idLabel);

        return box;
    }

    /**
     * Shows a popup window containing noble cards.
     *
     * @param title the popup title
     * @param nobles the nobles to display
     */
    private void showNoblesPopup(String title, List<Noble> nobles) {
        Stage popupStage = new Stage();
        popupStage.initOwner(root.getScene().getWindow());
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle(title);

        FlowPane cardPane = new FlowPane();
        cardPane.setHgap(16);
        cardPane.setVgap(16);
        cardPane.setPrefWrapLength(700);
        cardPane.setAlignment(Pos.TOP_LEFT);
        cardPane.setStyle("-fx-padding: 20; -fx-background-color: #1f2937;");

        if (nobles == null || nobles.isEmpty()) {
            Label emptyLabel = new Label("No noble cards to show.");
            emptyLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18px;");
            cardPane.getChildren().add(emptyLabel);
        } else {
            for (Noble noble : nobles) {
                cardPane.getChildren().add(createNoblePopupNode(noble));
            }
        }

        ScrollPane scrollPane = new ScrollPane(cardPane);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #1f2937; -fx-background-color: #1f2937;");

        Scene scene = new Scene(scrollPane, 800, 550);
        popupStage.setScene(scene);
        popupStage.show();
    }

    /**
     * Creates one card node for a noble popup.
     *
     * @param noble the noble to display
     * @return the UI node displaying the noble
     */
    private VBox createNoblePopupNode(Noble noble) {
        VBox box = new VBox(8);
        box.setAlignment(Pos.CENTER);

        String imagePath = "/UI/images/cards/nobleCards/" + noble.getID() + ".png";
        CardView cardView = new CardView(noble.getID(), imagePath, 140, 140);
        box.getChildren().add(cardView);

        Label idLabel = new Label(noble.getID());
        idLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");
        box.getChildren().add(idLabel);

        return box;
    }

    /**
     * Shows the winner popup once when the game has ended.
     */
    private void maybeShowWinnerPopup() {
        if (winnerPopupShown || gameLogic == null || !gameLogic.isGameOver()) {
            return;
        }

        winnerPopupShown = true;

        List<Player> winners = gameLogic.determineWinners();
        String winnerText;

        if (winners.size() == 1) {
            winnerText = winners.get(0).getName();
        } else {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < winners.size(); i++) {
                if (i > 0) {
                    sb.append(i == winners.size() - 1 ? " & " : ", ");
                }
                sb.append(winners.get(i).getName());
            }
            winnerText = sb.toString();
        }

        showWinnerPopup(winnerText);
    }

    /**
     * Displays the final winner popup with celebration effects.
     *
     * @param winnerName the winning player's name, or names in case of a tie
     */
    private void showWinnerPopup(String winnerName) {
        Stage popupStage = new Stage();
        popupStage.initOwner(root.getScene().getWindow());
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Winner!");

        Label confettiLabel = new Label("<333333333");
        confettiLabel.setStyle("-fx-font-size: 34px;");

        Label yayLabel = new Label("YAYYYY");
        yayLabel.setStyle(
            "-fx-text-fill: #fff7b2;"
                + "-fx-font-size: 42px;"
                + "-fx-font-weight: bold;"
        );

        Label winnerLabel = new Label(winnerName + " HAS WON!");
        winnerLabel.setStyle(
            "-fx-text-fill: white;"
                + "-fx-font-size: 28px;"
                + "-fx-font-weight: bold;"
        );

        Label subLabel = new Label("Thanks for playing! :)");
        subLabel.setStyle(
            "-fx-text-fill: #fde68a;"
                + "-fx-font-size: 18px;"
        );

        Button closeBtn = new Button("quit");
        closeBtn.setOnAction(e -> Platform.exit());
        closeBtn.setStyle(
            "-fx-font-size: 16px;"
                + "-fx-font-weight: bold;"
                + "-fx-background-radius: 16;"
                + "-fx-padding: 8 18 8 18;"
        );

        VBox popupBox = new VBox(16, confettiLabel, yayLabel, winnerLabel, subLabel, closeBtn);
        popupBox.setAlignment(Pos.CENTER);
        popupBox.setStyle(
            "-fx-background-color: linear-gradient(to bottom, #6320ff, #ff4dd8);"
                + "-fx-background-radius: 26;"
                + "-fx-padding: 28;"
        );
        popupBox.setPrefWidth(430);

        StackPane overlay = new StackPane(popupBox);
        overlay.setAlignment(Pos.CENTER);
        overlay.setStyle(
            "-fx-background-color: rgba(15,23,42,0.72);"
                + "-fx-padding: 24;"
        );

        Scene scene = new Scene(overlay, 700, 450);
        popupStage.setScene(scene);

        ScaleTransition bounce = new ScaleTransition(Duration.millis(700), yayLabel);
        bounce.setFromX(0.7);
        bounce.setFromY(0.7);
        bounce.setToX(1.12);
        bounce.setToY(1.12);
        bounce.setAutoReverse(true);
        bounce.setCycleCount(2);

        RotateTransition wiggle = new RotateTransition(Duration.millis(160), winnerLabel);
        wiggle.setByAngle(5);
        wiggle.setAutoReverse(true);
        wiggle.setCycleCount(6);

        TranslateTransition floaty = new TranslateTransition(Duration.millis(700), confettiLabel);
        floaty.setFromY(0);
        floaty.setToY(-8);
        floaty.setAutoReverse(true);
        floaty.setCycleCount(4);

        popupStage.show();

        bounce.play();
        wiggle.play();
        floaty.play();
    }

    /* ------------------------------------ Discard Popup Helpers ------------------------------------ */

    /**
     * Opens, updates, or closes the discard popup based on the current game state.
     * The popup is only shown for human players while they must discard tokens.
     */
    private void syncDiscardPopup() {
        if (gameLogic == null) {
            return;
        }

        if (gameLogic.getCurrentPlayer() instanceof Computer) {
            closeDiscardPopup();
            return;
        }

        if (gameLogic.isDiscardMode()) {
            if (discardPopupStage == null || !discardPopupStage.isShowing()) {
                showDiscardPopup();
            }
            updateDiscardPopup();
        } else {
            closeDiscardPopup();
        }
    }

    /**
     * Shows the discard popup window for the current player.
     * The popup displays each token color horizontally with its current inventory count.
     */
    private void showDiscardPopup() {
        discardCountLabels.clear();
        discardButtons.clear();

        discardPopupStage = new Stage();
        discardPopupStage.initOwner(root.getScene().getWindow());
        discardPopupStage.initModality(Modality.APPLICATION_MODAL);
        discardPopupStage.setTitle("Discard Tokens");

        discardPopupStage.setOnCloseRequest(e -> {
            if (gameLogic != null && gameLogic.isDiscardMode()) {
                e.consume();
            }
        });

        Label titleLabel = new Label("Discard tokens");
        titleLabel.setStyle(
            "-fx-text-fill: white;"
                + "-fx-font-size: 22px;"
                + "-fx-font-weight: bold;"
        );

        discardPopupInfoLabel = new Label();
        discardPopupInfoLabel.setStyle(
            "-fx-text-fill: #e5e7eb;"
                + "-fx-font-size: 16px;"
        );

        HBox tokenRow = new HBox(18);
        tokenRow.setAlignment(Pos.CENTER);

        tokenRow.getChildren().addAll(
            createDiscardTokenNode(TokenBank.GOLD, "gold.png"),
            createDiscardTokenNode(TokenBank.GREEN, "greenGem.png"),
            createDiscardTokenNode(TokenBank.WHITE, "whiteGem.png"),
            createDiscardTokenNode(TokenBank.BLACK, "blackGem.png"),
            createDiscardTokenNode(TokenBank.RED, "redGem.png"),
            createDiscardTokenNode(TokenBank.BLUE, "blueGem.png")
        );

        VBox layout = new VBox(18, titleLabel, discardPopupInfoLabel, tokenRow);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(24));
        layout.setStyle(
            "-fx-background-color: #1f2937;"
                + "-fx-background-radius: 18;"
        );

        StackPane rootPane = new StackPane(layout);
        rootPane.setPadding(new Insets(20));
        rootPane.setStyle("-fx-background-color: rgba(15,23,42,0.75);");

        Scene scene = new Scene(rootPane, 760, 280);
        discardPopupStage.setScene(scene);
        discardPopupStage.show();

        updateDiscardPopup();
    }

    /**
     * Creates one token entry for the discard popup.
     * Each entry shows a clickable token button, the player's count, and the color label.
     *
     * @param color the token color represented by this popup entry
     * @param imageFile the image file used for the token icon
     * @return the UI node for this token entry
     */
    private VBox createDiscardTokenNode(String color, String imageFile) {
        ImageView imageView = new ImageView(loadTokenImage(imageFile));
        imageView.setFitWidth(56);
        imageView.setFitHeight(56);
        imageView.setPreserveRatio(true);

        Button tokenButton = new Button();
        tokenButton.setGraphic(imageView);
        tokenButton.setMinSize(76, 76);
        tokenButton.setPrefSize(76, 76);
        tokenButton.setMaxSize(76, 76);
        tokenButton.setStyle(
            "-fx-background-color: rgba(255,255,255,0.10);"
                + "-fx-background-radius: 999;"
                + "-fx-border-color: rgba(255,255,255,0.20);"
                + "-fx-border-radius: 999;"
                + "-fx-cursor: hand;"
        );
        tokenButton.setOnAction(e -> handleDiscard(color));

        Label countLabel = new Label("0");
        countLabel.setStyle(
            "-fx-text-fill: white;"
                + "-fx-font-size: 16px;"
                + "-fx-font-weight: bold;"
        );

        Label nameLabel = new Label(color);
        nameLabel.setStyle(
            "-fx-text-fill: #cbd5e1;"
                + "-fx-font-size: 12px;"
        );

        discardButtons.put(color, tokenButton);
        discardCountLabels.put(color, countLabel);

        VBox box = new VBox(8, tokenButton, countLabel, nameLabel);
        box.setAlignment(Pos.CENTER);
        return box;
    }

    /**
     * Discards one token of the given color for the current player.
     * This updates the main UI, the status bar, and closes the popup when discarding is complete.
     *
     * @param color the token color to discard
     */
    private void handleDiscard(String color) {
        if (gameLogic == null || !gameLogic.isDiscardMode()) {
            return;
        }

        MoveResult result = gameLogic.discardToken(color);
        refreshFromGameLogic();
        updateStatus(result);

        if (result.isSuccess() && !gameLogic.isDiscardMode()) {
            updateStatus(MoveResult.success(result.getMessage() + " Press End Turn when ready."));
        }
    }

    /**
     * Refreshes the discard popup so it shows the latest discard requirement
     * and the current player's token counts.
     */
    private void updateDiscardPopup() {
        if (gameLogic == null || discardPopupStage == null || !discardPopupStage.isShowing()) {
            return;
        }

        Player player = gameLogic.getCurrentPlayer();

        discardPopupInfoLabel.setText(
            "Discard " + gameLogic.getTokensToDiscard() + " more token(s)"
        );

        updateDiscardPopupToken(TokenBank.GOLD, player.getTokens(TokenBank.GOLD));
        updateDiscardPopupToken(TokenBank.GREEN, player.getTokens(TokenBank.GREEN));
        updateDiscardPopupToken(TokenBank.WHITE, player.getTokens(TokenBank.WHITE));
        updateDiscardPopupToken(TokenBank.BLACK, player.getTokens(TokenBank.BLACK));
        updateDiscardPopupToken(TokenBank.RED, player.getTokens(TokenBank.RED));
        updateDiscardPopupToken(TokenBank.BLUE, player.getTokens(TokenBank.BLUE));
    }

    /**
     * Updates one token entry in the discard popup.
     * The button is disabled when the player has none of that token color.
     *
     * @param color the token color being updated
     * @param count the player's current number of tokens of that color
     */
    private void updateDiscardPopupToken(String color, int count) {
        Label countLabel = discardCountLabels.get(color);
        Button button = discardButtons.get(color);

        if (countLabel != null) {
            countLabel.setText(String.valueOf(count));
        }

        if (button != null) {
            button.setDisable(count == 0);
            button.setOpacity(count == 0 ? 0.45 : 1.0);
        }
    }

    /**
     * Closes the discard popup and clears its cached UI references.
     */
    private void closeDiscardPopup() {
        if (discardPopupStage != null) {
            discardPopupStage.close();
            discardPopupStage = null;
        }

        discardButtons.clear();
        discardCountLabels.clear();
        discardPopupInfoLabel = null;
    }

    /* ------------------------------------ Computer Turn Logic ------------------------------------ */

    /**
     * Runs the computer player's turn if the current player is a computer.
     * The computer pauses briefly before acting to make the turn easier to follow.
     */
    private void maybeRunComputerTurn() {
        if (gameLogic == null || gameLogic.isGameOver()) {
            return;
        }

        if (!(gameLogic.getCurrentPlayer() instanceof Computer)) {
            return;
        }

        PauseTransition actionPause = new PauseTransition(Duration.millis(3000));
        actionPause.setOnFinished(e -> {
            MoveResult actionResult = ComputerService.performMainAction(gameLogic);
            updateStatus(actionResult);
            refreshFromGameLogic();

            resolveComputerDiscardIfNeeded();

            boolean computerReservedCard = actionResult.isSuccess()
                && actionResult.getMessage() != null
                && actionResult.getMessage().toLowerCase().contains("reserved");

            // After reserving, let the computer take gold if gold is available
            if (computerReservedCard && gameLogic.getTokenBank().get(TokenBank.GOLD) > 0) {
                PauseTransition goldPause = new PauseTransition(Duration.millis(2000));
                goldPause.setOnFinished(e2 -> {
                    MoveResult goldResult = gameLogic.takeGold();
                    updateStatus(goldResult);
                    refreshFromGameLogic();
                    resolveComputerDiscardIfNeeded();
                    runComputerEndTurn();
                });
                goldPause.play();
            } else {
                runComputerEndTurn();
            }
        });

        updateStatus(MoveResult.success("Computer is thinking..."));
        actionPause.play();
    }

    /**
     * Ends the computer player's turn after a short delay.
     * If a noble choice is required, the computer automatically chooses the first option.
     */
    private void runComputerEndTurn() {
        PauseTransition endPause = new PauseTransition(Duration.millis(2000));
        endPause.setOnFinished(e -> {
            MoveResult endResult = gameLogic.endTurn();
            updateStatus(endResult);
            refreshFromGameLogic();

            if (gameLogic.isWaitingForNobleChoice()) {
                PauseTransition noblePause = new PauseTransition(Duration.millis(2000));
                noblePause.setOnFinished(e2 -> {
                    MoveResult nobleResult = gameLogic.chooseNoble(0);
                    updateStatus(nobleResult);
                    refreshFromGameLogic();
                    maybeShowWinnerPopup();
                    maybeRunComputerTurn();
                });
                noblePause.play();
                return;
            }

            maybeShowWinnerPopup();
            maybeRunComputerTurn();
        });
        endPause.play();
    }

    /**
     * Resolves the computer player's discard requirement automatically.
     * The computer discards one token at a time until it is no longer over the token limit.
     */
    private void resolveComputerDiscardIfNeeded() {
        if (gameLogic == null || !(gameLogic.getCurrentPlayer() instanceof Computer)) {
            return;
        }

        Player player = gameLogic.getCurrentPlayer();
        String[] discardOrder = {
            TokenBank.GOLD,
            TokenBank.GREEN,
            TokenBank.WHITE,
            TokenBank.BLACK,
            TokenBank.RED,
            TokenBank.BLUE
        };

        while (gameLogic.isDiscardMode()) {
            boolean discarded = false;

            for (String color : discardOrder) {
                if (player.getTokens(color) > 0) {
                    gameLogic.discardToken(color);
                    discarded = true;
                    break;
                }
            }

            if (!discarded) {
                break;
            }
        }

        refreshFromGameLogic();
    }

    /* ------------------------------------ Background Setup ------------------------------------ */

    /**
     * Loads all background images used in the game screen.
     */
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

    /**
     * Sets up the cloud layers so they fill the screen and scroll correctly.
     */
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

        // Position the B copy just to the left of the A copy
        resetPair(gameCloud1A, gameCloud1B, BASE_W);
        resetPair(gameCloud2A, gameCloud2B, BASE_W);
        resetPair(gameCloud3A, gameCloud3B, BASE_W);
        resetPair(gameCloud4A, gameCloud4B, BASE_W);
        resetPair(gameCloud5A, gameCloud5B, BASE_W);

        // Use opacity to give the clouds a depth effect
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
        v.setMouseTransparent(true);
    }

    /**
     * Resets a pair of scrolling cloud images so one starts immediately to the left of the other.
     *
     * @param a the first image
     * @param b the second image
     * @param width the width used for positioning
     */
    private void resetPair(ImageView a, ImageView b, double width) {
        a.setLayoutX(0);
        b.setLayoutX(-width);
    }

    /**
     * Starts the cloud scrolling animation.
     */
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
                if (w <= 0) {
                    return;
                }

                scrollPair(gameCloud1A, gameCloud1B, LAYER1_SPEED * dt, w);
                scrollPair(gameCloud2A, gameCloud2B, LAYER2_SPEED * dt, w);
                scrollPair(gameCloud3A, gameCloud3B, LAYER3_SPEED * dt, w);
                scrollPair(gameCloud4A, gameCloud4B, LAYER4_SPEED * dt, w);
                scrollPair(gameCloud5A, gameCloud5B, LAYER5_SPEED * dt, w);
            }
        };

        cloudTimer.start();
    }

    /**
     * Scrolls a pair of cloud images horizontally and wraps them when needed.
     *
     * @param a the first image in the pair
     * @param b the second image in the pair
     * @param dx the horizontal movement amount
     * @param w the width of the screen
     */
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

    /**
     * Loads one background image from the game background resources.
     *
     * @param fileName the file name of the image
     * @return the loaded image
     */
    private Image loadBackgroundImage(String fileName) {
        String path = "/UI/images/backgrounds/gameBackgrounds/" + fileName;
        URL resource = getClass().getResource(path);

        if (resource == null) {
            throw new IllegalArgumentException("Could not find image: " + path);
        }

        return new Image(resource.toExternalForm());
    }

    /**
     * Loads all token images used in the UI.
     */
    private void loadTokenImages() {
        goldTokenView.setImage(loadTokenImage("gold.png"));
        greenTokenView.setImage(loadTokenImage("greenGem.png"));
        whiteTokenView.setImage(loadTokenImage("whiteGem.png"));
        blackTokenView.setImage(loadTokenImage("blackGem.png"));
        redTokenView.setImage(loadTokenImage("redGem.png"));
        blueTokenView.setImage(loadTokenImage("blueGem.png"));
    }

    /**
     * Loads one token image from the token image resources.
     *
     * @param fileName the file name of the image
     * @return the loaded token image
     */
    private Image loadTokenImage(String fileName) {
        String path = "/UI/images/tokens/" + fileName;
        URL resource = getClass().getResource(path);

        if (resource == null) {
            throw new IllegalArgumentException("Could not find token image: " + path);
        }

        return new Image(resource.toExternalForm());
    }

    /* ------------------------------------ Token Button Clicks ------------------------------------ */

    /**
     * Handles clicking the gold token button.
     */
    @FXML
    private void onGoldTokenClick() {
        handleGoldTokenClick();
    }

    /**
     * Handles clicking the green token button.
     */
    @FXML
    private void onGreenTokenClick() {
        handleTokenClick(TokenBank.GREEN);
    }

    /**
     * Handles clicking the white token button.
     */
    @FXML
    private void onWhiteTokenClick() {
        handleTokenClick(TokenBank.WHITE);
    }

    /**
     * Handles clicking the black token button.
     */
    @FXML
    private void onBlackTokenClick() {
        handleTokenClick(TokenBank.BLACK);
    }

    /**
     * Handles clicking the red token button.
     */
    @FXML
    private void onRedTokenClick() {
        handleTokenClick(TokenBank.RED);
    }

    /**
     * Handles clicking the blue token button.
     */
    @FXML
    private void onBlueTokenClick() {
        handleTokenClick(TokenBank.BLUE);
    }

    /* ------------------------------------ View Buttons and Token Logic ------------------------------------ */

    /**
     * Shows the current player's reserved cards in a popup window.
     */
    @FXML
    private void handleViewReserved() {
        if (gameLogic == null) {
            return;
        }

        Player currentPlayer = gameLogic.getCurrentPlayer();
        showCardsPopup("Reserved Cards", currentPlayer.getReservedCards(), true);
    }

    /**
     * Shows the current player's bought cards in a popup window.
     */
    @FXML
    private void handleViewBought() {
        if (gameLogic == null) {
            return;
        }

        Player currentPlayer = gameLogic.getCurrentPlayer();
        showCardsPopup("Bought Cards", currentPlayer.getBoughtCards(), false);
    }

    /**
     * Shows the current player's nobles in a popup window.
     */
    @FXML
    private void handleViewNoble() {
        if (gameLogic == null) {
            return;
        }

        Player currentPlayer = gameLogic.getCurrentPlayer();
        showNoblesPopup("Noble Cards", currentPlayer.getPlayerNobles());
    }

    /**
     * Handles clicks on a non-gold token button.
     * During discard mode, the clicked token is discarded immediately.
     * Otherwise, the click is treated as part of the current token-taking action.
     *
     * @param color the clicked token color
     */
    private void handleTokenClick(String color) {
        if (gameLogic == null) {
            return;
        }

        if (gameLogic.isDiscardMode()) {
            handleDiscard(color);
            return;
        }

        if (turnActionCommitted) {
            updateStatus(MoveResult.fail("You already used your turn action. Press End Turn."));
            return;
        }

        if (currentMode != ActionMode.TAKE_TOKENS || tokenActionMode == TokenActionMode.NONE) {
            updateStatus(MoveResult.fail("Choose Take 3 Tokens or Take 2 Same Tokens first."));
            return;
        }

        switch (tokenActionMode) {
            case TAKE_THREE -> handleTakeThreeSelection(color);
            case TAKE_TWO_SAME -> handleTakeTwoSameSelection(color);
            default -> {
            }
        }
    }

    /**
     * Handles one token selection while the player is taking 3 different tokens.
     *
     * @param color the selected token color
     */
    private void handleTakeThreeSelection(String color) {
        if (selectedTokenColors.contains(color)) {
            updateStatus(MoveResult.fail("Pick 3 different colors."));
            return;
        }

        selectedTokenColors.add(color);

        if (selectedTokenColors.size() < 3) {
            updateStatus(MoveResult.success(
                "Selected " + selectedTokenColors.size() + "/3 colors."
            ));
            return;
        }

        MoveResult result = gameLogic.takeThreeTokens(
            selectedTokenColors.get(0),
            selectedTokenColors.get(1),
            selectedTokenColors.get(2)
        );

        finishTokenAction(result);
    }

    /**
     * Handles the token selection for taking 2 of the same token color.
     *
     * @param color the selected token color
     */
    private void handleTakeTwoSameSelection(String color) {
        MoveResult result = gameLogic.takeTwoTokens(color);
        finishTokenAction(result);
    }

    /**
     * Finishes a token-taking action and updates the UI accordingly.
     *
     * @param result the result of the token action
     */
    private void finishTokenAction(MoveResult result) {
        if (!result.isSuccess()) {
            clearSelectedTokenColors();
            refreshFromGameLogic();
            updateStatus(result);
            return;
        }

        resetTokenActionMode();
        currentMode = ActionMode.NONE;
        turnActionCommitted = true;
        refreshFromGameLogic();

        if (gameLogic.isDiscardMode()) {
            updateStatus(result);
        } else {
            updateStatus(MoveResult.success(result.getMessage() + " Press End Turn when ready."));
        }
    }

    /**
     * Clears the list of currently selected token colors.
     */
    private void clearSelectedTokenColors() {
        selectedTokenColors.clear();
    }

    /**
     * Resets token-taking mode and clears any selected token colors.
     */
    private void resetTokenActionMode() {
        tokenActionMode = TokenActionMode.NONE;
        selectedTokenColors.clear();
    }

    /**
     * Finishes a normal action such as buying a card and updates the UI.
     *
     * @param result the result of the action
     */
    private void finishStandardAction(MoveResult result) {
        if (!result.isSuccess()) {
            updateStatus(result);
            refreshFromGameLogic();
            return;
        }

        currentMode = ActionMode.NONE;
        resetTokenActionMode();
        turnActionCommitted = true;
        refreshFromGameLogic();

        updateStatus(MoveResult.success(result.getMessage() + " Press End Turn when ready."));
    }

    /**
     * Finishes a reserve action and allows a gold token to be taken if available.
     *
     * @param result the result of the reserve action
     */
    private void finishReserveAction(MoveResult result) {
        if (!result.isSuccess()) {
            canTakeGoldAfterReserve = false;
            refreshFromGameLogic();
            updateStatus(result);
            return;
        }

        turnActionCommitted = true;

        if (gameLogic.getTokenBank().get(TokenBank.GOLD) > 0) {
            canTakeGoldAfterReserve = true;
            refreshFromGameLogic();
            updateStatus(MoveResult.success(
                result.getMessage() + " Click gold to take 1 gold, or End Turn."
            ));
        } else {
            canTakeGoldAfterReserve = false;
            currentMode = ActionMode.NONE;
            refreshFromGameLogic();
            updateStatus(MoveResult.success(
                result.getMessage() + " No gold left in bank. Press End Turn."
            ));
        }
    }

    /**
     * Handles clicking the gold token button.
     * During discard mode, clicking gold discards a gold token.
     * Otherwise, gold may only be taken after a successful reserve action.
     */
    private void handleGoldTokenClick() {
        if (gameLogic == null) {
            return;
        }

        if (gameLogic.isDiscardMode()) {
            handleDiscard(TokenBank.GOLD);
            return;
        }

        if (currentMode != ActionMode.RESERVE_CARD || !canTakeGoldAfterReserve) {
            updateStatus(MoveResult.fail("You can only take gold after successfully reserving a card."));
            return;
        }

        MoveResult result = gameLogic.takeGold();

        if (!result.isSuccess()) {
            refreshFromGameLogic();
            updateStatus(result);
            return;
        }

        canTakeGoldAfterReserve = false;
        currentMode = ActionMode.NONE;
        refreshFromGameLogic();

        if (gameLogic.isDiscardMode()) {
            updateStatus(result);
        } else {
            updateStatus(MoveResult.success(result.getMessage() + " Press End Turn when ready."));
        }
    }

    /**
     * Checks whether the player has already used their main action this turn
     * or must complete discarding before choosing a new action.
     *
     * @return true if the turn is locked for a new action, false otherwise
     */
    private boolean isTurnLockedForNewAction() {
        if (gameLogic != null && gameLogic.isDiscardMode()) {
            updateStatus(MoveResult.fail(
                "You must discard " + gameLogic.getTokensToDiscard() + " more token(s) first."
            ));
            return true;
        }

        if (turnActionCommitted) {
            updateStatus(MoveResult.fail("You already used your turn action. Press End Turn."));
            return true;
        }

        return false;
    }
}
