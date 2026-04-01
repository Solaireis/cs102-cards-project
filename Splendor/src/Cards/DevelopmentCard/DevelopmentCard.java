
package Cards.DevelopmentCard;

import Cards.AbstractCard.AbstractCard;
/**
 * The DevelopmentCard class stores a single development card object, each development object has bonus color, points, cost for each token color.
*/
public class DevelopmentCard extends AbstractCard {
    private String color;     // color of the bonus token
    private int level;        // tier level of card

    /**
     * Initializes a development card's color, prestige points, cost and id
     * @param color the bonus color
     * @param points the number of prestige points 
     * @param blackCost number of black tokens required
     * @param whiteCost number of white tokens required
     * @param redCost number of red tokens required
     * @param blueCost number of blue tokens required
     * @param greenCost number of green tokens required
     * @param id unique identifier of each card
     */
    public DevelopmentCard(String color, int points, int blackCost, int whiteCost, int redCost, int blueCost, int greenCost, String id){
        
        super(points, blackCost, whiteCost, redCost, blueCost, greenCost, id);
        this.color = color;

    }

    /**
     * Returns the bonus color of the development card
     * @return bonus color
     */
    public String getBonus(){ 
        return color; 
    }

    /**
     * Returns the tier level the development card
     * @return tier level
     */
    public int getLevel(){ 
        return level; 
    }

    /**
     * Sets the tier level of the development card
     */
    public void setLevel(int level){ 
        this.level = level;
    }
    
    /**
     * Returns a string containing the color, prestige points and cost of the card
     * @return the color, prestige points and cost of card
     */
    @Override
    public String toString() {
        return color + super.toString();
    }
}