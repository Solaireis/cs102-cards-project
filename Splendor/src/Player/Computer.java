package Player;

import java.util.*;

import Cards.DevelopmentCard.*;
import Cards.Token.*;
import Cards.Noble.*;

public class Computer extends Player {
    List<String> randomizedTokenColors = Arrays.asList(Test.Game.TAKE_COLORS);

    public Computer() {
        super("Computer");
    }

    // returns end value
    public boolean turnAlgorithm(TokenBank tb, DevelopmentCardFaceUP developmentFaceUp, DevelopmentCardDeck developmentDesk, int winningCondition, NobleDeck nobleDeck) {
        // current algorithm:
        // 1. looks for development cards it can buy
        // 2. if cannot buy development cards, take three random tokens
        // 3. if cannot take three tokens, take two tokens
        // 4. if cannot take two tokens, reserve

        boolean valid = false;

        // step 1
        if (!valid) {
            valid = computerBuyCard(tb, developmentFaceUp, developmentDesk);
        }

        // step 2
        if (!valid) {
            valid = computerTakeThreeTokens(tb);
        }

        // step 3
        if (!valid) {
            valid = computerTakeTwoTokens(tb);
        }

        // step 4
        if (!valid) {
            valid = reserveCard(tb, developmentFaceUp, developmentDesk);
        }

        // end turn
        boolean end = computerEndTurn(tb, winningCondition);

        // award noble if any
        computerAwardNobleIfAny(nobleDeck);
        
        return end;
    }

    //-----------------------------------------------------------------------------------------------------------------

    private boolean computerBuyCard(TokenBank tb, DevelopmentCardFaceUP developmentFaceUp, DevelopmentCardDeck developmentDesk) {
        DevelopmentCard currCard = null;

        // from reserve
        for (int index = 0; index < this.totalReserves(); index++) {
            try {
                currCard = this.getReserveCard(index);
                if (PurchaseService.canBuy(this, currCard)) {
                    this.buyReserve(currCard);
                    System.out.println("Bought card: " + currCard);
                    return true;
                }
            } catch (Exception e) {
            }
        }

        // from market
        for (int level = 3; level >= 1; level--) {
            for (int index = 0; index <= 3; index++) {
                try {
                    currCard = developmentFaceUp.getCard(level, index);
                    if (PurchaseService.canBuy(this, currCard)) {
                        PurchaseService.buy(this, currCard, tb);
                        developmentFaceUp.removeAndRefill(level, index, developmentDesk);
                        System.out.println("Bought card: " + currCard);
                        return true;
                    }
                } catch (Exception e) {
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

    private boolean computerTakeTwoTokens(TokenBank tb) {
        if (this.totalTokens() + 2 <= 10) {
            Collections.shuffle(randomizedTokenColors);
            String color = null;
            int randomizedTokenColorsIndex = 0;
            while (randomizedTokenColorsIndex < randomizedTokenColors.size()) {
                String currColor = randomizedTokenColors.get(randomizedTokenColorsIndex);
                if (tb.hasEnough(currColor, 4)) {
                    color = currColor;
                    break;
                }
                randomizedTokenColorsIndex++;
            }
            
            if (color != null) {
                System.out.print("Computer has taken 2 " + color + " tokens.");
                tb.remove(color, 2);
                this.addTokens(color, 2);
                return true;
            }
        }
        return false;
    }

    private boolean reserveCard(TokenBank tb, DevelopmentCardFaceUP developmentFaceUp, DevelopmentCardDeck developmentDesk) {
        if (this.totalReserves() == 3) {
            return false;
        }

        DevelopmentCard chosen = null;
        // try reserving index 0 of each level
        for (int level = 1; level <= 3; level++) {
            try {
                chosen = developmentFaceUp.getCard(level, 0);
                developmentFaceUp.removeAndRefill(level, 0, developmentDesk);
                break;
            } catch (Exception e) {
            }
        }

        if (chosen == null) {
            return false;
        }

        this.addReserve(chosen);
        if (tb.get("GOLD") > 0) {
            tb.remove("GOLD", 1);
            this.addTokens("GOLD", 1);
            System.out.println("Gold token added to inventory");
        } else {
            System.out.println("No remaining gold tokens");
        }

        System.out.println("Card" + chosen + "reserved by computer.");
        return true;
    }

    //-----------------------------------------------------------------------------------------------------------------

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

    private void computerAwardNobleIfAny(NobleDeck deck) {
        ArrayList<Noble> eligible = deck.getAttractableNobles(this);

        if (eligible.isEmpty()) {
            return;
        }

        Noble chosen = eligible.get(0);     // gets first eligible noble no matter what

        this.addNobles(chosen);
        deck.removeNoble(chosen);

        System.out.println("Noble gained: " + chosen);
    }
}
