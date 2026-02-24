package Cards.Noble;

import java.util.ArrayList;

public class NobleDeck {

    private final ArrayList<Noble> nobles = new ArrayList<>();

    public NobleDeck() {
        CreateNobles();
    }

    // Create all nobles, there are 10 nobles in total
    private void CreateNobles() {
    // call noble constructor: new Noble(points, blackCost, whiteCost, redCost, blueCost, greenCost)
    // Note: The Cost here is not the cost of token, it is the number of particular color development card
    //       a player must have. This is the actually the bonus a player owned.

        nobles.add(new Noble(3, 4, 4, 0, 0, 0)); // 4B + 4W
        nobles.add(new Noble(3, 0, 0, 3, 3, 3)); // 3R + 3Bl + 3G
        nobles.add(new Noble(3, 0, 4, 0, 4, 0)); // 4W + 4Bl
        nobles.add(new Noble(3, 0, 3, 0, 3, 3)); // 3W + 3Bl + 3G
        nobles.add(new Noble(3, 4, 0, 4, 0, 0)); // 4B + 4R
        nobles.add(new Noble(3, 0, 0, 4, 0, 4)); // 4R + 4G
        nobles.add(new Noble(3, 0, 0, 0, 4, 4)); // 4Bl + 4G
        nobles.add(new Noble(3, 3, 3, 3, 0, 0)); // 3B + 3W + 3R
        nobles.add(new Noble(3, 3, 3, 0, 3, 0)); // 3B + 3W + 3Bl
        nobles.add(new Noble(3, 3, 0, 3, 0, 3)); // 3B + 3R + 3G
    }
}
