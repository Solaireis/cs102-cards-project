/*
This NobleFaceUP class store the (player + 1) nobles which will be used / are face up on the table.
*/

package Cards.Noble;

import java.util.ArrayList;

public class NobleFaceUP {

    private ArrayList<Noble> faceUp = new ArrayList<>();
    int cardAmt;

    // Constructor, initally put cardAmt nobles face up
    public NobleFaceUP(NobleDeck desk, int playerAmt) {
        cardAmt = playerAmt + 1;
        for (int i = 0; i < cardAmt; i++) {
            faceUp.add(desk.draw());
        }
    }

    public Noble getCard(int index) {
        return faceUp.get(index);
    }

    // remove no refill
    public void remove(int index) {
        faceUp.remove(index);
    }

    public void printMarket() {
        System.out.println("=== Noble ===");
        printRow(faceUp);
    }


    private void printRow(ArrayList<Noble> row) {
        for (int i = 0; i < row.size(); i++) {
            System.out.println("[" + i + "] " + row.get(i));
        }
    }
}