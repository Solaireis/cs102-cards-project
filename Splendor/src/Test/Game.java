package Test;

import Cards.DevelopmentCard.*;
import Cards.Noble.*;
import Cards.Token.*;
import Player.*;
import Properties.Reader;
import java.util.*;

/**
 * The Game class contains the main method and some utility methods
 */

public class Game {

    /**
     * Array of token colors as constants
     */
    public static final String[] TAKE_COLORS = {
            TokenBank.WHITE, TokenBank.BLUE, TokenBank.GREEN, TokenBank.RED, TokenBank.BLACK
    };

    /**
     * Prints menu for player to pick an action for a turn
     */
    public static void choiceMsg(){
        System.out.println("\nChoose action:");
        System.out.println("1) Take 3 different color tokens");
        System.out.println("2) Take 2 same color tokens");
        System.out.println("3) Buy a development card");
        System.out.println("4) Reserve a development card");
        System.out.println("5) Quit");
        System.out.print("Your choice: ");
    }

    /**
     * Prints an intro to the game to let player know the game has started
     */
    public static void playMsg(){
        System.out.println(" ");
        System.out.println("============================================================");
        System.out.println("                     S P L E N D O R                        ");
        System.out.println("------------------------------------------------------------");
        System.out.println("        Trade   •   Build   •   Earn Prestige   •   Win     ");
        System.out.println("============================================================");
        System.out.println("                     G2T4 EDITION                           ");
        System.out.println("============================================================");
    }

    /**
     * Prints a message to let player know they are against another player
     */
    public static void playVsPlayer(){
        playMsg();
        System.out.println("                Playing Against the local Player                ");
        System.out.println("============================================================");
    }

    /**
     * Prints a message to let player know they are against a computer player
     */
    public static void playVsCom(){
        playMsg();
        System.out.println("                Playing Against the Computer                ");
        System.out.println("============================================================");
    }

