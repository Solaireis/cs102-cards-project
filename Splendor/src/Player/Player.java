
package Player;

import Cards.DevelopmentCard.DevelopmentCard;
import Cards.Noble.Noble;
import Cards.Token.TokenBank;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * The player class store the information of a single player object.
 * A player has points, bonus owned, tokens owned, developmentCard owned, developmentCard reversed, noble owned
*/
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

    /**
     * Adds tokens to player tokens based on color
     * @param color color of token
     * @param amount amount of tokens of that color taken by player
     */
    public void addTokens(String color, int amount) {
        playerTokens.put(color, playerTokens.get(color) + amount);
    }

    /**
     * Remove tokens from player tokens based on color
     * @param color color of tokens
     * @param amount amount of tokens of that color used by player 
     */
    public void removeTokens(String color, int amount) {
        int t = playerTokens.get(color);

        if (t < amount){
            throw new IllegalArgumentException("Player does not have enough " + color + " tokens");
        }

        playerTokens.put(color, t - amount);
    }

    /**
     * Add development card to player development cards and bonus
     * @param card DevelopementCard object to add
     */
    public void addDevelopmentCard(DevelopmentCard card) {
        playerDevelopmentCards.add(card);
        playerPoints += card.getPoints();

        // card color becomes permanent bonus (discount) for that color
        String bonusColor = card.getBonus();
        playerBonuses.put(bonusColor, playerBonuses.get(bonusColor) + 1);
    }

    /**
     * Returns total number of development cards owned by player (or total bonuses)
     * @return total number of development cards owned by player
     */
    public int totalDevelopmentCards() {
        return playerDevelopmentCards.size();
    }

    /**
     * Add noble object to player inventory when they qualify for a noble visit
     * @param noble Noble object the player qualified for
     */
    public void addNobles(Noble noble){
        playerNobles.add(noble);
        playerPoints += noble.getPoints();
    }

    /**
     * Returns an arrayList of noble objects owned by the player
     * @return arrayList of noble objects owned by player
     */
    public ArrayList<Noble> getPlayerNobles(){
        return new ArrayList<>(playerNobles);
    }

    /**
     * Returns the total number of nobles owned by player
     * @return total number of nobles owned by player
     */
    public int totalNobles() {
        return playerNobles.size();
    }

    /**
     * Returns a card in the reserve at a specific index
     * @param index index of card within reserve
     * @return DevelopmentCard object at the specific index within the reserve
     */
    public DevelopmentCard getReserveCard(int index) {
        return playerReserves.get(index);
    }

    /**
     * Adds a DevelopmentCard object to the player reserve when they reserve a card
     * @param card DevelopmentCard object reserved by player
     */
    public void addReserve(DevelopmentCard card) {
        playerReserves.add(card);
    }

    /**
     * Removes DevelopmentCard object from player reserve when they decide to buy it
     * @param card DevelopmentCard object within reserve the player wants to buy
     */
    public void buyReserve(DevelopmentCard card) {
        playerReserves.remove(card);
    }

    /**
     * Returns the total number of cards within the player's reserve
     * @return total number of cards within the player's reserve
     */
    public int totalReserves() {
        return playerReserves.size();
    }

    /**
     * Returns a string containing the player's prestige points, token, bonuses, reserved cards, nobles, total tokens and total cards in reserve
     */
    public void printStatus() {
        System.out.println("Player points = " + playerPoints + " | tokens = " + playerTokens 
        + " | bonuses = " + playerBonuses + " | reserves = " + playerReserves + " | nobles = " + playerNobles + " | totalTokens = " + totalTokens() + " | totalReserves = " + totalReserves());
    }


    /**
     * Returns all reserve cards reserved by player
     * @return Arraylist of DevelopmentCard objects reserved by the player
     */
    public ArrayList<DevelopmentCard> getReservedCards() {
        return new ArrayList<>(playerReserves);
    }

    /**
     * Returns all cards bought by player
     * @return Arraylist of DevelopmentCard objects bought by the player
     */
    public ArrayList<DevelopmentCard> getBoughtCards() {
        return new ArrayList<>(playerDevelopmentCards);
    }

    /**
     * Cheat/debug method for quickly giving player bonuses of a certain color
     *
     * @param color the bonus color to add
     * @param amount the number of bonus cards to add
     * @throws IllegalArgumentException if the color is not valid
     */
    public void addBonus(String color, int amount) {
        if (!playerBonuses.containsKey(color)) {
            throw new IllegalArgumentException("Invalid bonus color: " + color);
        }

        playerBonuses.put(color, playerBonuses.get(color) + amount);
    }

}