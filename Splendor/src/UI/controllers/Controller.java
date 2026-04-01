package UI.controllers;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.layout.Region;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import Cards.Token.TokenBank;
import Cards.DevelopmentCard.DevelopmentCard;
import UI.components.BoardView;
import UI.components.CardView;
import Player.Player;
import Test.GameLogic;
import Test.MoveResult;

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
    @FXML private Label goldBankCountLabel;
    @FXML private Label greenBankCountLabel;
    @FXML private Label whiteBankCountLabel;
    @FXML private Label blackBankCountLabel;
    @FXML private Label redBankCountLabel;
    @FXML private Label blueBankCountLabel;

    // Options Menu Label
    @FXML private Label currentPlayerLabel;
    @FXML private Label turnLabel;

    // 5 Choices Buttons
    @FXML private Button takeThreeTokensButton;
    @FXML private Button takeTwoTokensButton;
    @FXML private Button buyCardButton;
    @FXML private Button reserveCardButton;
    @FXML private Button endTurnButton;

    @FXML private Label pointsLabel;
    @FXML private VBox currentPlayerTokensBox;

    @FXML private Button viewReservedButton;
    @FXML private Button viewBoughtButton;

    // Board (4 rows of cards)
    @FXML private StackPane boardContainer;

    // Bottom Status Label
    @FXML private Label statusBarLabel;
    @FXML private HBox statusBar;
    @FXML private Label statusIcon;


    // Back end!
    private BoardView boardView;
    private GameLogic gameLogic;
    private enum ActionMode {
        NONE, TAKE_TOKENS, BUY_CARD, RESERVE_CARD
    }
    private ActionMode currentMode = ActionMode.NONE;

    private enum TokenActionMode {
        NONE,
        TAKE_THREE,
        TAKE_TWO_SAME
    }

    private TokenActionMode tokenActionMode = TokenActionMode.NONE;
    private final ArrayList<String> selectedTokenColors = new ArrayList<>();

    // Tracks if player has successfully reserved a card this turn yet
    private boolean canTakeGoldAfterReserve = false;
    
    // Tracks if player has used their main action yet (view reserved or view bought not a main action)
    private boolean turnActionCommitted = false;


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

        boardView.setOnFaceUpCardClick((tier, index) -> handleFaceUpCardClick(tier, index));
        boardView.setOnTopDeckClick(tier -> handleTopDeckClick(tier));
        boardView.setOnNobleClick(index -> handleNobleClick(index));

    }


/* ------------------------------------ Main Actions ------------------------------------ */

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
    }




