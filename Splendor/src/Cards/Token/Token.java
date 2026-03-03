/*
This Token class stores the information of a single colour Token object with the repective amount.
*/
package Cards.Token;

public class Token {
    private String color;
    private int amount;

    public Token(String color, int amount) {
        this.color = color;
        this.amount = amount;
    }

    // Get the color of the token
    public String getColor() {
        return color;
    }

    // Get the amount of the token in the bank, this amount is initialised the TokanBank class with
    // an initial value of 7 (per token) and 5 (gold)
    public int getAmount() {
        return amount;
    }

    // Add tokens into the bank when the user purchased a developement card
    public void add(int value) {
        amount += value;
    }

    // Remove the tokens from the bank when the user takes the tokens
    public void remove(int value) {
        if (amount < value) {
            throw new IllegalArgumentException(color + " token not enough");
        }
        amount -= value;
    }


    @Override
    public String toString() {
        return color + "=" + amount;
    }
}