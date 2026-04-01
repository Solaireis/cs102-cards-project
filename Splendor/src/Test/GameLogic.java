package Test;

import Cards.DevelopmentCard.*;
import Cards.Noble.*;
import Cards.Token.*;
import Player.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class GameLogic {

    public static final String[] TAKE_COLORS = {
        TokenBank.WHITE, TokenBank.BLUE, TokenBank.GREEN, TokenBank.RED, TokenBank.BLACK
    };

    private final int winningCondition;
    private final ArrayList<Player> players;

    private final TokenBank tokenBank;
    private final DevelopmentCardDeck developmentDeck;
    private final DevelopmentCardFaceUP developmentFaceUp;
    private final NobleDeck nobleDeck;
    private final NobleFaceUP nobleFaceUp;

    private final NobleService nobleService;
    private ArrayList<Noble> pendingNobleChoices = new ArrayList<>();
    private boolean waitingForNobleChoice = false;

    private int currentPlayerIndex = 0;
    private int turnNumber = 1;
    private boolean gameOver = false;
    private boolean lastRoundTriggered = false;

    public GameLogic(List<String> playerNames, int winningCondition) {
        this.winningCondition = winningCondition;
        this.players = new ArrayList<>();

        for (String name : playerNames) {
            players.add(new Player(name));
        }

        // keep your original behavior if only 1 human player
        if (players.size() < 2) {
            players.add(new Computer());
        }

        int numOfPlayers = players.size();

        this.tokenBank = new TokenBank(numOfPlayers);
        this.developmentDeck = new DevelopmentCardDeck();
        this.developmentFaceUp = new DevelopmentCardFaceUP(developmentDeck);
        this.nobleDeck = new NobleDeck();
        this.nobleFaceUp = new NobleFaceUP(nobleDeck, numOfPlayers);
        this.nobleService = new NobleService();
    }

    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    public int getTurnNumber() {
        return turnNumber;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public TokenBank getTokenBank() {
        return tokenBank;
    }

    public DevelopmentCardFaceUP getDevelopmentFaceUp() {
        return developmentFaceUp;
    }

    public NobleFaceUP getNobleFaceUp() {
        return nobleFaceUp;
    }

    public ArrayList<Noble> getPendingNobleChoices() {
        return pendingNobleChoices;
    }

    public boolean isWaitingForNobleChoice() {
        return waitingForNobleChoice;
    }
    
    public boolean isGameOver() {
        return gameOver;
    }

    public boolean isLastRoundTriggered() {
        return lastRoundTriggered;
    }

    public MoveResult takeThreeTokens(String a, String b, String c) {
        Player player = getCurrentPlayer();

        a = a.toUpperCase();
        b = b.toUpperCase();
        c = c.toUpperCase();

        if (player.totalTokens() + 3 > 10) {
            return MoveResult.fail("You cannot take 3 tokens because you would exceed 10 tokens.");
        }

        HashSet<String> set = new HashSet<>();
        set.add(a);
        set.add(b);
        set.add(c);

        if (set.size() != 3) {
            return MoveResult.fail("Must choose 3 different colors.");
        }

        if (a.equals(TokenBank.GOLD) || b.equals(TokenBank.GOLD) || c.equals(TokenBank.GOLD)) {
            return MoveResult.fail("You cannot take GOLD using this action.");
        }

        for (String color : set) {
            if (!isTakeColor(color)) {
                return MoveResult.fail("Invalid color: " + color);
            }
            if (!tokenBank.hasEnough(color, 1)) {
                return MoveResult.fail("Bank does not have enough " + color);
            }
        }

        for (String color : set) {
            tokenBank.remove(color, 1);
            player.addTokens(color, 1);
        }

        return MoveResult.success("Tokens taken successfully.");
    }

    
    public MoveResult takeTwoTokens(String color) {
        Player player = getCurrentPlayer();
        color = color.toUpperCase();

        if (player.totalTokens() + 2 > 10) {
            return MoveResult.fail("You cannot take 2 tokens because you would exceed 10 tokens.");
        }

        if (color.equals(TokenBank.GOLD)) {
            return MoveResult.fail("You cannot take GOLD using this action.");
        }

        if (!isTakeColor(color)) {
            return MoveResult.fail("Invalid color: " + color);
        }

        if (!tokenBank.hasEnough(color, 4)) {
            return MoveResult.fail("Bank does not have at least 4 " + color);
        }

        tokenBank.remove(color, 2);
        player.addTokens(color, 2);

        return MoveResult.success("Player has taken 2 " + color + " tokens.");
    }

    public MoveResult buyMarketCard(int level, int index) {
        Player player = getCurrentPlayer();

        try {
            DevelopmentCard chosen = developmentFaceUp.getCard(level, index);

            if (!PurchaseService.canBuy(player, chosen)) {
                return MoveResult.fail("You cannot afford this card.");
            }

            PurchaseService.buy(player, chosen, tokenBank);
            developmentFaceUp.removeAndRefill(level, index, developmentDeck);

            return MoveResult.success("Bought card: " + chosen);
        } catch (Exception e) {
            return MoveResult.fail("Buy failed: " + e.getMessage());
        }
    }

    public MoveResult buyReservedCard(int reserveIndex) {
        Player player = getCurrentPlayer();

        if (player.totalReserves() == 0) {
            return MoveResult.fail("No cards in reserve.");
        }

        try {
            DevelopmentCard chosen = player.getReserveCard(reserveIndex);

            if (!PurchaseService.canBuy(player, chosen)) {
                return MoveResult.fail("You cannot afford this card.");
            }

            PurchaseService.buy(player, chosen, tokenBank);
            player.buyReserve(chosen);

            return MoveResult.success("Bought reserved card: " + chosen);
        } catch (Exception e) {
            return MoveResult.fail("Buy failed: " + e.getMessage());
        }
    }

    public MoveResult reserveTopDeckCard(int level) {
        Player player = getCurrentPlayer();

        if (player.totalReserves() == 3) {
            return MoveResult.fail("Reserve full.");
        }

        DevelopmentCard chosen;

        switch (level) {
            case 1:
                if (developmentDeck.isLevel1Empty()) {
                    return MoveResult.fail("No remaining cards in level 1.");
                }
                chosen = developmentDeck.drawLevel1();
                break;
            case 2:
                if (developmentDeck.isLevel2Empty()) {
                    return MoveResult.fail("No remaining cards in level 2.");
                }
                chosen = developmentDeck.drawLevel2();
                break;
            case 3:
                if (developmentDeck.isLevel3Empty()) {
                    return MoveResult.fail("No remaining cards in level 3.");
                }
                chosen = developmentDeck.drawLevel3();
                break;
            default:
                return MoveResult.fail("Invalid level.");
        }

        player.addReserve(chosen);
        //giveGoldIfAvailable(player);

        return MoveResult.success("Card reserved successfully.");
    }

    public MoveResult reserveFaceUpCard(int level, int index) {
        Player player = getCurrentPlayer();

        if (player.totalReserves() == 3) {
            return MoveResult.fail("Reserve full.");
        }

        try {
            DevelopmentCard chosen = developmentFaceUp.getCard(level, index);
            developmentFaceUp.removeAndRefill(level, index, developmentDeck);
            player.addReserve(chosen);
            //giveGoldIfAvailable(player);

            return MoveResult.success("Card reserved successfully.");
        } catch (Exception e) {
            return MoveResult.fail(e.getMessage());
        }
    }

    public MoveResult discardToken(String color) {
        Player player = getCurrentPlayer();
        color = color.toUpperCase();

        try {
            player.removeTokens(color, 1);
            tokenBank.add(color, 1);
            return MoveResult.success("Discarded 1 " + color + ".");
        } catch (Exception e) {
            return MoveResult.fail("Cannot discard " + color + ".");
        }
    }

    public boolean needsDiscard() {
        return getCurrentPlayer().totalTokens() > 10;
    }


    public MoveResult endTurn() {
        // Guard against being called when player hasn't picked yet
        if (waitingForNobleChoice) {
            return MoveResult.fail("Player must choose a noble first.");
        }

        Player player = getCurrentPlayer();

        if (player.totalTokens() > 10) {
            return MoveResult.fail("Player must discard down to 10 tokens first.");
        }

        boolean needsChoice = awardNobleIfAny(player);

        if (needsChoice) {
            return MoveResult.success("Player must choose one noble.");
        }

        return finishTurn(player);
    }


    public List<Player> determineWinners() {
        ArrayList<Player> winner = new ArrayList<>();

        for (Player player : players) {
            if (winner.isEmpty() || player.getPoints() > winner.get(0).getPoints()) {
                winner.clear();
                winner.add(player);
            } else if (player.getPoints() == winner.get(0).getPoints()) {
                if (player.totalDevelopmentCards() < winner.get(0).totalDevelopmentCards()) {
                    winner.clear();
                    winner.add(player);
                } else if (player.totalDevelopmentCards() == winner.get(0).totalDevelopmentCards()) {
                    winner.add(player);
                }
            }
        }

        return winner;
    }

    private boolean awardNobleIfAny(Player player) {
        ArrayList<Noble> eligible = nobleService.getEligibleNobles(player, nobleFaceUp);

        if (eligible.isEmpty()) {
            waitingForNobleChoice = false;
            pendingNobleChoices.clear();
            return false;
        }

        if (eligible.size() == 1) {
            Noble awarded = nobleService.awardChosenNoble(player, nobleFaceUp, eligible.get(0));
            waitingForNobleChoice = false;
            pendingNobleChoices.clear();
            System.out.println(player.getName() + " attracted noble: " + awarded);
            return false;
        }

        pendingNobleChoices = new ArrayList<>(eligible);
        waitingForNobleChoice = true;
        return true;
    }

    public MoveResult chooseNoble(int index) {
        if (!waitingForNobleChoice) {
            return MoveResult.fail("No noble choice is pending.");
        }

        if (index < 0 || index >= pendingNobleChoices.size()) {
            return MoveResult.fail("Invalid noble choice.");
        }

        Player player = getCurrentPlayer();
        Noble chosen = pendingNobleChoices.get(index);

        nobleService.awardChosenNoble(player, nobleFaceUp, chosen);

        waitingForNobleChoice = false;
        pendingNobleChoices.clear();

        System.out.println(player.getName() + " attracted noble: " + chosen);

        return finishTurn(player);
    }

    private MoveResult finishTurn(Player player) {
        if (player.getPoints() >= winningCondition) {
            lastRoundTriggered = true;
            gameOver = true;
            return MoveResult.success("Player reached winning condition.");
        }


        // increment turn number once 1 cycle of all the players are done
        if (currentPlayerIndex == players.size() - 1) {
            turnNumber++;
        }

        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        return MoveResult.success("Turn ended.");
    }

    public MoveResult takeGold() {
        Player currentPlayer = players.get(currentPlayerIndex);

        if (currentPlayer.totalTokens() + 1 > 10) {
            return MoveResult.fail("You cannot take gold because you would exceed 10 tokens.");
        }

        return giveGoldIfAvailable(currentPlayer);
    } 


    private MoveResult giveGoldIfAvailable(Player player) {
        if (tokenBank.get(TokenBank.GOLD) > 0) {
            tokenBank.remove(TokenBank.GOLD, 1);
            player.addTokens(TokenBank.GOLD, 1);
            return MoveResult.success("Gold token taken");
        }
        return MoveResult.fail("No more gold");
    }



    private boolean isTakeColor(String c) {
        for (String color : TAKE_COLORS) {
            if (color.equals(c)) return true;
        }
        return false;
    }



}