/* ------------------------------------ Main Action Helpers ------------------------------------ */
    private void handleFaceUpCardClick(int tier, int index) {
        if (gameLogic == null) {
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
                return;
        }
    }
    
    private void handleTopDeckClick(int tier) {
        if (gameLogic == null) {
            return;
        }

        if (turnActionCommitted) {
            updateStatus(MoveResult.fail("You already used your turn action. Press End Turn."));
            return;
        }
    
        if (currentMode != ActionMode.RESERVE_CARD) {
            updateStatus(MoveResult.fail("Choose Reserve Card first."));
            return;
        }
    
        MoveResult result = gameLogic.reserveTopDeckCard(tier);
        finishReserveAction(result);
    }
    
    private void handleNobleClick(int index) {
        if (gameLogic == null) {
            return;
        }
    
        if (!gameLogic.isWaitingForNobleChoice()) {
            updateStatus(MoveResult.fail("No noble choice is pending."));
            return;
        }
    
        MoveResult result = gameLogic.chooseNoble(index);
        updateStatus(result);
        refreshFromGameLogic();
    }

    private void handleReservedCardClick(int reserveIndex, Stage popupStage) {
        if (gameLogic == null) {
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



    public void setGameLogic(GameLogic gameLogic) {
        this.gameLogic = gameLogic;
        refreshFromGameLogic();
    }

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

    private void refreshFromGameLogic() {
        if (gameLogic == null) {
            return;
        }

        Player currentPlayer = gameLogic.getCurrentPlayer();

        currentPlayerLabel.setText(currentPlayer.getName());
        turnLabel.setText("Turn: " + gameLogic.getTurnNumber());
        pointsLabel.setText("Points: " + currentPlayer.getPoints());
        viewReservedButton.setText("View Reserved (" + currentPlayer.totalReserves() + ")");
        viewBoughtButton.setText("View Bought (" + currentPlayer.totalDevelopmentCards() + ")");

        updateCurrentPlayerTokensBox(currentPlayer);
        updateTokenBankCounts();
        updateTokenButtonStates();
        updateActionButtonStates();
        refreshBoardFromGameLogic();
    }

    private boolean didTurnActuallyEnd(MoveResult result) {
        return "Turn ended.".equals(result.getMessage())
            || "Player reached winning condition.".equals(result.getMessage());
    }
    
/* ------------------------------------ Update Parts of UI Helper methods ------------------------------------ */


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

    private HBox createTokenBonusRow(String colorName, int tokenCount, int bonusCount) {
        Label leftLabel = new Label(colorName + ": " + tokenCount);
        leftLabel.setStyle("-fx-text-fill: white;");

        Label rightLabel = new Label("(+" + bonusCount + ")");
        rightLabel.setStyle("-fx-text-fill: #cbd5e1; -fx-font-weight: bold;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        HBox row = new HBox(8);
        row.getChildren().addAll(leftLabel, spacer, rightLabel);

        return row;
    }

    private void updateStatus(MoveResult result) {
        statusBarLabel.setText(result.getMessage());
        if (result.isSuccess()) {
            statusIcon.setText("✓");
            statusBar.setStyle(
                "-fx-background-color: rgba(75,85,99,0.92);" +
                "-fx-background-radius: 14 14 0 0;" +
                "-fx-padding: 0 18 0 18;" +
                "-fx-border-color: rgba(255,255,255,0.12);" +
                "-fx-border-width: 1 0 0 0;"
            );
        } else {
            statusIcon.setText("✕");
            statusBar.setStyle(
                "-fx-background-color: rgba(199, 72, 72, 0.92);" +
                "-fx-background-radius: 14 14 0 0;" +
                "-fx-padding: 0 18 0 18;" +
                "-fx-border-color: rgba(255,255,255,0.12);" +
                "-fx-border-width: 1 0 0 0;"
            );            
        }

        statusIcon.setStyle(
            "-fx-background-color: rgba(255,255,255,0.22);" +
            "-fx-background-radius: 999;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 15px;" +
            "-fx-font-weight: bold;"
        );
    }

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

    private void updateActionButtonStates() {
        String enabledStyle = "";
        String lockedStyle =
            "-fx-opacity: 0.55;" +
            "-fx-background-color: #999999;" +
            "-fx-text-fill: #dddddd;";
    
        takeThreeTokensButton.setStyle(turnActionCommitted ? lockedStyle : enabledStyle);
        takeTwoTokensButton.setStyle(turnActionCommitted ? lockedStyle : enabledStyle);
        buyCardButton.setStyle(turnActionCommitted ? lockedStyle : enabledStyle);
        reserveCardButton.setStyle(turnActionCommitted ? lockedStyle : enabledStyle);
    }

    private void updateTokenButtonStates() {
        if (gameLogic == null) {
            return;
        }

        TokenBank bank = gameLogic.getTokenBank();

        greenBtn.setDisable(bank.get(TokenBank.GREEN) == 0);
        whiteBtn.setDisable(bank.get(TokenBank.WHITE) == 0);
        blackBtn.setDisable(bank.get(TokenBank.BLACK) == 0);
        redBtn.setDisable(bank.get(TokenBank.RED) == 0);
        blueBtn.setDisable(bank.get(TokenBank.BLUE) == 0);

        goldBtn.setDisable(bank.get(TokenBank.GOLD) == 0);
    }

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


/* ------------------------------------ Setting Up Cloud Background ------------------------------------ */

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


/* ------------------------------------ On Token Clicks ------------------------------------ */
    @FXML
    private void onGoldTokenClick() {
        handleGoldTokenClick();
    }

    @FXML
    private void onGreenTokenClick() {
        handleTokenClick(TokenBank.GREEN);
    }

    @FXML
    private void onWhiteTokenClick() {
        handleTokenClick(TokenBank.WHITE);
    }

    @FXML
    private void onBlackTokenClick() {
        handleTokenClick(TokenBank.BLACK);  
    }

    @FXML
    private void onRedTokenClick() {
        handleTokenClick(TokenBank.RED);
    }

    @FXML
    private void onBlueTokenClick() {
        handleTokenClick(TokenBank.BLUE);
    }


/* ------------------------------------ Unsorted LMFAO ------------------------------------ */

    @FXML
    private void handleViewReserved() {
        if (gameLogic == null) {
            return;
        }

        Player currentPlayer = gameLogic.getCurrentPlayer();
        showCardsPopup("Reserved Cards", currentPlayer.getReservedCards(), true);
    }

    @FXML
    private void handleViewBought() {
        if (gameLogic == null) {
            return;
        }

        Player currentPlayer = gameLogic.getCurrentPlayer();
        showCardsPopup("Bought Cards", currentPlayer.getBoughtCards(), false);
    }


    private void handleTokenClick(String color) {
        if (turnActionCommitted) {
            updateStatus(MoveResult.fail("You already used your turn action. Press End Turn."));
            return;
        }

        if (gameLogic == null) {
            return;
        }

        if (currentMode != ActionMode.TAKE_TOKENS || tokenActionMode == TokenActionMode.NONE) {
            updateStatus(MoveResult.fail("Choose Take 3 Tokens or Take 2 Same Tokens first."));
            return;
        }

        switch (tokenActionMode) {
            case TAKE_THREE -> handleTakeThreeSelection(color);
            case TAKE_TWO_SAME -> handleTakeTwoSameSelection(color);
            default -> {}
        }
    }


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

    private void handleTakeTwoSameSelection(String color) {
        MoveResult result = gameLogic.takeTwoTokens(color);
        finishTokenAction(result);
    }

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

        updateStatus(MoveResult.success(result.getMessage() + " Press End Turn when ready."));
    }

    private void clearSelectedTokenColors() {
        selectedTokenColors.clear();
    }

    private void resetTokenActionMode() {
        tokenActionMode = TokenActionMode.NONE;
        selectedTokenColors.clear();
    }

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

    private void handleGoldTokenClick() {
        if (gameLogic == null) {
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

        updateStatus(MoveResult.success(result.getMessage() + " Press End Turn when ready."));
    }

    private boolean isTurnLockedForNewAction() {
        if (turnActionCommitted) {
            updateStatus(MoveResult.fail("You already used your turn action. Press End Turn."));
            return true;
        }
        return false;
    }

}
