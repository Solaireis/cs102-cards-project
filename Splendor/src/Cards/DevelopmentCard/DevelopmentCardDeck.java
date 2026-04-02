
package Cards.DevelopmentCard;

import Properties.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The DevelopmentCardDeck class stores and manages all the development cards separated by level
*/
public class DevelopmentCardDeck {

    // ArrayList to store the different card with different level.
    private ArrayList<DevelopmentCard> level1Deck;
    private ArrayList<DevelopmentCard> level2Deck;
    private ArrayList<DevelopmentCard> level3Deck;

    /**
     * Initializes and shuffles development card decks by level
     */
    public DevelopmentCardDeck() {
        level1Deck = new ArrayList<>();
        level2Deck = new ArrayList<>();
        level3Deck = new ArrayList<>();

        //placeholder variables for filepath
        String tier1DeckDir = null;
        String tier2DeckDir = null;
        String tier3DeckDir = null;
        
        try{
            Reader reader = new Reader(); // Create an instance of Reader
            //calls for the config properties filepaths of the current cards
            tier1DeckDir = reader.getTierDeck(1);
            tier2DeckDir = reader.getTierDeck(2);
            tier3DeckDir = reader.getTierDeck(3);
            System.out.println("Deck Files found!");
            // Call the method on the instance
        } catch ( Exception e){
                System.out.println("Cant find file");
        }
        // initialise the deck
        initializeDeck(tier1DeckDir, level1Deck, 1);
        initializeDeck(tier2DeckDir, level2Deck, 2);
        initializeDeck(tier3DeckDir, level3Deck, 3);
        

        Collections.shuffle(level1Deck);
        Collections.shuffle(level2Deck);
        Collections.shuffle(level3Deck);
    }


    /**
     * Reads from csv files and creates DevelopmentCard objects
     * @param fileName name of the csv file
     * @param deck list of development cards to store DevelopmentCard objects
     * @param level tierlevel the development cards
     */
    public static void initializeDeck(String fileName, List<DevelopmentCard> deck, int level) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            br.readLine(); // skip header

            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");

                String color = values[0];
                int points = Integer.parseInt(values[1]);
                int blackCost = Integer.parseInt(values[2]);
                int whiteCost = Integer.parseInt(values[3]);
                int redCost = Integer.parseInt(values[4]);
                int blueCost = Integer.parseInt(values[5]);
                int greenCost = Integer.parseInt(values[6]);
                String id = values[7];                              // needed for later updating UI

                DevelopmentCard card = new DevelopmentCard(color, points, blackCost, whiteCost, redCost, blueCost, greenCost, id);
                card.setLevel(level);
                deck.add(card);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks if level 1 DevelopmentCardDeck is empty
     * @return true if deck is empty
     */
    public boolean isLevel1Empty(){ 
        return level1Deck.isEmpty(); 
    }

    /**
     * Checks if level 2 DevelopmentCardDeck is empty
     * @return true if deck is empty
     */
    public boolean isLevel2Empty(){ 
        return level2Deck.isEmpty(); 
    }

    /**
     * Checks if level 3 DevelopmentCardDeck is empty
     * @return true if deck is empty
     */
    public boolean isLevel3Empty(){ 
        return level3Deck.isEmpty(); 
    }


    /**
     * Draws DevelopmentCard from level 1 DevelopmentCardDeck
     * @return DevelopmentCardObject at top of deck
     * @throws IllegalArgumentException if deck is empty
     */
    public DevelopmentCard drawLevel1(){
        if (isLevel1Empty()) {
            throw new IllegalArgumentException("level 1 development cards are not enough");
        }
        return level1Deck.remove(0);
    }

    /**
     * Draws DevelopmentCard from level 2 DevelopmentCardDeck
     * @return DevelopmentCardObject at top of deck
     * @throws IllegalArgumentException if deck is empty
     */
   public DevelopmentCard drawLevel2(){ 
        if (isLevel2Empty()) {
            throw new IllegalArgumentException("level 2 development cards are not enough");
        }
        return level2Deck.remove(0);
    }

    /**
     * Draws DevelopmentCard from level 3 DevelopmentCardDeck
     * @return DevelopmentCardObject at top of deck
     * @throws IllegalArgumentException if deck is empty
     */
    public DevelopmentCard drawLevel3(){
        if (isLevel3Empty()) {
            throw new IllegalArgumentException("level 3 development cards are not enough");
        }
        return level3Deck.remove(0);
    }

    /**
     * Gets the number of remaining cards in the level 1 DevelopmentCardDeck
     * @return number of remaining cards
     */
    public int level1Size(){ 
        return level1Deck.size(); 
    }

    /**
     * Gets the number of remaining cards in the level 2 DevelopmentCardDeck
     * @return number of remaining cards
     */
    public int level2Size(){ 
        return level2Deck.size(); 
    }

    /**
     * Gets the number of remaining cards in the level 3 DevelopmentCardDeck
     * @return number of remaining cards
     */
    public int level3Size(){ 
        return level3Deck.size(); 
    }


}