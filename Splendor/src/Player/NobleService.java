// AI-assisted: Parts of this class, including noble eligibility and noble-awarding logic,
// were developed with help from ChatGPT-5, using NobleAttractService.java as an original reference.
// The team reviewed, tested, and modified the final implementation.
package Player;

import Cards.Noble.Noble;
import Cards.Noble.NobleFaceUP;
import Cards.Token.TokenBank;
import java.util.ArrayList;

/**
 * Handles checking noble eligibility and awarding nobles to players.
 */

public class NobleService {

    /**
     * Returns all face up nobles the player currently qualifies for
     * 
     * @param player Player to check
     * @param nobleFaceUp Deck of face up noble cards
     * @return a list of eligible nobles
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
     * Awards the chosen noble to the player and removes it from the face-up nobles.
     * 
     * @param player the player receiving the noble
     * @param nobleFaceUp the collection of face-up nobles
     * @param chosen the noble chosen by the player
     * @return the awarded noble, or null if no noble was chosen
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
     * Checks whether the player qualifies for the given noble.
     *
     * @param player the player to check
     * @param noble the noble being checked
     * @return true if the player has enough bonuses to attract the noble, false otherwise
     */
    private boolean qualifies(Player player, Noble noble) {
        return player.getBonus(TokenBank.WHITE) >= noble.getCost(TokenBank.WHITE)
            && player.getBonus(TokenBank.BLUE)  >= noble.getCost(TokenBank.BLUE)
            && player.getBonus(TokenBank.GREEN) >= noble.getCost(TokenBank.GREEN)
            && player.getBonus(TokenBank.RED)   >= noble.getCost(TokenBank.RED)
            && player.getBonus(TokenBank.BLACK) >= noble.getCost(TokenBank.BLACK);
    }
}