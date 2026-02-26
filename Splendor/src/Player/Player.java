/*
This player class store the information of a single player object
A player has points, bonus owned, tokens owned, developmentCard owned, developmentCard reversed, noble owned
*/
package Player;

import Cards.DevelopmentCard.*;
import Cards.Token.*;
import Cards.Noble.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Player {

    // HashMap to store the tokens and Bonuses, basically they are the same object but token is temporary while bonus is permenent
    // So use two differet list to store them.

    // Why using HashMap for Tokens but ArrayList for Cards is because, For tokens, each tokens have a name and value,
    // the name is a constant but value is a varible, so that varible is mapped to a constant, so whenever i want to change
    // or search for that value i can just search for the constant name, then the system will tells me the value it corresponding to.
    // However, the development card has not variables which needs to be changed manually, so there is not need to search for a 
    // specific card. System can just randomly draw cards from the desk and place it on the table and for player to buy it.
    private HashMap<String, Integer> playerTokens = new HashMap<>();
    private HashMap<String, Integer> playerBonuses = new HashMap<>();
    private ArrayList<DevelopmentCard> playerDevelopmentCards = new ArrayList<>();
    private ArrayList<Noble> playerNobles = new ArrayList<>();
    private int playerPoints = 0;

    public Player(){
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
    }
    
    public int getTokens(String color){
        return playerTokens.get(color);
    }

    public int getBonus(String color){
        return playerBonuses.get(color);
    }

    public int getPoints(){
        return playerPoints;
    }


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
            throw new IllegalArgumentException("Player not enough " + color);
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

    public int totalDevelopementCards() {
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

    public void printStatus() {
        System.out.println("Player points=" + playerPoints + " tokens=" + playerTokens 
        + " bonuses=" + playerBonuses + " totalTokens=" + totalTokens());
    }
}