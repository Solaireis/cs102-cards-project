/*
This Noble class stores the noble object, each noble object has points and
cost for each card color.
*/
package Cards.Noble;

import Cards.AbstractCard.AbstractCard;

public class Noble extends AbstractCard {

    // Constructor to initialise all the information for individual card
    public Noble(int points, int blackCost, int whiteCost, int redCost, int blueCost, int greenCost){

        super(points, blackCost, whiteCost, redCost, blueCost, greenCost);

    }

    @Override
    public String toString() {
        return super.toString();
    }
}