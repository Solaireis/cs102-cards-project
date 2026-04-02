package UI.components;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import Cards.DevelopmentCard.DevelopmentCard;
import Cards.Noble.Noble;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Displays the main board area of the game.
 * This includes the face-up nobles and the three tiers of development cards,
 * and forwards card clicks to handlers set by the controller.
 */
public class BoardView extends VBox {

    private final HBox nobleRow = new HBox(15);
    private final HBox tier3Row = new HBox(15);
    private final HBox tier2Row = new HBox(15);
    private final HBox tier1Row = new HBox(15);

    private BiConsumer<Integer, Integer> onFaceUpCardClick;
    private Consumer<Integer> onTopDeckClick;
    private Consumer<Integer> onNobleClick;

    /**
     * Creates the board view and sets up the layout for noble and card rows.
     */
    public BoardView() {
        setSpacing(25);
        setAlignment(Pos.CENTER);

        nobleRow.setAlignment(Pos.CENTER);
        tier3Row.setAlignment(Pos.CENTER);
        tier2Row.setAlignment(Pos.CENTER);
        tier1Row.setAlignment(Pos.CENTER);

        getChildren().addAll(nobleRow, tier3Row, tier2Row, tier1Row);
    }

    /**
     * Sets the handler for clicks on face-up development cards.
     *
     * @param onFaceUpCardClick the handler that receives the tier and card index
     */
    public void setOnFaceUpCardClick(BiConsumer<Integer, Integer> onFaceUpCardClick) {
        this.onFaceUpCardClick = onFaceUpCardClick;
    }

    /**
     * Sets the handler for clicks on the top deck of a card tier.
     *
     * @param onTopDeckClick the handler that receives the tier number
     */
    public void setOnTopDeckClick(Consumer<Integer> onTopDeckClick) {
        this.onTopDeckClick = onTopDeckClick;
    }

    /**
     * Sets the handler for clicks on face-up nobles.
     *
     * @param onNobleClick the handler that receives the noble index
     */
    public void setOnNobleClick(Consumer<Integer> onNobleClick) {
        this.onNobleClick = onNobleClick;
    }

    /**
     * Clears any current visual selection on the board.
     * This method is currently unused because selection animation was removed.
     */
    public void clearSelection() {
        // Selected border animation removed.
    }

    /**
     * Loads and displays the current face-up nobles.
     *
     * @param nobles the nobles to display
     */
    public void loadNobles(List<Noble> nobles) {
        nobleRow.getChildren().clear();

        for (int i = 0; i < nobles.size(); i++) {
            Noble noble = nobles.get(i);
            String cardId = noble.getID();
            String imagePath = "/UI/images/cards/nobleCards/" + cardId + ".png";

            CardView cardView = new CardView(cardId, imagePath, 120, 120);
            final int nobleIndex = i;

            cardView.setOnMouseClicked(e -> {
                if (onNobleClick != null) {
                    onNobleClick.accept(nobleIndex);
                }
            });

            nobleRow.getChildren().add(cardView);
        }
    }

    /**
     * Loads and displays the face-up cards for tier 1.
     *
     * @param cards the tier 1 cards to display
     */
    public void loadTier1(List<DevelopmentCard> cards) {
        loadTierRow(tier1Row, cards, 1, "/UI/images/cards/devCards/tier1/", "/UI/images/cards/devCards/tier1/tier1Back.png");
    }

    /**
     * Loads and displays the face-up cards for tier 2.
     *
     * @param cards the tier 2 cards to display
     */
    public void loadTier2(List<DevelopmentCard> cards) {
        loadTierRow(tier2Row, cards, 2, "/UI/images/cards/devCards/tier2/", "/UI/images/cards/devCards/tier2/tier2Back.png");
    }

    /**
     * Loads and displays the face-up cards for tier 3.
     *
     * @param cards the tier 3 cards to display
     */
    public void loadTier3(List<DevelopmentCard> cards) {
        loadTierRow(tier3Row, cards, 3, "/UI/images/cards/devCards/tier3/", "/UI/images/cards/devCards/tier3/tier3Back.png");
    }

    /**
     * Loads one full development card row.
     * This includes the top deck card back and the face-up cards in that tier.
     *
     * @param row the UI row to load into
     * @param cards the face-up cards to display
     * @param tier the tier number
     * @param folder the folder containing the card face images
     * @param backPath the image path for the deck back
     */
    private void loadTierRow(HBox row, List<DevelopmentCard> cards, int tier, String folder, String backPath) {
        row.getChildren().clear();

        // Add the clickable deck back for reserving from the top of the deck.
        CardView deckBack = new CardView("tier" + tier + "_deck", backPath, 140, 196);
        deckBack.setOnMouseClicked(e -> {
            if (onTopDeckClick != null) {
                onTopDeckClick.accept(tier);
            }
        });
        row.getChildren().add(deckBack);

        // Add the current face-up cards for this tier.
        for (int i = 0; i < cards.size(); i++) {
            DevelopmentCard card = cards.get(i);
            String cardId = card.getID();
            String imagePath = folder + cardId + ".png";

            CardView cardView = new CardView(cardId, imagePath, 140, 196);
            final int cardIndex = i;

            cardView.setOnMouseClicked(e -> {
                if (onFaceUpCardClick != null) {
                    onFaceUpCardClick.accept(tier, cardIndex);
                }
            });

            row.getChildren().add(cardView);
        }
    }
}