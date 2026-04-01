package Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import Cards.DevelopmentCard.DevelopmentCard;
import Cards.Token.TokenBank;
import Test.GameLogic;
import Test.MoveResult;

public class ComputerService {

    private static final List<String> TAKE_COLORS = new ArrayList<>(
        Arrays.asList(TokenBank.WHITE, TokenBank.BLUE, TokenBank.GREEN, TokenBank.RED, TokenBank.BLACK)
    );

    public static MoveResult performMainAction(GameLogic gameLogic) {
        if (!(gameLogic.getCurrentPlayer() instanceof Computer)) {
            return MoveResult.fail("Current player is not a computer.");
        }

        Player current = gameLogic.getCurrentPlayer();

        // 1. Buy reserved card if possible
        for (int i = 0; i < current.totalReserves(); i++) {
            DevelopmentCard card = current.getReserveCard(i);
            if (PurchaseService.canBuy(current, card)) {
                return gameLogic.buyReservedCard(i);
            }
        }

        // 2. Buy market card if possible
        for (int level = 3; level >= 1; level--) {
            List<DevelopmentCard> row = gameLogic.getDevelopmentFaceUp().getFaceUp(level);
            for (int i = 0; i < row.size(); i++) {
                DevelopmentCard card = gameLogic.getDevelopmentFaceUp().getCard(level, i);
                if (PurchaseService.canBuy(current, card)) {
                    return gameLogic.buyMarketCard(level, i);
                }
            }
        }

        // 3. Take 3 different tokens if possible
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

        // 4. Take 2 same if possible
        Collections.shuffle(colors);
        for (String color : colors) {
            if (gameLogic.getTokenBank().hasEnough(color, 4)) {
                return gameLogic.takeTwoTokens(color);
            }
        }

        // 5. Reserve first available face-up card
        if (current.totalReserves() < 3) {
            for (int level = 1; level <= 3; level++) {
                List<DevelopmentCard> row = gameLogic.getDevelopmentFaceUp().getFaceUp(level);
                if (!row.isEmpty()) {
                    return gameLogic.reserveFaceUpCard(level, 0);
                }
            }
        }

        return MoveResult.success("Computer took no action.");
    }


}
