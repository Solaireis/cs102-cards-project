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

    public Noble getCard(int index) {
        return faceUp.get(index);
    }

    // remove no refill
    public void remove(int index) {
        faceUp.remove(index);
    }

    public void printMarket() {
        System.out.println("=== Noble ===");
        for (int i = 0; i < faceUp.size(); i++) {
            System.out.println("[" + i + "] " + faceUp.get(i));
        }
    }
}