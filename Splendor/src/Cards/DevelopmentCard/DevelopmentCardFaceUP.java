
package Cards.DevelopmentCard;

import java.util.ArrayList;

/**
 * The DevelopmentCardFaceUP class stores the 4 development cards of each level which are face up on the table for players to purchase. 
*/
public class DevelopmentCardFaceUP {

    // ArrayList to store 4 cards of each level
    private ArrayList<DevelopmentCard> faceUp1 = new ArrayList<>();
    private ArrayList<DevelopmentCard> faceUp2 = new ArrayList<>();
    private ArrayList<DevelopmentCard> faceUp3 = new ArrayList<>();


    /**
     * Initializes face up cards for each level in an arrayList
     * @param deck the card decks with all cards separated by level
     */
    public DevelopmentCardFaceUP(DevelopmentCardDeck deck){
        for (int i = 0; i < 4; i++) {
            faceUp1.add(deck.drawLevel1());
            faceUp2.add(deck.drawLevel2());
            faceUp3.add(deck.drawLevel3());
        }
    }

    /**
     * Returns arrayList of face up cards for the specified level
     * @param level specific level of development card
     * @return arrayList of development cards face up for that level
     * @throws IllegalArgumentException when level is invalid
     */
    public ArrayList<DevelopmentCard> getFaceUp(int level) {
        if (level == 1){
            return faceUp1;
        }
        if (level == 2){
            return faceUp2;
        }
        if (level == 3){
            return faceUp3;
        }
        throw new IllegalArgumentException("Invalid level: " + level);
    }

    /**
     * Returns face up card at specific level and index 
     * @param level level of development card
     * @param index index of development card within a level
     * @return DevelopmentCard object at that level and index
     */
    public DevelopmentCard getCard(int level, int index){
        return getFaceUp(level).get(index);
    }

    /**
     * Removes a face up card and replaces the empty slot with a card from the top of the development deck of that level
     * @param level level of development card to remove
     * @param index index of development card to remove
     * @param deck the card decks with all cards separated by level
     */
    public void removeAndRefill(int level, int index, DevelopmentCardDeck deck) {
        ArrayList<DevelopmentCard> row = getFaceUp(level);
        row.remove(index);

        // refill the empty slot (if deck not empty)
        DevelopmentCard newCard = null;
        if (level == 1 && !deck.isLevel1Empty()){
            newCard = deck.drawLevel1();
        }
        if (level == 2 && !deck.isLevel2Empty()){
            newCard = deck.drawLevel2();
        }
        if (level == 3 && !deck.isLevel3Empty()){
            newCard = deck.drawLevel3();
        }

        if (newCard != null){
            row.add(newCard);
        }
    }

    /**
     * Prints all the face up cards separated by level
     */
    public void printMarket() {
        System.out.println("=== Market Level 1 ===");
        printRow(faceUp1);
        System.out.println();
        System.out.println("=== Market Level 2 ===");
        printRow(faceUp2);
        System.out.println();
        System.out.println("=== Market Level 3 ===");
        printRow(faceUp3);
        System.out.println();
    }


    /**
     * Formats and prints the face up cards of a specific level
     * @param row arrayList of face up cards in a specific level
     */
    private void printRow(ArrayList<DevelopmentCard> row) {

        String[] colors = {"BLACK", "WHITE", "RED", "BLUE", "GREEN"};
        String[][] matrix = new String[9][row.size()];

        for (int i = 0; i < row.size(); i++) {
            matrix[0][i] = "Index " + i;
            matrix[1][i] = "--------------";
            matrix[2][i] = String.format("| %dpt %6s |", row.get(i).getPoints(), row.get(i).getBonus());
            matrix[8][i] = "--------------";
            int point = 7;
            for (String color : colors) {
                if (row.get(i).getCost(color) != 0) {
                    matrix[point][i] = String.format("| %-6s= %-3d|", color, row.get(i).getCost(color));
                    point -= 1;
                }
            }
            while (point != 2) {
                matrix[point][i] = "|            |";
                point -= 1;
            }
        }

        for (String[] rows : matrix) {
            for (String val : rows) {
                System.out.printf("%-17s", val);
            }
            System.out.println();
        }
     
    }
}