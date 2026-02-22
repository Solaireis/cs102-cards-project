/*
This DevelopmentCardDesk class store and manage all the development cards
*/
package Cards.DevelopmentCard;

import java.util.ArrayList;
import java.util.Collections;

import Cards.Token.TokenBank;

public class DevelopmentCardDesk {

    // ArrayList to store the different card with different level.
    private ArrayList<DevelopmentCard> level1Deck;
    private ArrayList<DevelopmentCard> level2Deck;
    private ArrayList<DevelopmentCard> level3Deck;

    public DevelopmentCardDesk() {
        level1Deck = new ArrayList<>();
        level2Deck = new ArrayList<>();
        level3Deck = new ArrayList<>();

        initialiseCards();
        shuffleDesks();
    }

    // Call the constructor in the DevelopementCard to initialise all the 90 cards
    private void initialiseCards() {
        level1Deck.add(new DevelopmentCard(TokenBank.BLACK, 1, 0, 1, 1, 1, 1));
        level1Deck.add(new DevelopmentCard(TokenBank.RED, 1, 0, 0, 1, 1, 1));
        level1Deck.add(new DevelopmentCard(TokenBank.WHITE, 1, 1, 0, 1, 0, 1));
        level1Deck.add(new DevelopmentCard(TokenBank.BLUE, 1, 0, 1, 1, 1, 1));
        level1Deck.add(new DevelopmentCard(TokenBank.GREEN, 1, 0, 1, 1, 1, 1));
        level1Deck.add(new DevelopmentCard(TokenBank.BLUE, 1, 0, 1, 1, 1, 1));
        level1Deck.add(new DevelopmentCard(TokenBank.RED, 1, 0, 1, 1, 1, 1));
        level1Deck.add(new DevelopmentCard(TokenBank.GREEN, 1, 0, 1, 1, 1, 1));
        level1Deck.add(new DevelopmentCard(TokenBank.WHITE, 1, 0, 1, 1, 1, 1));
        level1Deck.add(new DevelopmentCard(TokenBank.BLUE, 1, 3, 1, 1, 1, 1));
        level2Deck.add(new DevelopmentCard(TokenBank.GREEN, 2, 0, 2, 0, 4, 0));
        level2Deck.add(new DevelopmentCard(TokenBank.GREEN, 3, 0, 0, 0, 4, 0));
        level2Deck.add(new DevelopmentCard(TokenBank.BLUE, 2, 0, 5, 0, 4, 0));
        level2Deck.add(new DevelopmentCard(TokenBank.BLACK, 2, 2, 0, 0, 4, 0));
        level2Deck.add(new DevelopmentCard(TokenBank.WHITE, 2, 0, 0, 3, 4, 0));
        level2Deck.add(new DevelopmentCard(TokenBank.WHITE, 2, 0, 3, 0, 3, 1));
        level2Deck.add(new DevelopmentCard(TokenBank.BLUE, 2, 0, 1, 0, 4, 0));
        level2Deck.add(new DevelopmentCard(TokenBank.WHITE, 2, 6, 0, 0, 0, 0));
        level2Deck.add(new DevelopmentCard(TokenBank.BLUE, 3, 3, 0, 3, 0, 3));
        level3Deck.add(new DevelopmentCard(TokenBank.BLACK, 3, 2, 0, 0, 1, 0));
        level3Deck.add(new DevelopmentCard(TokenBank.BLUE, 4, 6, 0, 0, 0, 0));
        level3Deck.add(new DevelopmentCard(TokenBank.GREEN, 3, 6, 0, 0, 0, 0));
        level3Deck.add(new DevelopmentCard(TokenBank.RED, 4, 2, 0, 0, 4, 0));
        level3Deck.add(new DevelopmentCard(TokenBank.GREEN, 3, 6, 0, 0, 0, 0));
        level3Deck.add(new DevelopmentCard(TokenBank.RED, 3, 6, 0, 0, 0, 0));
        level3Deck.add(new DevelopmentCard(TokenBank.BLUE, 3, 2, 0, 3, 0, 0));
        level3Deck.add(new DevelopmentCard(TokenBank.WHITE, 4, 3, 1, 1, 0, 0));
        level3Deck.add(new DevelopmentCard(TokenBank.BLACK, 3, 3, 1, 2, 1, 1));
        level3Deck.add(new DevelopmentCard(TokenBank.RED, 3, 2, 4, 0, 1, 3));
        //....... 90 lines of code here to store all the cards if decide to hardcode LOL.......
    }

    // Method in the collection to automatically shuffle the order of the cards for every level.
    private void shuffleDesks() {
        Collections.shuffle(level1Deck);
        Collections.shuffle(level2Deck);
        Collections.shuffle(level3Deck);
    }

    // Helper function to check if there is enough card in the desk for system to draw cards
    public boolean isLevel1Empty(){ 
        return level1Deck.isEmpty(); 
    }
    public boolean isLevel2Empty(){ 
        return level2Deck.isEmpty(); 
    }
    public boolean isLevel3Empty(){ 
        return level3Deck.isEmpty(); 
    }


    // Method to draw cards to place on the table for each level
    public DevelopmentCard drawLevel1(){
        if (isLevel1Empty()) {
            throw new IllegalArgumentException("level 1 development cards are not enough");
        }
        return level1Deck.remove(0);
    }

   public DevelopmentCard drawLevel2(){ 
        if (isLevel2Empty()) {
            throw new IllegalArgumentException("level 2 development cards are not enough");
        }
        return level2Deck.remove(0);
    }

    public DevelopmentCard drawLevel3(){
        if (isLevel3Empty()) {
            throw new IllegalArgumentException("level 3 development cards are not enough");
        }
        return level3Deck.remove(0);
    }

    // return the number of remining cards in the desk.
    public int level1Size(){ 
        return level1Deck.size(); 
    }
    public int level2Size(){ 
        return level2Deck.size(); 
    }
    public int level3Size(){ 
        return level3Deck.size(); 
    }

}