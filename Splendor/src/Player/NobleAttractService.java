/*
This class handle the Noble attraction logic
at the end of a player's turn, if they qualify for >=1 nobles,
they must choose exactly One noble (if multiple, player chooses by themselves).
*/

package Player;

import java.util.ArrayList;
import java.util.Scanner;

import Cards.Noble.Noble;
import Cards.Noble.NobleDesk;

public class NobleAttractService {

    // Awards One noble to the player if possible.
    // return the Noble awarded, or null if player qualifies for nothing.

    public Noble awardNobleIfPossible(Player player, NobleDesk deck, Scanner sc) {
        ArrayList<Noble> eligible = deck.getAttractableNobles(player);

        if (eligible.isEmpty()) {
            return null;
        }

        Noble chosen;
        if (eligible.size() == 1) {
            chosen = eligible.get(0);  // returns that one noble
        } else {
            chosen = chooseOne(eligible, sc); // If multiple nobles player choose, invoke the helper method
        }

        player.addNobles(chosen);
        deck.removeNoble(chosen);
        return chosen;
    }

    // Let player choose one noble when multiple nobles are eligible
    private Noble chooseOne(ArrayList<Noble> eligible, Scanner sc) {
        System.out.println("You qualify for multiple nobles. Choose ONE:");

        // print out all the information of nobles for player to choose
        for (int i = 0; i < eligible.size(); i++) {
            System.out.println(i + ": " + eligible.get(i));
        }

        int idx = readIndex(sc, eligible.size()); // invoke the helper method, making sure that the play's input is valid.
        return eligible.get(idx);
    }

    // Reads the integer input from the player
    // the input must within the number of eligible nobles
    // like if a player have 4 nobles then their input must between 0 to 3
    // keep prompt the player until they put a correct number.
    private int readIndex(Scanner sc, int size) {
        while (true) {
            System.out.print("Enter choice (0-" + (size - 1) + "): ");

            if (sc.hasNextInt()) {
                int x = sc.nextInt();
                sc.nextLine(); // consume newline
                if (x >= 0 && x < size) {
                    return x;
                }
            } else {
                sc.nextLine(); // discard invalid input, continue prompting the player
            }

            System.out.println("Invalid choice, try again.");
        }
    }
}