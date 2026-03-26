package Player;

import java.util.*;

import Cards.DevelopmentCard.*;
import Cards.Token.*;

public class Computer extends Player {
    List<String> randomizedTokenColors = Arrays.asList(Test.Game.TAKE_COLORS);

    public Computer() {
        super("Computer");
    }

    // returns end value
    public boolean turnAlgorithm(TokenBank tb, DevelopmentCardFaceUP developmentFaceUp, DevelopmentCardDeck developmentDesk, int winningCondition) {
        // current algorithm:
        // 1. looks for development cards it can buy
        // 2. if there's none, take three random tokens
        // 3. to-do: if cannot take three tokens, take two tokens

        boolean valid = false;

        // step 1
        if (!valid) {
            valid = computerBuyCard(tb, developmentFaceUp, developmentDesk);
        }

        // step 2
        if (!valid) {
            valid = computerTakeThreeTokens(tb);
        }        

        // end turn
        boolean end = false;
        end = computerEndTurn(tb, winningCondition);

        // award noble if any
        
        return end;
    }

    //-----------------------------------------------------------------------------------------------------------------

    private boolean computerBuyCard(TokenBank tb, DevelopmentCardFaceUP developmentFaceUp, DevelopmentCardDeck developmentDesk) {
        DevelopmentCard currCard = null;
        for (int level = 3; level >= 1; level--) {
            for (int index = 0; index <= 3; index++) {
                currCard = developmentFaceUp.getCard(level, index);
                if (PurchaseService.canBuy(this, currCard)) {
                    PurchaseService.buy(this, currCard, tb);
                    developmentFaceUp.removeAndRefill(level, index, developmentDesk);
                    System.out.println("Bought card: " + currCard);
                    return true;
                }
            }
        }
        return false;
    }

    private boolean computerTakeThreeTokens(TokenBank tb) {
        if (this.totalTokens() + 3 <= 10) {
            Collections.shuffle(randomizedTokenColors);
            String[] colors = new String[3];
            int randomizedTokenColorsIndex = 0;
            for (int i = 0; i < 3; i++) {
                while (randomizedTokenColorsIndex < randomizedTokenColors.size()) {
                    String currColor = randomizedTokenColors.get(randomizedTokenColorsIndex);
                    if (tb.hasEnough(currColor, 1)) {
                        colors[i] = currColor;
                        break;
                    }
                    randomizedTokenColorsIndex++;
                }
                randomizedTokenColorsIndex++;
            }
            // means there's 3 colors Computer are allowed to take, therefore taking three tokens is valid
            if (colors[2] != null) {
                System.out.print("Computer has taken ");
                for (String color : colors) {
                    tb.remove(color, 1);
                    this.addTokens(color, 1);
                    System.out.print(color + " ");
                }
                System.out.print("tokens.");
                return true;
            }
        }
        return false;
    }

    private boolean computerEndTurn(TokenBank tb, int winningCondition) {
        while (this.totalTokens() > 10) {
            // remove random token
            Collections.shuffle(randomizedTokenColors);
            String color = null;
            for (int i = 0; i < randomizedTokenColors.size(); i++) {
                if (this.getTokens(randomizedTokenColors.get(i)) > 0) {
                    color = randomizedTokenColors.get(i);
                    break;
                }
            }
            this.removeTokens(color, 1);
            tb.add(color, 1);
        }
        if (this.getPoints() >= winningCondition) {
            System.out.println("Computer reached winning condition, last turn");
            return true;
        }
        return false;
    }
}
