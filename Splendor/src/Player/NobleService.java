package Player;

import Cards.Noble.Noble;
import Cards.Noble.NobleFaceUP;
import Cards.Token.TokenBank;
import java.util.ArrayList;

/**
 * The NobleService class checks and awards eligible nobles to players
 */

public class NobleService {

    /**
     * Returns an arrayList of eligible nobles player qualified for
     * @param player Player to check
     * @param nobleFaceUp Deck of face up noble cards
     * @return arrayList of eligible nobles
     */
    public ArrayList<Noble> getEligibleNobles(Player player, NobleFaceUP nobleFaceUp) {
        ArrayList<Noble> eligible = new ArrayList<>();

        for (Noble noble : nobleFaceUp.getFaceUp()) {
            if (qualifies(player, noble)) {
                eligible.add(noble);
            }
        }

        return eligible;
    }

    /**
     * Returns the Noble Object chosen by player
     * @param player Player
     * @param nobleFaceUp Deck of face up noble cards
     * @param chosen Chosen noble card
     * @return Noble Object chosen by player
     */
    public Noble awardChosenNoble(Player player, NobleFaceUP nobleFaceUp, Noble chosen) {
        if (chosen == null) {
            return null;
        }

        player.addNobles(chosen);
        nobleFaceUp.remove(chosen);
        return chosen;
    }

    /**
     * Returns true if player qualifies for visit by noble
     * @param player Player
     * @param noble Specific noble object
     * @return true if player qualifies, false otherwise
     */
    private boolean qualifies(Player player, Noble noble) {
        return player.getBonus(TokenBank.WHITE) >= noble.getCost(TokenBank.WHITE)
            && player.getBonus(TokenBank.BLUE)  >= noble.getCost(TokenBank.BLUE)
            && player.getBonus(TokenBank.GREEN) >= noble.getCost(TokenBank.GREEN)
            && player.getBonus(TokenBank.RED)   >= noble.getCost(TokenBank.RED)
            && player.getBonus(TokenBank.BLACK) >= noble.getCost(TokenBank.BLACK);
    }
}