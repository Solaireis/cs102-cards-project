/*
This DevelopmentCard class stores a single development card object, each development object has bonus color, points,
cost for each token color.
*/
package Cards.DevelopmentCard;

import Cards.AbstractCard.AbstractCard;

public class DevelopmentCard extends AbstractCard {
    private String color;     // color of the bonus token

    // Constructor to initialise all the information for individual card
    public DevelopmentCard(String color, int points, int blackCost, int whiteCost, int redCost, int blueCost, int greenCost){
        
        super(points, blackCost, whiteCost, redCost, blueCost, greenCost);
        this.color = color;

    }

    public String getBonus(){ 
        return color; 
    
    }
    
    @Override
    public String toString() {
        return color + super.toString();
    }
}