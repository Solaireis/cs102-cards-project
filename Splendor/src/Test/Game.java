package Test;

import Cards.DevelopmentCard.*;
import Cards.Noble.*;
import Cards.Token.*;
import Player.*;
import Properties.Reader;
import java.util.*;

public class Game {

    public static final String[] TAKE_COLORS = {
            TokenBank.WHITE, TokenBank.BLUE, TokenBank.GREEN, TokenBank.RED, TokenBank.BLACK
    };

  
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        //need to change path of file that reader is reading from
        //no need number of players
        int winningCondition = 15;  //default if loading error
        try{
            Reader reader = new Reader(); // Create an instance of Reader
            winningCondition = reader.getPrestigePointToWin(); 
            
            // Call the method on the instance
        } catch ( Exception e){
                System.out.println("Cant find file man");
        }


        ArrayList<Player> players = new ArrayList<>();

        //list of players
        int numOfPlayers = 0;
        while (numOfPlayers == 0) {
            System.out.print("Enter number of players: ");
            numOfPlayers = sc.nextInt();
            sc.nextLine();
            if (numOfPlayers == 0) {
                System.out.println("At least 1 player needed");
                continue;
            }
            for (int i = 1; i <= numOfPlayers; i++) {
                System.out.printf("Player %d Name: ", i);
                String name = sc.nextLine();
                Player player = new Player(name);
                players.add(player);
            }
        }
        if (numOfPlayers < 2) {
            numOfPlayers += 1;
            players.add(new Computer());
        }
        
        
        TokenBank tb = new TokenBank(numOfPlayers);
        DevelopmentCardDeck developmentDesk = new DevelopmentCardDeck();
        DevelopmentCardFaceUP developmentFaceUp = new DevelopmentCardFaceUP(developmentDesk);
        //need to print remaining cards in decks

        NobleDeck nobleDeck = new NobleDeck();  
        NobleFaceUP nobleFaceUp = new NobleFaceUP(nobleDeck, numOfPlayers);    // change 4 to amt of players later           
        NobleAttractService nobleService = new NobleAttractService();

