/*
This NobleFaceUP class store the (player + 1) nobles which will be used / are face up on the table.
*/

package Cards.Noble;

import java.util.ArrayList;

public class NobleFaceUP {

    private ArrayList<Noble> faceUp = new ArrayList<>();
    private int cardAmt;

    // Constructor, initally put cardAmt nobles face up
    public NobleFaceUP(NobleDeck desk, int playerAmt) {
        cardAmt = playerAmt + 1;
        if (cardAmt == 2) {
            cardAmt += 1;
        }
        
        for (int i = 0; i < cardAmt; i++) {
            Noble n = desk.draw();
            if(n != null){
                faceUp.add(n);
            }
        }
    }

    public ArrayList<Noble> getFaceUp() {
        return faceUp;
    }

    public Noble getCard(int index) {
        return faceUp.get(index);
    }

    // remove no refill
    public void remove(int index) {
        faceUp.remove(index);
    }

    public void remove(Noble noble) {
        faceUp.remove(noble);
    }

    public void printMarket() {
        System.out.println("=== Noble ===");
        String[] colors = {"BLACK", "WHITE", "RED", "BLUE", "GREEN"};
        String[][] matrix = new String[9][faceUp.size()];

        for (int i = 0; i < faceUp.size(); i++) {
            matrix[0][i] = "Index " + i;
            matrix[1][i] = "--------------";
            matrix[2][i] = String.format("| %dpt        |", faceUp.get(i).getPoints());
            matrix[8][i] = "--------------";
            int point = 7;
            for (String color : colors) {
                if (faceUp.get(i).getCost(color) != 0) {
                    matrix[point][i] = String.format("| %-6s= %-3d|", color, faceUp.get(i).getCost(color));
                    point -= 1;
                }
            }
            while (point != 2) {
                matrix[point][i] = "|            |";
                point -= 1;
            }
        }

        for (String[] row : matrix) {
            for (String val : row) {
                System.out.printf("%-17s", val);
            }
            System.out.println();
        }
        
    }
}