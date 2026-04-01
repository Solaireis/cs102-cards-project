/**
 * The TokenBank class stores all the token objects on the board in a hashmap
 */

package Cards.Token;

import Properties.*;
import java.util.HashMap;

public class TokenBank{

    // Constant name for each color, so whenever we call the Token color we must using TokenBank.COLOR
    // It is helps to recognise that it is the color of a token no anything else.
    public static final String WHITE = "WHITE";
    public static final String BLUE = "BLUE";
    public static final String GREEN = "GREEN";
    public static final String RED = "RED";
    public static final String BLACK = "BLACK";
    public static final String GOLD = "GOLD";

    // List of tokens, which store the color of the token and the token object.
    private HashMap<String, Token> tokens;

    /**
     * Initializes the number of tokens of each color based on number of players
     * If customTokenMode == 1 in config.properties, set number of tokens based on config.properties
     * @param numOfPlayers number of players 
     */
    public TokenBank(int numOfPlayers) {

        tokens = new HashMap<>();

        // One colour is mapping to a specific token object.
        int white = 0;
        int blue = 0;
        int green = 0;
        int red = 0;
        int black = 0;
        int gold = 0;
        int customTokenMode = 0;
        //takes a predetermine number of tokens from the properties file
        try{
            Reader reader = new Reader();
            // Create an instance of Reader
            customTokenMode = reader.getCustomTokenMode();
            if (customTokenMode == 1){
                white = reader.getColourToken("WHITE");
                blue = reader.getColourToken("BLUE");
                green = reader.getColourToken("GREEN");
                red = reader.getColourToken("RED");
                black = reader.getColourToken("BLACK");
                gold = reader.getColourToken("GOLD");
            }
            System.out.println("Num of Cards properties found!");
            // Call the method on the instance
        } catch ( Exception e){
            System.out.println("Cant find file");
        }
        if (customTokenMode == 0){
            switch(numOfPlayers) {
                case 2:
                    tokens.put(WHITE, new Token(WHITE, 4));
                    tokens.put(BLUE, new Token(BLUE, 4));
                    tokens.put(GREEN, new Token(GREEN, 4));
                    tokens.put(RED, new Token(RED, 4));
                    tokens.put(BLACK, new Token(BLACK, 4));
                    tokens.put(GOLD, new Token(GOLD, 5));
                    break;
                case 3:
                    tokens.put(WHITE, new Token(WHITE, 5));
                    tokens.put(BLUE, new Token(BLUE, 5));
                    tokens.put(GREEN, new Token(GREEN, 5));
                    tokens.put(RED, new Token(RED, 5));
                    tokens.put(BLACK, new Token(BLACK, 5));
                    tokens.put(GOLD, new Token(GOLD, 5));
                    break;
                default:
                    tokens.put(WHITE, new Token(WHITE, 7));
                    tokens.put(BLUE, new Token(BLUE, 7));
                    tokens.put(GREEN, new Token(GREEN, 7));
                    tokens.put(RED, new Token(RED, 7));
                    tokens.put(BLACK, new Token(BLACK, 7));
                    tokens.put(GOLD, new Token(GOLD, 5));
            }
        }
        if (customTokenMode == 1){
            tokens.put(WHITE, new Token(WHITE, white));
            tokens.put(BLUE, new Token(BLUE, blue));
            tokens.put(GREEN, new Token(GREEN, green));
            tokens.put(RED, new Token(RED, red));
            tokens.put(BLACK, new Token(BLACK, black));
            tokens.put(GOLD, new Token(GOLD, gold));
        }
    }

    // Getter, get the amount of the specific color token in the bank
    // get(color) is the method to get the token Object with the color we want
    // getAmount() is the method in the token class to get the Amount of that token object. 
    public int get(String color) {
        return tokens.get(color).getAmount();
    }

    /**
     * Adds tokens when tokens are returned to the tokenBank, increases the remaining amount of tokens
     * @param color color of token
     * @param amount amount returned to tokenBank
     */
    public void add(String color, int amount) {
        tokens.get(color).add(amount);
    }

    /**
     * Returns true if there are enough tokens in the tokenBank for player to take
     * @param color color of token
     * @param amount amount player wants to take
     * @return true if enough tokens, false otherwise
     */
    public boolean hasEnough(String color, int amount) {
        return get(color) >= amount;
    }

    /**
     * Removes tokens when user picks up tokens in their turn, decreases the remaining amount of tokens
     * @param color color of token
     * @param amount amount of tokens user wants to take
     * @throws IllegalArgumentException if not enough remaining tokens
     */
    public void remove(String color, int amount){
        if (!hasEnough(color, amount)) {
            throw new IllegalArgumentException(color + " Token in the bank is not enough");
        }
        tokens.get(color).remove(amount);
    }

    /**
     * Prints all the remaining amounts of each color token
     */
    public void printBank() {
        System.out.println(tokens.values());
    }
}