package Player;

import Cards.DevelopmentCard.*;
import Cards.Noble.*;
import Cards.Token.*;
import java.util.*;

/**
 * The Computer class contains the logic of the computer player
 */

public class Computer extends Player {
    List<String> randomizedTokenColors = Arrays.asList(Test.Game.TAKE_COLORS);

    public Computer() {
        super("Computer");
    }

    /**
     * Executes the Computer's turn by doing these steps:
     * <pre>
     * 1. Looks for development cards it can buy
     * 1a. Looks through reserve
     * 1b. Looks through market
     * 2. If cannot buy development cards, takes three random tokens
     * 3. If cannot take three tokens, takes two tokens
     * 4. If cannot take two tokens, reserve a development card
     * 5. Ends the turn
     * 6. If is eligible for a noble, takes a noble
     * </pre>
     * 
     * @param tb                the token bank
     * @param developmentFaceUp development cards currently face up on the table
     * @param developmentDesk   the current deck of development cards
     * @param winningCondition  amount of points needed to win
     * @param nobleFaceUp         nobles currently on the table
     * @return a Boolean value indicating whether Computer has reached the winning
     *         condition or not
     */
    public boolean turnAlgorithm(TokenBank tb, DevelopmentCardFaceUP developmentFaceUp,
            DevelopmentCardDeck developmentDesk, int winningCondition, NobleFaceUP nobleFaceUp) {

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
            valid = computerReserveCard(tb, developmentFaceUp, developmentDesk);
        }

        // The correct order should be check if the computer can attract nobles before
        // enter into final round
        // even though the computer may have prestigious point >= 15 but we still need
        // to check all the possible
        // game action before enter into the final round.

        // end turn discard cleanup
        computerEndTurn(tb);

        // award noble if any before winning check
        computerAwardNobleIfAny(nobleFaceUp);

        if (this.getPoints() >= winningCondition) {
            System.out.println("Computer reached winning condition, final round");
            return true;
        }

        return false;
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Test.Game::buyCard, but adjusted for the Computer class.
     * Iterates through each card in its reserve then in the market to find one it
     * can afford.
     * 
     * @param tb                the token bank
     * @param developmentFaceUp development cards currently face up on the table
     * @param developmentDesk   the current deck of development cards
     * @return a Boolean value indicating whether Computer has successfully bought a
     *         card or not
     */
    private boolean computerBuyCard(TokenBank tb, DevelopmentCardFaceUP developmentFaceUp, DevelopmentCardDeck developmentDesk) {
        // from reserve
        for (int index = 0; index < this.totalReserves(); index++) {
            DevelopmentCard currCard = this.getReserveCard(index);
            if (PurchaseService.canBuy(this, currCard)) {
                PurchaseService.buy(this, currCard, tb);
                this.buyReserve(currCard);
                System.out.println("Bought card: " + currCard);
                return true;
            }
        }

        // from market
        for (int level = 3; level >= 1; level--){
            ArrayList<DevelopmentCard> row = developmentFaceUp.getFaceUp(level);
            for (int index = 0; index < row.size(); index++) {
                DevelopmentCard currCard = developmentFaceUp.getCard(level, index);
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

    /**
     * Test.Game::takeThreeTokens, but adjusted for the Computer class.
     * Takes three random tokens.
     * 
     * @param tb the token bank
     * @return a Boolean value indicating whether Computer has successfully taken
     *         three tokens or not
     */
    private boolean computerTakeThreeTokens(TokenBank tb) {
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
        // Computer are allowed to take 3 different color tokens
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
        return false;
    }

    /**
     * Test.Game::takeTwoTokens, but adjusted for the Computer class.
     * Takes two tokens of a random color.
     * 
     * @param tb the token bank
     * @return a Boolean value indicating whether Computer has successfully taken
     *         two tokens or not
     */
    private boolean computerTakeTwoTokens(TokenBank tb){
        Collections.shuffle(randomizedTokenColors);
        String color = null;
        int randomizedTokenColorsIndex = 0;
        while (randomizedTokenColorsIndex < randomizedTokenColors.size()){
            String currColor = randomizedTokenColors.get(randomizedTokenColorsIndex);
            if (tb.hasEnough(currColor, 4)){
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
        return false;
    }

    /**
     * Test.Game::reserveCard, but adjusted for the Computer class.
     * Tries to reserve the card in market level 1 index 0, then market level 2
     * index 0, then market level 3 index 0.
     * 
     * @param tb                the token bank
     * @param developmentFaceUp development cards currently face up on the table
     * @param developmentDeck   the current deck of development cards
     * @return a Boolean value indicating whether Computer has successfully reserved
     *         a card or not
     */
    private boolean computerReserveCard(TokenBank tb, DevelopmentCardFaceUP developmentFaceUp, DevelopmentCardDeck developmentDeck) {
        if (this.totalReserves() == 3) {
            return false;
        }

        DevelopmentCard chosen = null;
        // try reserving index 0 of each level
        for (int level = 1; level <= 3; level++) {
            if (developmentFaceUp.getFaceUp(level).isEmpty()) {
                continue;
            }

            chosen = developmentFaceUp.getCard(level, 0);
            developmentFaceUp.removeAndRefill(level, 0, developmentDeck);
            break;
        }

        if (chosen == null) {
            return false;
        }

        this.addReserve(chosen);
        if (tb.get("GOLD") > 0){
            tb.remove("GOLD", 1);
            this.addTokens("GOLD", 1);
            System.out.println("Gold token added to inventory");
        } else {
            System.out.println("No remaining gold tokens");
        }

        System.out.println("Card" + chosen + "reserved by computer.");
        return true;
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Test.Game::endTurn, but adjusted for the Computer class.
     * 
     * @param tb               the token bank
     * @param winningCondition amount of points needed to win
     * @return a Boolean value indicating whether Computer has reached the winning
     *         condition or not
     */
    private void computerEndTurn(TokenBank tb) {
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
    }

    /**
     * Test.Game::awardNobleIfAny, but adjusted for the Computer class.
     * 
     * @param deck nobles currently on the table
     */
    private void computerAwardNobleIfAny(NobleFaceUP nobleFaceUp){
        NobleService nobleService = new NobleService();
        ArrayList<Noble> eligible = nobleService.getEligibleNobles(this, nobleFaceUp);

        if (eligible.isEmpty()) {
            return;
        }

        Noble chosen = eligible.get(0); // gets first eligible noble no matter what

        nobleService.awardChosenNoble(this, nobleFaceUp, chosen);

        System.out.println("Noble gained: " + chosen);
    }
}