    /**
     * Main method
     * <p>
     * Gets inputs for winning condition, number of players.
     * Initialize card decks.
     * Run turns for each player until winning condition met or quit.
     * Determine winner
     * @param args
     */
    public static void main(String[] args){
        Scanner sc = new Scanner(System.in);

        //need to change path of file that reader is reading from
        //no need number of players
        int winningCondition = 0;  //default if loading error

        ArrayList<Player> players = new ArrayList<>();

        //list of players
        // numb of players is obtained from properties file
        int numOfPlayers = 0;
        try{
            Reader reader = new Reader(); // Create an instance of Reader
            winningCondition = reader.getPrestigePointToWin();
            numOfPlayers = reader.getNumOfPlayers();
            //Test commands to verify if the properties file works
            // System.out.println(winningCondition);
            // System.out.println(numOfPlayers);

            System.out.println("Config.properties File found!");
            System.out.println("Initializing Game configuration properties");
            System.out.printf("Number of Prestige points to win: %d \n", winningCondition);
            System.out.printf("Number of Players: %d \n", numOfPlayers);

            // Call the method on the instance
        } catch (Exception e){
            System.out.println("Cant find file");
        }
        // If no config properties is found
        if (winningCondition == 0){
            winningCondition = 15;
            System.out.println("File not found.. Default winning Condition set to 15");
        }

        while (numOfPlayers <= 0){
            System.out.print("Enter number of players: ");
            numOfPlayers = InputSafetyChecking.safeInt(sc, "Please enter a valid number: ");
            sc.nextLine();
            if (numOfPlayers <= 0) {
                System.out.println("At least 1 player needed");
                continue;
            }

        }

        if (numOfPlayers > 0){
            for (int i = 1; i <= numOfPlayers; i++) {
                System.out.printf("Player %d Name: ", i);
                String name = sc.nextLine();
                Player player = new Player(name);
                players.add(player);
            }
        }
        if (numOfPlayers < 2) {
            numOfPlayers += 1;

            playVsCom();
            players.add(new Computer());
        } else {
            //Displays the Player v Player print screen instead
            playVsPlayer();

        }

        TokenBank tb = new TokenBank(numOfPlayers);
        DevelopmentCardDeck DevelopmentDeck = new DevelopmentCardDeck();
        DevelopmentCardFaceUP developmentFaceUp = new DevelopmentCardFaceUP(DevelopmentDeck);
        //need to print remaining cards in decks

        NobleDeck nobleDeck = new NobleDeck();
        NobleFaceUP nobleFaceUp = new NobleFaceUP(nobleDeck, numOfPlayers); // change 4 to amt of players later
        NobleAttractService nobleService = new NobleAttractService();

        boolean end = false;
        boolean lastRoundTriggered = false;
        int finalRoundStarterIndex = -1; // Player how first tiggered the final round

        while (!end) {
            for (int i = 0; i < players.size(); i++) {
                Player player = players.get(i);
                if (lastRoundTriggered && i == finalRoundStarterIndex) {
                    end = true;
                    break;
                }
                System.out.println();
                System.out.println();
                System.out.println();
                System.out.println(player.getName().toUpperCase() + "'s turn");
                turnDisplay(players, player, tb, nobleFaceUp, developmentFaceUp);

                if (player instanceof Computer computer) {
                    boolean reached = computer.turnAlgorithm(tb, developmentFaceUp, DevelopmentDeck, winningCondition,
                            nobleFaceUp);
                    if (!lastRoundTriggered && reached) {
                        lastRoundTriggered = true;
                        finalRoundStarterIndex = i;
                    }
                } else {
                    boolean quit = false;
                    boolean valid = false;
                    while (!valid) {
                        choiceMsg();

                        int choice = InputSafetyChecking.readIntInRange(sc, 1, 5, "");
                        System.out.println();

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
                                valid = buyCard(sc, tb, player, developmentFaceUp, DevelopmentDeck);
                                break;

                            case 4:
                                valid = reserveCard(sc, tb, player, developmentFaceUp, DevelopmentDeck);
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
                        handleDiscard(sc, player, tb);
                        awardNobleIfAny(nobleService, player, nobleFaceUp, sc);

                        if (!lastRoundTriggered && reachedWinningCondition(player, winningCondition)) {
                            System.out.println("Player reached winning condition, final round triggered.");
                            lastRoundTriggered = true;
                            finalRoundStarterIndex = i;
                        }
                    }
                }
            }
        }
        determineWinner(players);
    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------------
    /**
     * Displays information about the tokenBank, player status, face up cards and other players' inventories
     * @param players arrayList of all players
     * @param player current player
     * @param tb tokenBank of all tokens not taken
     * @param nobleFaceUp face up noble cards
     * @param developmentFaceUp face up development cards
     */
    private static void turnDisplay(ArrayList<Player> players, Player player, TokenBank tb, NobleFaceUP nobleFaceUp,
            DevelopmentCardFaceUP developmentFaceUp) {
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
    /**
     * Returns true when player does not have more than 10 tokens, else prompts user to discard tokens
     * @param sc Scanner
     * @param player current player
     * @param tb tokenBank
     * @return true
     */
    private static boolean handleDiscard(Scanner sc, Player player, TokenBank tb) {
        while (player.totalTokens() > 10) {
            System.out.println("Total tokens exceeded 10. Pick one color to discard: ");
            String color = sc.nextLine().trim().toUpperCase();
            if (!isTokenColor(color)) {
                System.out.println("Invalid color.");
                continue;
            }
            if (player.getTokens(color) <= 0) {
                System.out.println("You do not have any " + color + " tokens.");
                continue;
            }
            player.removeTokens(color, 1);
            tb.add(color, 1);
        }
        return true;
    }

    // --------------------------------------------------------------------------------------------------------------------
    /**
     * Returns true if player reached the winning condition of prestige points, false otherwise
     * @param player current player
     * @param winningCondition number of prestige points to end the game
     * @return true if player prestige points >= winning condition, false otherwise
     */
    private static boolean reachedWinningCondition(Player player, int winningCondition) {
        return player.getPoints() >= winningCondition;
    }

    //---------------------------------------------------------------------------------------------------------------------
    /**
     * Awards Noble object to player if eligible
     * @param service NobleAttractService
     * @param player Current player
     * @param faceUp Face up noble cards
     * @param sc Scanner
     */
    private static void awardNobleIfAny(NobleAttractService service, Player player, NobleFaceUP faceUp, Scanner sc) {
        Noble gained = service.awardNobleIfPossible(player, faceUp, sc);
        if (gained != null) {
            System.out.println("Noble gained: " + gained);
        }
    }

    //-----------------------------------------------------------------------------------------------------------------
    /**
     * Returns true if action to take 3 tokens valid, else false
     * @param sc Scanner
     * @param tb TokenBank
     * @param player Current player
     * @return true if action valid, false otherwise
     */
    private static boolean takeThreeTokens(Scanner sc, TokenBank tb, Player player) {
        while (true){
            System.out.println();
            System.out.println(
                    "Enter 3 DIFFERENT colors (WHITE/BLUE/GREEN/RED/BLACK) separated by spaces (or type back):");
            System.out.print("> ");
            String input = sc.nextLine();

            if (isBackCommand(input)){
                return false;
            }

            List<String> colors = InputSafetyChecking.parseThreeColorsFlexibly(input, TAKE_COLORS);
            if (colors == null) {
                System.out.println("Please enter valid colors.");
                continue;
            }

            boolean enoughInBank = true;
            for (String color : colors) {
                if (!tb.hasEnough(color, 1)) {
                    System.out.println("Bank does not have enough " + color);
                    enoughInBank = false;
                    break;
                }
            }
            if (!enoughInBank) {
                continue;
            }

            System.out.print("Player has taken ");
            for (String color : colors) {
                tb.remove(color, 1);
                player.addTokens(color, 1);
                System.out.print(color + " ");
            }

            System.out.print("tokens.");
            return true;
        }
    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------------------
    /**
     * Returns true if action to take 2 tokens valid, else false
     * @param sc Scanner
     * @param tb TokenBank
     * @param player Current player
     * @return true if action valid, false otherwise
     */
    private static boolean takeTwoTokens(Scanner sc, TokenBank tb, Player player) {
        String color = "";
        while (true) {
            System.out.println();
            System.out.println("Enter a color (WHITE/BLUE/GREEN/RED/BLACK) or type back:");
            System.out.print("> ");
            color = InputSafetyChecking.normalizeUpper(sc.nextLine());

            if (isBackCommand(color)) {
                return false;
            }

            String matched = InputSafetyChecking.matchColor(color, 0);
            if (matched == null || matched.length() != color.length()) {
                System.out.println("Please enter a valid color.");
                continue;
            }
            if (!tb.hasEnough(color, 4)) {
                System.out.println("Bank does not have at least 4 " + color);
                continue;
            }
            break;
        }

        tb.remove(color, 2);
        player.addTokens(color, 2);

        System.out.println("Player has taken 2 " + color + " tokens");
        return true;

    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------------------
    /**
     * Determines the winner based on prestige points and number of bonuses if a tie results
     * @param players arrayList of all Players
     */
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
    /**
     * Returns true if action to buy development card valid, false otherwise
     * @param sc Scanner
     * @param tb TokenBank
     * @param player Current player
     * @param developmentFaceUp Face up development cards
     * @param developmentDeck Development cards in remaining deck
     * @return true if action valid, false otherwise
     */
    private static boolean buyCard(Scanner sc, TokenBank tb, Player player, DevelopmentCardFaceUP developmentFaceUp,
            DevelopmentCardDeck developmentDeck) {
        while (true) {
            System.out.println();
            System.out.print("Enter location of card (Reserve / Market) or type back: ");
            String location = sc.nextLine().trim().toLowerCase();

            if (isBackCommand(location)) {
                return false;
            }

            DevelopmentCard chosen = null;
            int level = 0;
            int index = 0;

            if (location.equals("reserve")) {
                if (player.totalReserves() == 0) {
                    System.out.println("No cards in reserve. Please choose market.");
                    continue;
                }

                while (true) {
                    Integer reserveIndex = readIntOrBack(sc,
                            "Enter index of card (0-" + (player.totalReserves() - 1) + ") or type back: ");
                    if (reserveIndex == null) {
                        return false;
                    }
                    index = reserveIndex;
                    if (index < 0 || index >= player.totalReserves()) {
                        System.out.println("Please enter a valid reserve index.");
                        continue;
                    }
                    chosen = player.getReserveCard(index);
                    break;
                }
            } else if (location.equals("market")) {
                while (true) {
                    System.out.println();
                    Integer levelInput = readIntOrBack(sc, "Choose level (1/2/3) or type back: ");
                    if (levelInput == null) {
                        return false;
                    }
                    level = levelInput;

                    if (level < 1 || level > 3) {
                        System.out.println("Please enter level 1, 2, or 3.");
                        continue;
                    }

                    Integer cardIndex = readIntOrBack(sc, "Choose card index (or type back): ");
                    if (cardIndex == null) {
                        return false;
                    }
                    index = cardIndex;

                    if (index < 0 || index >= developmentFaceUp.getFaceUp(level).size()) {
                        System.out.println("Please enter a valid card index.");
                        continue;
                    }

                    chosen = developmentFaceUp.getCard(level, index);
                    break;
                }
            } else {
                System.out.println("Invalid location. Please type Reserve or Market.");
                continue;
            }

            if (!PurchaseService.canBuy(player, chosen)) {
                System.out.println("You cannot afford this card. Choose another card.");
                continue;
            }

            try {
                PurchaseService.buy(player, chosen, tb);
                if (location.equals("reserve")) {
                    player.buyReserve(chosen);
                } else {
                    developmentFaceUp.removeAndRefill(level, index, developmentDeck);
                }

                System.out.println("Bought card: " + chosen);
                return true;
            } catch (Exception e) {
                System.out.println("Buy failed: " + e.getMessage());
            }
        }
    }

    //--------------------------------------------------------------------------------------------------------
    /**
     * Returns true if action to reserve card is valid, false otherwise
     * @param sc Scanner
     * @param tb TokenBank
     * @param player Current player
     * @param developmentFaceUp Face up development cards
     * @param DevelopmentDeck Development cards in remaining deck
     * @return true if action valid, false otherwise
     */
    private static boolean reserveCard(Scanner sc, TokenBank tb, Player player, DevelopmentCardFaceUP developmentFaceUp,
            DevelopmentCardDeck DevelopmentDeck) {
        if (player.totalReserves() == 3) {
            System.out.println("Reserve full");
            return false;
        }

        while (true){
            System.out.println();
            System.out.println("Option a: draw first card from any deck");
            System.out.println("Option b: choose a face up card");
            System.out.print("Your Choice (a/b, or type back): ");
            String reserveLine = sc.nextLine().trim().toLowerCase();
            if (isBackCommand(reserveLine)) {
                return false;
            }
            if (reserveLine.isEmpty()) {
                System.out.println("Invalid choice. Please enter a or b.");
                continue;
            }
            char choice = reserveLine.charAt(0);

            DevelopmentCard chosen = null;
            if (choice == 'a') {
                while (true) {
                    System.out.println();
                    Integer levelInput = readIntOrBack(sc, "Enter level (1/2/3) or type back: ");
                    if (levelInput == null) {
                        return false;
                    }
                    int level = levelInput;
                    if (level < 1 || level > 3) {
                        System.out.println("Please enter level 1, 2, or 3.");
                        continue;
                    }

                    if (level == 1 && !DevelopmentDeck.isLevel1Empty()) {
                        chosen = DevelopmentDeck.drawLevel1();
                    } else if (level == 2 && !DevelopmentDeck.isLevel2Empty()) {
                        chosen = DevelopmentDeck.drawLevel2();
                    } else if (level == 3 && !DevelopmentDeck.isLevel3Empty()) {
                        chosen = DevelopmentDeck.drawLevel3();
                    } else {
                        System.out.println("No remaining cards in that level. Choose another level.");
                        continue;
                    }
                    break;
                }
            } else if (choice == 'b') {
                while (true) {
                    System.out.println();
                    Integer levelInput = readIntOrBack(sc, "Choose level (1/2/3) or type back: ");
                    if (levelInput == null) {
                        return false;
                    }
                    int level = levelInput;

                    if (level < 1 || level > 3) {
                        System.out.println("Please enter level 1, 2, or 3.");
                        continue;
                    }

                    Integer indexInput = readIntOrBack(sc, "Choose card index (or type back): ");
                    if (indexInput == null) {
                        return false;
                    }
                    int index = indexInput;

                    if (index < 0 || index >= developmentFaceUp.getFaceUp(level).size()) {
                        System.out.println("Please enter a valid card index.");
                        continue;
                    }

                    chosen = developmentFaceUp.getCard(level, index);
                    developmentFaceUp.removeAndRefill(level, index, DevelopmentDeck);
                    break;
                }
            } else {
                System.out.println("Invalid choice. Please enter a or b.");
                continue;
            }

            player.addReserve(chosen);
            if (tb.get(TokenBank.GOLD) > 0) {
                tb.remove(TokenBank.GOLD, 1);
                player.addTokens(TokenBank.GOLD, 1);
                System.out.println("Gold token added to inventory");
            } else {
                System.out.println("No remaining gold tokens");
            }

            System.out.println("Card reserved successfully");
            return true;
        }
    }

    // --------------------------------------------------------------------------------------------------------
    /**
     * Returns true if input color is a token color, false otherwise
     * Checks for invalid token color inputs
     * @param c Input color
     * @return true if color valid, false otherwise
     */
    private static boolean isTokenColor(String c) {
        return TokenBank.WHITE.equals(c)
                || TokenBank.BLUE.equals(c)
                || TokenBank.GREEN.equals(c)
                || TokenBank.RED.equals(c)
                || TokenBank.BLACK.equals(c)
                || TokenBank.GOLD.equals(c);
    }

    /**
     * Returns true if input command == back, false otherwise
     * @param input Input command
     * @return true if command is back, false otherwise
     */
    private static boolean isBackCommand(String input) {
        return input != null && input.trim().equalsIgnoreCase("back");
    }

    /**
     * Returns number if input is valid number and null if command is back
     * @param sc Scanner
     * @param prompt Prompt for input
     * @return number if input is valid number, null if command is back
     */
    private static Integer readIntOrBack(Scanner sc, String prompt) {
        while (true) {
            if (prompt != null && !prompt.isBlank()) {
                System.out.print(prompt);
            }

            String line = sc.nextLine().trim();
            if (isBackCommand(line)) {
                return null;
            }

            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number or type back.");
                System.out.println();
            }
        }
    }
}