/*
This class stores all the token objects, and with the methods to arrange the token accordingly. 
So whenever we want to edit the number of token we need to call the methods in this class only!
*/
package Cards.Token;

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

    public TokenBank() {

        tokens = new HashMap<>();

        // One colour is mapping to a specific token object.
        tokens.put(WHITE, new Token(WHITE, 7));
        tokens.put(BLUE, new Token(BLUE, 7));
        tokens.put(GREEN, new Token(GREEN, 7));
        tokens.put(RED, new Token(RED, 7));
        tokens.put(BLACK, new Token(BLACK, 7));
        tokens.put(GOLD, new Token(GOLD, 5));
    }

    // Getter, get the amount of the specific color token in the bank
    // get(color) is the method to get the token Object with the color we want
    // getAmount() is the method in the token class to get the Amount of that token object. 
    public int get(String color) {
        return tokens.get(color).getAmount();
    }

    public void add(String color, int amount) {
        tokens.get(color).add(amount);
    }

    // helper function to check if there is enought tokens in the bank for player to take
    public boolean hasEnough(String color, int amount) {
        return get(color) >= amount;
    }

    public void remove(String color, int amount){
        if (!hasEnough(color, amount)) {
            throw new IllegalArgumentException(color + " Token in the bank is not enough");
        }
        tokens.get(color).remove(amount);
    }

    public void printBank() {
        System.out.println(tokens.values());
    }
}