/*
This DevelopmentCard class stores a single development card object, each development object has bonus color, points,
cost for each token color.
*/
package Cards.DevelopmentCard;

import Cards.AbstractCard.AbstractCard;

public class DevelopmentCard extends AbstractCard {
    private String color;     // color of the bonus token
    private String id;        // id to recognize card

    // Constructor to initialise all the information for individual card
    public DevelopmentCard(String color, int points, int blackCost, int whiteCost, int redCost, int blueCost, int greenCost, String id){
        
        super(points, blackCost, whiteCost, redCost, blueCost, greenCost);
        this.color = color;
        this.id = id;

    }

    public String getBonus(){ 
        return color; 
    }

    public String getID() {
        return id;
    }
    
    @Override
    public String toString() {
        return color + super.toString();
    }
}