        boolean end = false;
        while (!end) {
            for (Player player : players) {
                
                System.out.println();
                System.out.println();
                System.out.println();
                System.out.println(player.getName().toUpperCase() + "'s turn");
                turnDisplay(players, player, tb, nobleDeck, nobleFaceUp, developmentFaceUp);

                if (player instanceof Computer computer) {
                    end = computer.turnAlgorithm(tb, developmentFaceUp, developmentDesk, winningCondition, nobleDeck);
                } else {
                    boolean quit = false;
                    boolean valid = false;
                    while (!valid) {
                        System.out.println("\nChoose action:");
                        System.out.println("1) Take 3 different color tokens");
                        System.out.println("2) Take 2 same color tokens");
                        System.out.println("3) Buy a development card");
                        System.out.println("4) Reserve a development card");
                        System.out.println("5) Quit");
                        System.out.print("Your choice: ");
                        int choice = sc.nextInt();
                        sc.nextLine();

                        switch (choice) {
                            default:
                                System.out.println("Invalid choice.");
                                break;

                            case 1:
                                valid = takeThreeTokens(sc, tb, player);
                                break;

                            case 2:
                                valid = takeTwoTokens(sc, tb, player);
                                break;

                            case 3:
                                valid = buyCard(sc, tb, player, developmentFaceUp, developmentDesk);
                                break;
                        
                            case 4:
                                valid = reserveCard(sc, tb, player, developmentFaceUp, developmentDesk);
                                break;

                            case 5:
                                System.out.println("Confirm quit? Y/N: ");
                                String answer = sc.nextLine().toUpperCase();
                                if (answer.equals("Y")) {
                                    System.out.println("Quit game.");
                                    end = true;
                                    quit = true;
                                    valid = true;
                                } 
                            }
                    }

                    if (quit) {
                        break;
                    }

                    if (valid) {
                        end = endTurn(sc, player, winningCondition, tb);
                        awardNobleIfAny(nobleService, player, nobleDeck, sc);
                    }
                }
            }
        }
        determineWinner(players);
    }

        //-----------------------------------------------------------------------------------------------------------------
        private static void turnDisplay(ArrayList<Player> players, Player player, TokenBank tb, NobleDeck nobleDeck, NobleFaceUP nobleFaceUp, DevelopmentCardFaceUP developmentFaceUp) {
            System.out.println("\n==============================");
            System.out.println("BANK: ");
            tb.printBank();

            System.out.println();
            System.out.println("PLAYER: ");
            player.printStatus();

            System.out.println();
            nobleFaceUp.printMarket();

            System.out.println();
            developmentFaceUp.printMarket();

            System.out.println("All Player's Inventories:");
            for (Player p : players) {
                System.out.println();
                System.out.println(p.getName());
                p.printStatus();
            }
        }

        //-----------------------------------------------------------------------------------------------------------------
        private static boolean endTurn(Scanner sc, Player player, int winningCondition, TokenBank tb) {
            while (player.totalTokens() > 10) {
                System.out.println("Total tokens exceeded 10. Pick one color to discard: ");
                String color = sc.nextLine();
                player.removeTokens(color, 1);
                tb.add(color, 1);
            }
            if (player.getPoints() >= winningCondition) {
                System.out.println("Player reached winning condition, last turn");
                return true;
            }
            return false;
        }

        //-----------------------------------------------------------------------------------------------------------------
        private static void awardNobleIfAny(NobleAttractService service, Player player, NobleDeck deck, Scanner sc) {
            Noble gained = service.awardNobleIfPossible(player, deck, sc);
            if (gained != null) {
                System.out.println("Noble gained: " + gained);
            }
        }

        //-----------------------------------------------------------------------------------------------------------------
        private static boolean takeThreeTokens(Scanner sc, TokenBank tb, Player player) {

            if (player.totalTokens() + 3 > 10) {
                System.out.println("You cannot take 3 tokens because you would exceed 10 tokens.");
                return false;
            }

            System.out.println("Enter 3 DIFFERENT colors (WHITE/BLUE/GREEN/RED/BLACK) separated by spaces:");
            System.out.print("> ");
            String a = sc.next().toUpperCase();
            String b = sc.next().toUpperCase();
            String c = sc.next().toUpperCase();

            HashSet<String> set = new HashSet<>();
            set.add(a);
            set.add(b);
            set.add(c);

            if (set.size() != 3) {
                System.out.println("Must choose 3 different colors.");
                return false;
            }
            if (a.equals(TokenBank.GOLD) || b.equals(TokenBank.GOLD) || c.equals(TokenBank.GOLD)) {
                System.out.println("You cannot take GOLD using this action.");
                return false;
            }

            for (String color : set) {
                if (!isTakeColor(color)) {
                    System.out.println("Invalid color: " + color);
                    return false;
                }
                if (!tb.hasEnough(color, 1)) {
                    System.out.println("Bank does not have enough " + color);
                    return false;
                }
            }

            System.out.print("Player has taken ");
            for (String color : set) {
                tb.remove(color, 1);
                player.addTokens(color, 1);
                System.out.print(color + " ");
            }

            System.out.print("tokens.");
            return true;
        }
    
    //------------------------------------------------------------------------------------------------------------------------------------------------------------------
    private static boolean takeTwoTokens(Scanner sc, TokenBank tb, Player player) {
        if (player.totalTokens() + 2 > 10) {
            System.out.println("You cannot take 2 tokens because you would exceed 10 tokens.");
            return false;
        }

        System.out.println("Enter a color (WHITE/BLUE/GREEN/RED/BLACK):");
        System.out.print("> ");
        String color = sc.nextLine().toUpperCase();

        if (color.equals(TokenBank.GOLD)) {
            System.out.println("You cannot take GOLD using this action.");
            return false;
        }

        if (!isTakeColor(color)) {
            System.out.println("Invalid color: " + color);
            return false;
        }

        if (!tb.hasEnough(color, 4)) {
            System.out.println("Bank does not have at least 4 " + color);
            return false;
        }
        
        tb.remove(color, 2);
        player.addTokens(color, 2);
        

        System.out.println("Player has taken 2 " + color + " tokens");
        return true;


    }
    //------------------------------------------------------------------------------------------------------------------------------------------------------------------
    private static void determineWinner(ArrayList<Player> players) {
        ArrayList<Player> winner = new ArrayList<>();
        for (Player player : players) {
            if (winner.size() == 0 || player.getPoints() > winner.get(0).getPoints()) {
                winner = new ArrayList<>();
                winner.add(player);
            } else if (player.getPoints() == winner.get(0).getPoints()) {
                if (player.totalDevelopmentCards() < winner.get(0).totalDevelopmentCards()) {
                    winner = new ArrayList<>();
                    winner.add(player);
                } else if (player.totalDevelopmentCards() == winner.get(0).totalDevelopmentCards()) {
                    winner.add(player);
                }
            }
        }
        if (winner.size() == 1) {
            System.out.println("The winner is " + winner.get(0).getName() + "!");
        } else {
            System.out.print("The winners are ");
            for (Player p : winner) {
                System.out.print("<" + p.getName() + ">");
            }
            System.out.print("!");
        }
        
    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------------------
    private static boolean buyCard(Scanner sc, TokenBank tb, Player player, DevelopmentCardFaceUP developmentFaceUp, DevelopmentCardDeck developmentDesk) {
        System.out.print("Enter location of card (Reserve / Market): ");
        String location = sc.nextLine().toLowerCase();

        DevelopmentCard chosen = null;
        int level = 0;
        int index = 0;
        if (location.equals("reserve")) {
            if (player.totalReserves() == 0) {
                System.out.println("No cards in reserve");
                return false;
            }

            System.out.print("Enter index of card (0-2): ");
            index = sc.nextInt();
            sc.nextLine();
            try {
                chosen = player.getReserveCard(index);
            } catch (Exception e) {
                System.out.println("card does not exist at index " + index);
                return false;
            }
        } else if (location.equals("market")) {
            try {
                System.out.print("Choose level (1/2/3): ");
                level = safeInt(sc);

                System.out.print("Choose card index: ");
                index = safeInt(sc);
                
                chosen = developmentFaceUp.getCard(level, index);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                return false;
            }
            
        } else {
            System.out.println("Invalid location");
            return false;
        }
        

        try {
            if (!PurchaseService.canBuy(player, chosen)) {
                System.out.println("You cannot afford this card.");
                return false;
            }

            PurchaseService.buy(player, chosen, tb);
            if (location.equals("Reserve")) {
                player.buyReserve(chosen);
            } else {
                developmentFaceUp.removeAndRefill(level, index, developmentDesk);
            }

            System.out.println("Bought card: " + chosen);
            return true;
        } catch (Exception e) {
            System.out.println("Buy failed: " + e.getMessage());
            return false;
        }
    }

    //--------------------------------------------------------------------------------------------------------
    private static boolean reserveCard(Scanner sc, TokenBank tb, Player player, DevelopmentCardFaceUP developmentFaceUp, DevelopmentCardDeck developmentDesk) {
        if (player.totalReserves() == 3) {
            System.out.println("Reserve full");
            return false;
        }

        System.out.println("Option a: draw first card from any deck");
        System.out.println("Option b: choose a face up card");
        System.out.print("Your Choice: ");
        char choice = sc.nextLine().charAt(0);

        DevelopmentCard chosen = null;
        if (choice == 'a') {
            System.out.print("enter level: ");
            int level = sc.nextInt();
            sc.nextLine();
            switch (level) {
                default:
                    System.out.println("invalid level");
                    return false;
                case 1:
                    if (!developmentDesk.isLevel1Empty()) {
                        chosen = developmentDesk.drawLevel1();
                    } else {
                        System.out.println("no remaining cards in level 1");
                        return false;
                    }
                    break;
                case 2:
                    if (!developmentDesk.isLevel2Empty()) {
                        chosen = developmentDesk.drawLevel2();
                    } else {
                        System.out.println("no remaining cards in level 2");
                        return false;
                    }
                    break;
                case 3:
                    if (!developmentDesk.isLevel3Empty()) {
                        chosen = developmentDesk.drawLevel3();
                    } else {
                        System.out.println("no remaining cards in level 3");
                        return false;
                    }
            }
        } else if (choice == 'b') {
            int level = 0;
            int index = 0;
            try {
                System.out.print("Choose level (1/2/3): ");
                level = safeInt(sc);

                System.out.print("Choose card index: ");
                index = safeInt(sc);
                
                chosen = developmentFaceUp.getCard(level, index);
                developmentFaceUp.removeAndRefill(level, index, developmentDesk);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                return false;
            }

            
        } else {    
            System.out.println("invalid choice");
            return false;
        }

        player.addReserve(chosen);
        if (tb.get("GOLD") > 0) {
            tb.remove("GOLD", 1);
            player.addTokens("GOLD", 1);
            System.out.println("Gold token added to inventory");
        } else {
            System.out.println("No remaining gold tokens");
        }

        System.out.println("Card reserved successfully");
        return true;
    }


    //--------------------------------------------------------------------------------------------------------
    private static boolean isTakeColor(String c) {
        for (String color : TAKE_COLORS) {
            if (color.equals(c)) return true;
        }
        return false;
    }

    private static int safeInt(Scanner sc) {
        while (!sc.hasNextInt()) {
            sc.next();
            System.out.print("Enter a number: ");
        }
        return sc.nextInt();
    }
}