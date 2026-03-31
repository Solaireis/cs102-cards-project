package UI.components;

import java.util.List;

import Cards.DevelopmentCard.DevelopmentCard;
import Cards.Noble.Noble;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class BoardView extends VBox {

    private final HBox nobleRow = new HBox(15);
    private final HBox tier3Row = new HBox(15);
    private final HBox tier2Row = new HBox(15);
    private final HBox tier1Row = new HBox(15);

    public BoardView() {
        setSpacing(25);
        setAlignment(Pos.CENTER);

        nobleRow.setAlignment(Pos.CENTER);
        tier3Row.setAlignment(Pos.CENTER);
        tier2Row.setAlignment(Pos.CENTER);
        tier1Row.setAlignment(Pos.CENTER);

        getChildren().addAll(nobleRow, tier3Row, tier2Row, tier1Row);
    }

    public HBox getNobleRow() {
        return nobleRow;
    }

    public HBox getTier3Row() {
        return tier3Row;
    }

    public HBox getTier2Row() {
        return tier2Row;
    }

    public HBox getTier1Row() {
        return tier1Row;
    }

    public void loadNobles(List<Noble> nobles) {
        nobleRow.getChildren().clear();

        for (Noble noble : nobles) {
            String cardId = noble.getID();
            String imagePath = "/UI/images/cards/nobleCards/" + cardId + ".png";
            nobleRow.getChildren().add(new CardView(cardId, imagePath, 120, 120));
        }
    }

    public void loadTier1(List<DevelopmentCard> cards) {
        tier1Row.getChildren().clear();

        tier1Row.getChildren().add(
            new CardView("tier1_deck", "/UI/images/cards/devCards/tier1/tier1Back.png", 140, 196)
        );

        for (DevelopmentCard card : cards) {
            String cardId = card.getID();
            String imagePath = "/UI/images/cards/devCards/tier1/" + cardId + ".png";

            tier1Row.getChildren().add(new CardView(cardId, imagePath, 140, 196));
        }
    }

    public void loadTier2(List<DevelopmentCard> cards) {
        tier2Row.getChildren().clear();

        tier2Row.getChildren().add(
            new CardView("tier2_deck", "/UI/images/cards/devCards/tier2/tier2Back.png", 140, 196)
        );

        for (DevelopmentCard card : cards) {
            String cardId = card.getID();
            String imagePath = "/UI/images/cards/devCards/tier2/" + cardId + ".png";

            tier2Row.getChildren().add(new CardView(cardId, imagePath, 140, 196));
        }
    }

    public void loadTier3(List<DevelopmentCard> cards) {
        tier3Row.getChildren().clear();

        tier3Row.getChildren().add(
            new CardView("tier3_deck", "/UI/images/cards/devCards/tier3/tier3Back.png", 140, 196)
        );

        for (DevelopmentCard card : cards) {
            String cardId = card.getID();
            String imagePath = "/UI/images/cards/devCards/tier3/" + cardId + ".png";

            tier3Row.getChildren().add(new CardView(cardId, imagePath, 140, 196));
        }
    }





}