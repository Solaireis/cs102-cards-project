// AI-assisted: Parts of this class, including the computer player's action-selection logic,
// were developed with help from ChatGPT-5, orignally referenced by Computer.java
// The team reviewed, tested, and modified the final implementation.
package Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import Cards.DevelopmentCard.DevelopmentCard;
import Cards.Token.TokenBank;
import Test.GameLogic;
import Test.MoveResult;

/**
 * Handles the computer player's automatic decision-making.
 * This class chooses and performs the computer's main action
 * based on a simple priority order.
 */
public class ComputerService {

    /**
     * Performs the computer player's main action for its turn.
     * The computer follows this priority order:
     * buy a reserved card, buy a market card, take 3 different tokens,
     * take 2 of the same token, or reserve a face-up card.
     *
     * @param gameLogic the current game logic instance
     * @return the result of the computer's chosen action
     */
    public static MoveResult performMainAction(GameLogic gameLogic) {
        if (!(gameLogic.getCurrentPlayer() instanceof Computer)) {
            return MoveResult.fail("Current player is not a computer.");
        }

        Player current = gameLogic.getCurrentPlayer();

        // 1. try to buy any reserved card the computer can afford
        for (int i = 0; i < current.totalReserves(); i++) {
            DevelopmentCard card = current.getReserveCard(i);
            if (PurchaseService.canBuy(current, card)) {
                return gameLogic.buyReservedCard(i);
            }
        }

        // If no reserved card can be bought, 2. try to buy a face-up market card.
        for (int level = 3; level >= 1; level--) {
            List<DevelopmentCard> row = gameLogic.getDevelopmentFaceUp().getFaceUp(level);
            for (int i = 0; i < row.size(); i++) {
                DevelopmentCard card = gameLogic.getDevelopmentFaceUp().getCard(level, i);
                if (PurchaseService.canBuy(current, card)) {
                    return gameLogic.buyMarketCard(level, i);
                }
            }
        }

        // If no card can be bought, 3. try to take 3 different available token colors.
        List<String> colors = new ArrayList<>(Arrays.asList(
            TokenBank.WHITE, TokenBank.BLUE, TokenBank.GREEN, TokenBank.RED, TokenBank.BLACK
        ));
        Collections.shuffle(colors);

        ArrayList<String> available = new ArrayList<>();
        for (String color : colors) {
            if (gameLogic.getTokenBank().hasEnough(color, 1)) {
                available.add(color);
            }
        }

        if (available.size() >= 3) {
            return gameLogic.takeThreeTokens(available.get(0), available.get(1), available.get(2));
        }

        // If that is not possible, 4. try taking 2 tokens of the same color.
        Collections.shuffle(colors);
        for (String color : colors) {
            if (gameLogic.getTokenBank().hasEnough(color, 4)) {
                return gameLogic.takeTwoTokens(color);
            }
        }

        // If the computer still cannot act, reserve the first available face-up card.
        if (current.totalReserves() < 3) {
            for (int level = 1; level <= 3; level++) {
                List<DevelopmentCard> row = gameLogic.getDevelopmentFaceUp().getFaceUp(level);
                if (!row.isEmpty()) {
                    return gameLogic.reserveFaceUpCard(level, 0);
                }
            }
        }

        // If no valid action is possible, do nothing.
        return MoveResult.success("Computer took no action.");
    }
}
