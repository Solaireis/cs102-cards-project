/*
This DevelopmentCardFaceUP class store the 4 development card of each level which are face up on the table for players to purchase. 
*/
package Cards.DevelopmentCard;

import java.util.ArrayList;

public class DevelopmentCardFaceUP {

    // ArrayList to store 4 cards of each level
    private ArrayList<DevelopmentCard> faceUp1 = new ArrayList<>();
    private ArrayList<DevelopmentCard> faceUp2 = new ArrayList<>();
    private ArrayList<DevelopmentCard> faceUp3 = new ArrayList<>();


    // Constructor to call the draw function in the development card desk class and put it into a arraylist with only 4 elements
    // which simulate the action of place the card from the deck to the table
    public DevelopmentCardFaceUP(DevelopmentCardDesk desk){
        for (int i = 0; i < 4; i++) {
            faceUp1.add(desk.drawLevel1());
            faceUp2.add(desk.drawLevel2());
            faceUp3.add(desk.drawLevel3());
        }
    }

    // Helper function to get the group of face up card with the level we want
    // Is like we have 3 rows of card on the table and this is use to point which row we want
    private ArrayList<DevelopmentCard> getFaceUp(int level) {
        if (level == 1){
            return new ArrayList<>(faceUp1);
        }
        if (level == 2){
            return new ArrayList<>(faceUp2);
        }
        if (level == 3){
            return new ArrayList<>(faceUp3);
        }
        throw new IllegalArgumentException("Invalid level: " + level);
    }

    // Getter to get the specific face up card object we want
    // getFaceUp(level) return the row we want and get(index) return which column we want
    // with row and column we can get specific card
    // This function return the DevelopmentCard Object, so it is used to show to the user the information of a 
    // specific card, like points it has, bonus, and cost.
    public DevelopmentCard getCard(int level, int index){
        return getFaceUp(level).get(index);
    }

    // Method to revove the card and refill it immediately
    public void removeAndRefill(int level, int index, DevelopmentCardDesk card) {
        ArrayList<DevelopmentCard> row = getFaceUp(level);
        row.remove(index);

        // refill the empty slot (if deck not empty)
        DevelopmentCard newCard = null;
        if (level == 1 && !card.isLevel1Empty()){
            newCard = card.drawLevel1();
        }
        if (level == 2 && !card.isLevel2Empty()){
            newCard = card.drawLevel2();
        }
        if (level == 3 && !card.isLevel3Empty()){
            newCard = card.drawLevel3();
        }

        if (newCard != null){
            row.add(newCard);
        }
    }

    public void printMarket() {
        System.out.println("=== Market Level 1 ===");
        printRow(faceUp1);
        System.out.println("=== Market Level 2 ===");
        printRow(faceUp2);
        System.out.println("=== Market Level 3 ===");
        printRow(faceUp3);
    }


    private void printRow(ArrayList<DevelopmentCard> row) {
        for (int i = 0; i < 4; i++) {
            System.out.println("[" + i + "] " + row.get(i));
        }
    }
}