package Cards.Noble;

import java.util.ArrayList;

import Cards.Token.TokenBank;
import Player.Player;

public class NobleDeck {

    private final ArrayList<Noble> nobles = new ArrayList<>();

    public NobleDeck() {
        createNobles();
    }

    // Create all nobles, there are 10 nobles in total
    private void createNobles() {
    // call noble constructor: new Noble(points, blackCost, whiteCost, redCost, blueCost, greenCost)
    // Note: The Cost here is not the cost of token, it is the number of particular color development card
    //       a player must have. This is the actually the bonus a player owned.

        nobles.add(new Noble(3, 4, 4, 0, 0, 0)); // 4B + 4W
        nobles.add(new Noble(3, 0, 0, 3, 3, 3)); // 3R + 3Bl + 3G
        nobles.add(new Noble(3, 0, 4, 0, 4, 0)); // 4W + 4Bl
        nobles.add(new Noble(3, 0, 3, 0, 3, 3)); // 3W + 3Bl + 3G
        nobles.add(new Noble(3, 4, 0, 4, 0, 0)); // 4B + 4R
        nobles.add(new Noble(3, 0, 0, 4, 0, 4)); // 4R + 4G
        nobles.add(new Noble(3, 0, 0, 0, 4, 4)); // 4Bl + 4G
        nobles.add(new Noble(3, 3, 3, 3, 0, 0)); // 3B + 3W + 3R
        nobles.add(new Noble(3, 3, 3, 0, 3, 0)); // 3B + 3W + 3Bl
        nobles.add(new Noble(3, 3, 0, 3, 0, 3)); // 3B + 3R + 3G
    }

    // Getter, get the number of Noble in the desk
    public int size(){
        return nobles.size();
    }

    // Getter, get the list of Nobles in the desk
    public ArrayList<Noble> getNobles() {
        return new ArrayList<>(nobles);
    }

    // remove a noble card from desk
    public void removeNoble(Noble noble){
        nobles.remove(noble);
    }

    // check if the noble desk is empty
    public boolean isEmpty() {
        return nobles.isEmpty();
    }

    // check if noble will be attracted by the player
    // Note there is possibility that player meet the qualification for multiple nobles at the same time
    // so the player should choose one and only one noble per round from all the eligible nobles
    public ArrayList<Noble> getAttractableNobles(Player player) {
        ArrayList<Noble> eligibleNobles = new ArrayList<>();

        for (Noble noble : nobles) {
            if (player.getBonus(TokenBank.BLACK) >= noble.getCost(TokenBank.BLACK) &&
                player.getBonus(TokenBank.WHITE) >= noble.getCost(TokenBank.WHITE) &&
                player.getBonus(TokenBank.RED)   >= noble.getCost(TokenBank.RED) &&
                player.getBonus(TokenBank.BLUE)  >= noble.getCost(TokenBank.BLUE) &&
                player.getBonus(TokenBank.GREEN) >= noble.getCost(TokenBank.GREEN)){
                eligibleNobles.add(noble);
            }
        }
        return eligibleNobles;
    }

}
