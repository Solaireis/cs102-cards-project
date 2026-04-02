package Cards.Noble;

import java.util.ArrayList;
import java.util.Collections;

/**
 * The NobleDeck class stores all noble cards
 */

public class NobleDeck {

    private ArrayList<Noble> nobles = new ArrayList<>();

    /**
     * Initialize noble cards and shuffle them in random order
     */
    public NobleDeck() {
        // call noble constructor: new Noble(points, blackCost, whiteCost, redCost, blueCost, greenCost)
        // Note: The Cost here is not the cost of token, it is the number of particular color development card 
        // a player must have. This is the actually the bonus a player owned.

        nobles.add(new Noble(3, 0, 0, 4, 0, 4, "noble1")); // 4R + 4G
        nobles.add(new Noble(3, 3, 3, 3, 0, 0, "noble2")); // 3B + 3W + 3R
        nobles.add(new Noble(3, 0, 4, 0, 4, 0, "noble3")); // 4W + 4Blu
        nobles.add(new Noble(3, 4, 4, 0, 0, 0, "noble4")); // 4Blk + 4W
        nobles.add(new Noble(3, 0, 0, 0, 4, 4, "noble5")); // 4Blu + 4G
        nobles.add(new Noble(3, 0, 0, 3, 3, 3, "noble6")); // 3R + 3Bl + 3G
        nobles.add(new Noble(3, 0, 3, 0, 3, 3, "noble7")); // 3W + 3Bl + 3G
        nobles.add(new Noble(3, 4, 0, 4, 0, 0, "noble8")); // 4B + 4R
        nobles.add(new Noble(3, 3, 3, 0, 3, 0, "noble9")); // 3B + 3W + 3Blu
        nobles.add(new Noble(3, 3, 0, 3, 0, 3, "noble10")); // 3B + 3R + 3G
        
        Collections.shuffle(nobles);
    }


    /**
     * Returns the first card of the noble deck
     * @return Noble object at index 0 of noble deck
     */
    public Noble draw(){
        if (isEmpty()) {
            throw new IllegalArgumentException("Noble cards are not enough");
        }
        return nobles.remove(0);
    }


    /**
     * Returns the size of the NobleDeck
     * @return size of NobleDeck
     */
    public int size(){
        return nobles.size();
    }

    // Getter, get the list of Nobles in the desk
    /**
     * Return an arrayList of Noble objects in the deck
     * @return arrayList of Nobles in NobleDeck
     */
    public ArrayList<Noble> getNobles() {
        return new ArrayList<>(nobles);
    }


    /**
     * Removes a Noble object from the NobleDeck
     * @param noble Noble object to remove
     */
    public void removeNoble(Noble noble){
        nobles.remove(noble);
    }


    /**
     * Returns true if NobleDeck is empty, false otherwise
     * @return true if NobleDeck is empty, false otherwise
     */
    public boolean isEmpty() {
        return nobles.isEmpty();
    }

}
