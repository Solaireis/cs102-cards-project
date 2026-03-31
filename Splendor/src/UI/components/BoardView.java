package UI.components;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import Cards.DevelopmentCard.DevelopmentCard;
import Cards.Noble.Noble;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class BoardView extends VBox {

    private final HBox nobleRow = new HBox(15);
    private final HBox tier3Row = new HBox(15);
    private final HBox tier2Row = new HBox(15);
    private final HBox tier1Row = new HBox(15);

    private BiConsumer<Integer, Integer> onFaceUpCardClick;
    private Consumer<Integer> onTopDeckClick;
    private Consumer<Integer> onNobleClick;

    private CardView selectedCard;

    public BoardView() {
        setSpacing(25);
        setAlignment(Pos.CENTER);

        nobleRow.setAlignment(Pos.CENTER);
        tier3Row.setAlignment(Pos.CENTER);
        tier2Row.setAlignment(Pos.CENTER);
        tier1Row.setAlignment(Pos.CENTER);

        getChildren().addAll(nobleRow, tier3Row, tier2Row, tier1Row);
    }

    public void setOnFaceUpCardClick(BiConsumer<Integer, Integer> onFaceUpCardClick) {
        this.onFaceUpCardClick = onFaceUpCardClick;
    }

    public void setOnTopDeckClick(Consumer<Integer> onTopDeckClick) {
        this.onTopDeckClick = onTopDeckClick;
    }

    public void setOnNobleClick(Consumer<Integer> onNobleClick) {
        this.onNobleClick = onNobleClick;
    }

    private void selectCard(CardView cardView) {
        if (selectedCard != null) {
            selectedCard.setSelected(false);
        }
        selectedCard = cardView;
        selectedCard.setSelected(true);
    }

    public void clearSelection() {
        if (selectedCard != null) {
            selectedCard.setSelected(false);
            selectedCard = null;
        }
    }

    public void loadNobles(List<Noble> nobles) {
        nobleRow.getChildren().clear();

        for (int i = 0; i < nobles.size(); i++) {
            Noble noble = nobles.get(i);
            String cardId = noble.getID();
            String imagePath = "/UI/images/cards/nobleCards/" + cardId + ".png";

            CardView cardView = new CardView(cardId, imagePath, 120, 120);
            final int nobleIndex = i;

            cardView.setOnMouseClicked(e -> {
                selectCard(cardView);
                if (onNobleClick != null) {
                    onNobleClick.accept(nobleIndex);
                }
            });

            nobleRow.getChildren().add(cardView);
        }
    }

    public void loadTier1(List<DevelopmentCard> cards) {
        loadTierRow(tier1Row, cards, 1, "/UI/images/cards/devCards/tier1/", "/UI/images/cards/devCards/tier1/tier1Back.png");
    }

    public void loadTier2(List<DevelopmentCard> cards) {
        loadTierRow(tier2Row, cards, 2, "/UI/images/cards/devCards/tier2/", "/UI/images/cards/devCards/tier2/tier2Back.png");
    }

    public void loadTier3(List<DevelopmentCard> cards) {
        loadTierRow(tier3Row, cards, 3, "/UI/images/cards/devCards/tier3/", "/UI/images/cards/devCards/tier3/tier3Back.png");
    }

    private void loadTierRow(HBox row, List<DevelopmentCard> cards, int tier, String folder, String backPath) {
        row.getChildren().clear();

        CardView deckBack = new CardView("tier" + tier + "_deck", backPath, 140, 196);
        deckBack.setOnMouseClicked(e -> {
            selectCard(deckBack);
            if (onTopDeckClick != null) {
                onTopDeckClick.accept(tier);
            }
        });
        row.getChildren().add(deckBack);

        for (int i = 0; i < cards.size(); i++) {
            DevelopmentCard card = cards.get(i);
            String cardId = card.getID();
            String imagePath = folder + cardId + ".png";

            CardView cardView = new CardView(cardId, imagePath, 140, 196);
            final int cardIndex = i;

            cardView.setOnMouseClicked(e -> {
                selectCard(cardView);
                if (onFaceUpCardClick != null) {
                    onFaceUpCardClick.accept(tier, cardIndex);
                }
            });

            row.getChildren().add(cardView);
        }
    }
}