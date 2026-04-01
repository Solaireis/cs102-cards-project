/**
 * The AbstractCard class stores the shared attributes of card objects, which can be used for both Development cards and Noble cards
*/
package Cards.AbstractCard;
import Cards.Token.TokenBank;

public abstract class AbstractCard {
    private int points;
    private int blackCost;
    private int whiteCost;
    private int redCost;
    private int blueCost;
    private int greenCost;
    private String id;

    /**
     * Initializes a card's prestige points, cost and id
     * @param points the number of prestige points 
     * @param blackCost number of black tokens required
     * @param whiteCost number of white tokens required
     * @param redCost number of red tokens required
     * @param blueCost number of blue tokens required
     * @param greenCost number of green tokens required
     * @param id unique identifier of each card
     */
    public AbstractCard(int points, int blackCost, int whiteCost, int redCost, int blueCost, int greenCost, String id){

        this.points = points;
        this.blackCost = blackCost;
        this.whiteCost = whiteCost;
        this.redCost = redCost;
        this.blueCost = blueCost;
        this.greenCost = greenCost;
        this.id = id;

    }

    /**
     * Returns the number of prestige points the card is worth
     * @return prestige points 
     */
    public int getPoints() {
        return points;
    }

    /**
     * Returns the required number of tokens of the specified color to buy the card
     * @param tokenColor color of token
     * @return required number of this color token
     * @throws IllegalArgumentException if token color not valid
     */
    public int getCost(String tokenColor) {
        if (tokenColor.equals(TokenBank.BLACK)){
            return blackCost;
        }
        if (tokenColor.equals(TokenBank.WHITE)){
            return whiteCost;
        }
        if (tokenColor.equals(TokenBank.RED)){
            return redCost;
        }
        if (tokenColor.equals(TokenBank.BLUE)){
            return blueCost;
        }
        if (tokenColor.equals(TokenBank.GREEN)){
            return greenCost;
        }
        throw new IllegalArgumentException("Unknown token color: " + tokenColor);
    }

    /**
     * Returns a string containing the prestige points and cost of the card
     * @return the prestige points and cost of card
     */
    @Override
    public String toString() {
        return " (" + points + "pt)" + " cost[Bk=" + blackCost + ", W=" + whiteCost + ", R=" + redCost + ", Bl=" + blueCost + ", G=" + greenCost + "]";
    }

    /**
     * Returns the id number of the card
     * @return id of the card
     */
    public String getID() {
        return id;
    }
}