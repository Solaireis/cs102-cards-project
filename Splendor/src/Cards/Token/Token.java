/**
This Token class stores the information of a single colour Token object with the repective amount.
*/
package Cards.Token;

public class Token {
    private String color;
    private int amount;

    /**
     * Initializes the color and amount of a token
     * @param color color of the token
     * @param amount remaining amount of the token
     */
    public Token(String color, int amount) {
        this.color = color;
        this.amount = amount;
    }

    /**
     * Returns the color of the token
     * @return color of the token
     */
    public String getColor() {
        return color;
    }

    /**
     * Returns remaining amount of the token
     * @return remaining amount of the token
     */
    public int getAmount() {
        return amount;
    }

    // Add tokens into the bank when the user purchased a developement card
    /**
     * Adds tokens when tokens are returned to the tokenBank, increases the remaining amount of tokens
     * @param value number of tokens returned to tokenBank of this color
     */
    public void add(int value) {
        amount += value;
    }

    // Remove the tokens from the bank when the user takes the tokens
    /**
     * Removes tokens when user picks up tokens in their turn, decreases the remaining amount of tokens
     * @param value number of tokens chosen by user of this color
     * @throws IllegalArgumentException if number of tokens chosen > remaining amount
     */
    public void remove(int value) {
        if (amount < value) {
            throw new IllegalArgumentException(color + " token not enough");
        }
        amount -= value;
    }

    /**
     * Returns a string containing the color and remaining amount of this token
     */
    @Override
    public String toString() {
        return color + "=" + amount;
    }
}