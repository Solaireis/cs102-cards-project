package Test;
import Cards.DevelopmentCard.*;
import Cards.Token.TokenBank;
import Player.Player;
import Player.PurchaseService;

import java.util.HashSet;
import java.util.Scanner;

public class Game {

    private static final String[] TAKE_COLORS = { TokenBank.WHITE, TokenBank.BLUE, TokenBank.GREEN, TokenBank.RED, TokenBank.BLACK };

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        TokenBank tb = new TokenBank();
        DevelopmentCardDesk developmentDesk = new DevelopmentCardDesk();
        DevelopmentCardFaceUP developmentFaceUp = new DevelopmentCardFaceUP(developmentDesk);

        Player player = new Player();

        while (player.getPoints() < 15) {
            System.out.println("\n==============================");
            System.out.println("BANK: ");
            tb.printBank();
            System.out.println("PLAYER: ");
            player.printStatus();
            System.out.println();
            developmentFaceUp.printMarket();

            System.out.println("\nChoose action:");
            System.out.println("1) Take 3 different color tokens");
            System.out.println("2) Buy a development card");
            System.out.println("3) Quit");
            System.out.print("Your choice: ");
            int choice = safeInt(sc);

            if (choice == 1) {
                takeThreeTokens(sc, tb, player);
            } else if (choice == 2) {
                buyCard(sc, tb, player, developmentFaceUp, developmentDesk);
            } else if (choice == 3) {
                System.out.println("Quit game.");
                break;
            } else {
                System.out.println("Invalid choice.");
            }
        }

        if (player.getPoints() >= 15) {
            System.out.println("\n You reached 15 points! You win!");
        }
    }

//-----------------------------------------------------------------------------------------------------------------    
    // Helper function 1 : For player to take any three tokens but they can own maximum 10 tokens.
    private static void takeThreeTokens(Scanner sc, TokenBank tb, Player player) {

        if (player.totalTokens() + 3 > 10) {
            System.out.println("You cannot take 3 tokens because you would exceed 10 tokens.");
            return;
        }

        System.out.println("Enter 3 DIFFERENT colors (WHITE/BLUE/GREEN/RED/BLACK) separated by spaces:");
        System.out.print("> ");
        String a = sc.next().toUpperCase();
        String b = sc.next().toUpperCase();
        String c = sc.next().toUpperCase();

        // must be different + not GOLD
        HashSet<String> set = new HashSet<>();
        set.add(a);
        set.add(b);
        set.add(c);

        if (set.size() != 3) {
            System.out.println("Must choose 3 different colors.");
            return;
        }
        if (a.equals(TokenBank.GOLD) || b.equals(TokenBank.GOLD) || c.equals(TokenBank.GOLD)) {
            System.out.println("You cannot take GOLD using this action.");
            return;
        }

        // must be valid and tb must have enough
        for (String color : set) {
            if (!isTakeColor(color)) {
                System.out.println("Invalid color: " + color);
                return;
            }
            if (!tb.hasEnough(color, 1)) {
                System.out.println("Bank does not have enough " + color);
                return;
            }
        }

        // perform transfer
        for (String color : set) {
            tb.remove(color, 1);
            player.addTokens(color, 1);
        }

        System.out.println("Tokens taken successfully.");
    }

//------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // Helper function 2 : For player to buy a development card
    private static void buyCard(Scanner sc, TokenBank tb, Player player, DevelopmentCardFaceUP developmentFaceUp, DevelopmentCardDesk developmentDesk) {

        System.out.print("Choose level (1/2/3): ");
        int level = safeInt(sc);

        System.out.print("Choose card index: ");
        int index = safeInt(sc);

        try {
            DevelopmentCard chosen = developmentFaceUp.getCard(level, index);

            if (!PurchaseService.canBuy(player, chosen)) {
                System.out.println("You cannot afford this card.");
                return;
            }

            PurchaseService.buy(player, chosen, tb);
            developmentFaceUp.removeAndRefill(level, index, developmentDesk);

            System.out.println("Bought card: " + chosen);
        } catch (Exception e) {
            System.out.println("Buy failed: " + e.getMessage());
        }
    }

    private static boolean isTakeColor(String c) {
        for (String color : TAKE_COLORS) {
            if (color.equals(c))
                return true;
        }
        return false;
    }

    private static int safeInt(Scanner sc) {
        while (!sc.hasNextInt()) {
            sc.next(); // discard
            System.out.print("Enter a number: ");
        }
        return sc.nextInt();
    }
}