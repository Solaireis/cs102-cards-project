/**
 * This player class store the information of a single player object
 * A player has points, bonus owned, tokens owned, developmentCard owned, developmentCard reversed, noble owned
*/
package Player;

import Cards.DevelopmentCard.DevelopmentCard;
import Cards.Noble.Noble;
import Cards.Token.TokenBank;
import java.util.ArrayList;
import java.util.HashMap;

public class Player {

    // HashMap to store the tokens and Bonuses, basically they are the same object but token is temporary while bonus is permanent
    // So use two differet list to store them.

    // Why using HashMap for Tokens but ArrayList for Cards is because, For tokens, each tokens have a name and value,
    // the name is a constant but value is a varible, so that variable is mapped to a constant, so whenever i want to change
    // or search for that value i can just search for the constant name, then the system will tells me the value it corresponding to.
    // However, the development card has no variables which needs to be changed manually, so there is not need to search for a 
    // specific card. System can just randomly draw cards from the deck and place it on the table and for player to buy it.
    private HashMap<String, Integer> playerTokens = new HashMap<>();
    private HashMap<String, Integer> playerBonuses = new HashMap<>();
    private ArrayList<DevelopmentCard> playerDevelopmentCards = new ArrayList<>();
    private ArrayList<Noble> playerNobles = new ArrayList<>();
    private ArrayList<DevelopmentCard> playerReserves = new ArrayList<>();
    private int playerPoints = 0;
    private String name;

    /**
     * Initialize the tokens, bonuses and name of player
     * @param name Name of player
     */
    public Player(String name){
        // start at 0 tokens
        playerTokens.put(TokenBank.WHITE, 0);
        playerTokens.put(TokenBank.BLUE, 0);
        playerTokens.put(TokenBank.GREEN, 0);
        playerTokens.put(TokenBank.RED, 0);
        playerTokens.put(TokenBank.BLACK, 0);
        playerTokens.put(TokenBank.GOLD, 0);

        // start at 0 bonuses
        playerBonuses.put(TokenBank.WHITE, 0);
        playerBonuses.put(TokenBank.BLUE, 0);
        playerBonuses.put(TokenBank.GREEN, 0);
        playerBonuses.put(TokenBank.RED, 0);
        playerBonuses.put(TokenBank.BLACK, 0); 

        this.name = name;
    }
    
    /**
     * Returns number of tokens of a specific color the player owns
     * @param color color of token
     * @return number of tokens of a specific color the player owns
     */
    public int getTokens(String color){
        return playerTokens.get(color);
    }

    /**
     * Returns number of bonuses of a specific color the player has
     * @param color color of bonus
     * @return number of bonuses of the specific color
     */
    public int getBonus(String color){
        return playerBonuses.get(color);
    }

    /**
     * Returns number of prestige points the player has
     * @return number of prestige points the player has
     */
    public int getPoints(){
        return playerPoints;
    }

    /**
     * Returns name of player
     * @return name of player
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the total number of tokens owned by player
     * @return total number of tokens owned by player
     */
    public int totalTokens(){
        int sum = 0;
        for(int t : playerTokens.values()){
            sum += t;
        }

        return sum;
    }

    public void addTokens(String color, int amount) {
        playerTokens.put(color, playerTokens.get(color) + amount);
    }

    public void removeTokens(String color, int amount) {
        int t = playerTokens.get(color);

        if (t < amount){
            throw new IllegalArgumentException("Player does not have enough " + color + " tokens");
        }

        playerTokens.put(color, t - amount);
    }

    public void addDevelopmentCard(DevelopmentCard card) {
        playerDevelopmentCards.add(card);
        playerPoints += card.getPoints();

        // card color becomes permanent bonus (discount) for that color
        String bonusColor = card.getBonus();
        playerBonuses.put(bonusColor, playerBonuses.get(bonusColor) + 1);
    }

    public int totalDevelopmentCards() {
        return playerDevelopmentCards.size();
    }

    public void addNobles(Noble noble){
        playerNobles.add(noble);
        playerPoints += noble.getPoints();
    }

    public ArrayList<Noble> getPlayerNobles(){
        return new ArrayList<>(playerNobles);
    }

    public int totalNobles() {
        return playerNobles.size();
    }

    public DevelopmentCard getReserveCard(int index) {
        return playerReserves.get(index);
    }

    public void addReserve(DevelopmentCard card) {
        playerReserves.add(card);
    }

    public void buyReserve(DevelopmentCard card) {
        playerReserves.remove(card);
    }

    public int totalReserves() {
        return playerReserves.size();
    }

    public void printStatus() {
        System.out.println("Player points = " + playerPoints + " | tokens = " + playerTokens 
        + " | bonuses = " + playerBonuses + " | reserves = " + playerReserves + " | nobles = " + playerNobles + " | totalTokens = " + totalTokens() + " | totalReserves = " + totalReserves());
    }

}