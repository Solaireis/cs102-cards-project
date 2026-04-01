package Player;

import java.util.ArrayList;

import Cards.Noble.Noble;
import Cards.Noble.NobleFaceUP;
import Cards.Token.TokenBank;

public class NobleService {

    public ArrayList<Noble> getEligibleNobles(Player player, NobleFaceUP nobleFaceUp) {
        ArrayList<Noble> eligible = new ArrayList<>();

        for (Noble noble : nobleFaceUp.getFaceUp()) {
            if (qualifies(player, noble)) {
                eligible.add(noble);
            }
        }

        return eligible;
    }

    public Noble awardChosenNoble(Player player, NobleFaceUP nobleFaceUp, Noble chosen) {
        if (chosen == null) {
            return null;
        }

        player.addNobles(chosen);
        nobleFaceUp.remove(chosen);
        return chosen;
    }

    private boolean qualifies(Player player, Noble noble) {
        return player.getBonus(TokenBank.WHITE) >= noble.getCost(TokenBank.WHITE)
            && player.getBonus(TokenBank.BLUE)  >= noble.getCost(TokenBank.BLUE)
            && player.getBonus(TokenBank.GREEN) >= noble.getCost(TokenBank.GREEN)
            && player.getBonus(TokenBank.RED)   >= noble.getCost(TokenBank.RED)
            && player.getBonus(TokenBank.BLACK) >= noble.getCost(TokenBank.BLACK);
    }
}