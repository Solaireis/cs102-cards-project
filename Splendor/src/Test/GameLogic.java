// AI-assisted: Parts of this class, including token-taking, discard-mode handling,
// noble selection flow, and final-round/end-turn logic, were developed with help from ChatGPT-5 referencing Game.java and some other partial classes.
// The team reviewed, tested, and modified the final implementation.
package Test;

import Cards.DevelopmentCard.*;
import Cards.Noble.*;
import Cards.Token.*;
import Player.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Handles the main game rules and turn flow for game.
 * This class manages players, turn order, player actions, noble attraction,
 * and the winning and final-round logic.
 */
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
    private boolean discardMode = false;                                                    //tracks whether current player is forced to discard tokens (>10 tokens)
    private int tokensToDiscard = 0;                                                        //stores how many more tokens player still needs to discard


    /**
     * Creates a new game with the given players and winning condition.
     * If fewer than 2 players are provided, a computer player is added automatically.
     *
     * @param playerNames the names of the players in the game
     * @param winningCondition the number of points needed to trigger the final round
     */
    public GameLogic(List<String> playerNames, int winningCondition) {
        this.winningCondition = winningCondition;
        this.players = new ArrayList<>();

        for (String name : playerNames) {
            players.add(new Player(name));
        }

        // Add computer player if only 1 player
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

    /**
     * Returns the player whose turn it currently is.
     *
     * @return the current player
     */
    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    /**
     * Returns the current turn number.
     *
     * @return the turn number
     */
    public int getTurnNumber() {
        return turnNumber;
    }

    /**
     * Returns all players in the game.
     *
     * @return the list of players
     */
    public ArrayList<Player> getPlayers() {
        return players;
    }

    /**
     * Returns the token bank used in this game.
     *
     * @return the token bank
     */   
    public TokenBank getTokenBank() {
        return tokenBank;
    }

    /**
     * Returns the face-up development cards currently available.
     *
     * @return the face-up development cards
     */    
    public DevelopmentCardFaceUP getDevelopmentFaceUp() {
        return developmentFaceUp;
    }

    /**
     * Returns the face-up nobles currently available.
     *
     * @return the face-up nobles
     */    
    public NobleFaceUP getNobleFaceUp() {
        return nobleFaceUp;
    }

    /**
     * Returns the list of nobles the current player may choose from.
     *
     * @return the pending noble choices
     */   
    public ArrayList<Noble> getPendingNobleChoices() {
        return pendingNobleChoices;
    }

    /**
     * Returns whether the current player must choose a noble before ending their turn.
     *
     * @return true if a noble choice is pending, false otherwise
     */    
    public boolean isWaitingForNobleChoice() {
        return waitingForNobleChoice;
    }

    /**
     * Returns whether the game is over.
     *
     * @return true if the game has ended, false otherwise
     */    
    public boolean isGameOver() {
        return gameOver;
    }

    /**
     * Returns whether the final round has been triggered.
     *
     * @return true if the final round has started, false otherwise
     */    
    public boolean isLastRoundTriggered() {
        return lastRoundTriggered;
    }

    /**
     * Lets the current player take three different non-gold tokens.
     *
     * @param a the first token color
     * @param b the second token color
     * @param c the third token color
     * @return the result of the move
     */
    public MoveResult takeThreeTokens(String a, String b, String c) {
        if (discardMode) {
            return MoveResult.fail("You must discard tokens first.");
        }

        Player player = getCurrentPlayer();

        a = a.toUpperCase();
        b = b.toUpperCase();
        c = c.toUpperCase();

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

        return checkDiscardAfterTake(
            player.getName() + " took 3 tokens: " + a + ", " + b + ", " + c + "."
        );
    }

    /**
     * Lets the current player take two tokens of the same color.
     * This is only allowed if the bank has at least 4 of that color.
     *
     * @param color the token color to take
     * @return the result of the move
     */    
    public MoveResult takeTwoTokens(String color) {
        if (discardMode) {
            return MoveResult.fail("You must discard tokens first.");
        }

        Player player = getCurrentPlayer();
        color = color.toUpperCase();

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

        return checkDiscardAfterTake(
            player.getName() + " has taken 2 " + color + " tokens."
        );
    }

    /**
     * Lets the current player buy a face-up development card from the market.
     *
     * @param level the card tier level
     * @param index the position of the card in that tier
     * @return the result of the move
     */
    public MoveResult buyMarketCard(int level, int index) {
        if (discardMode) {
            return MoveResult.fail("You must discard tokens first.");
        }

        Player player = getCurrentPlayer();

        try {
            DevelopmentCard chosen = developmentFaceUp.getCard(level, index);

            if (!PurchaseService.canBuy(player, chosen)) {
                return MoveResult.fail("You cannot afford this card.");
            }

            PurchaseService.buy(player, chosen, tokenBank);
            developmentFaceUp.removeAndRefill(level, index, developmentDeck);

            return MoveResult.success(player.getName() + " bought card: " + chosen);
        } catch (Exception e) {
            return MoveResult.fail("Buy failed: " + e.getMessage());
        }
    }

    /**
     * Lets the current player buy one of their reserved cards.
     *
     * @param reserveIndex the index of the reserved card
     * @return the result of the move
     */
    public MoveResult buyReservedCard(int reserveIndex) {
        if (discardMode) {
            return MoveResult.fail("You must discard tokens first.");
        }

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

            return MoveResult.success(player.getName() + " bought card: " + chosen);
        } catch (Exception e) {
            return MoveResult.fail("Buy failed: " + e.getMessage());
        }
    }

    /**
     * Lets the current player reserve the top card from a development deck.
     *
     * @param level the deck level to reserve from
     * @return the result of the move
     */
    public MoveResult reserveTopDeckCard(int level) {
        if (discardMode) {
            return MoveResult.fail("You must discard tokens first.");
        }

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

        return MoveResult.success("Card reserved successfully.");
    }

    /**
     * Lets the current player reserve a face-up development card.
     *
     * @param level the card tier level
     * @param index the position of the card in that tier
     * @return the result of the move
     */
    public MoveResult reserveFaceUpCard(int level, int index) {
        if (discardMode) {
            return MoveResult.fail("You must discard tokens first.");
        }

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


    /**
     * Checks whether the current player has more than 10 tokens and must discard.
     *
     * @return true if the player must discard, false otherwise
     */
    public boolean needsDiscard() {
        return getCurrentPlayer().totalTokens() > 10;
    }

    /**
     * Ends the current player's turn if all end-of-turn conditions are satisfied.
     * A player cannot end their turn while they still need to choose a noble
     * or while they have more than 10 tokens.
     *
     * @return the result of ending the turn
     */    
    public MoveResult endTurn() {
        if (discardMode) {
            return MoveResult.fail("You must finish discarding first.");
        }

        if (waitingForNobleChoice) {
            return MoveResult.fail("Player must choose a noble first.");
        }

        Player player = getCurrentPlayer();

        if (player.totalTokens() > 10) {
            return MoveResult.fail("Player must discard down to 10 tokens first.");
        }

        String nobleMessage = awardNobleIfAny(player);

        if (waitingForNobleChoice) {
            return MoveResult.success("Player must choose one noble.");
        }

        MoveResult finishResult = finishTurn(player);

        if (nobleMessage != null) {
            return MoveResult.success(nobleMessage + " " + finishResult.getMessage());
        }

        return finishResult;
    }

    /**
     * Determines the winner or winners of the game.
     * The player with the most points wins. If players are tied on points,
     * the one with fewer development cards wins. If still tied, all tied players are returned.
     *
     * @return the list of winners
     */
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

    /**
     * Awards a noble to the player if they are eligible.
     * If the player qualifies for only one noble, it is awarded automatically.
     * If they qualify for multiple nobles, they must choose one.
     *
     * @param player the player being checked
     * @return a message describing the awarded noble, or null if none was awarded yet
     */
    private String awardNobleIfAny(Player player) {
        ArrayList<Noble> eligible = nobleService.getEligibleNobles(player, nobleFaceUp);

        if (eligible.isEmpty()) {
            waitingForNobleChoice = false;
            pendingNobleChoices.clear();
            return null;
        }

        if (eligible.size() == 1) {
            Noble awarded = nobleService.awardChosenNoble(player, nobleFaceUp, eligible.get(0));
            waitingForNobleChoice = false;
            pendingNobleChoices.clear();
            return player.getName() + " attracted noble: " + awarded;
        }

        pendingNobleChoices = new ArrayList<>(eligible);
        waitingForNobleChoice = true;
        return null;
    }

    /**
     * Lets the current player choose one noble from the pending noble choices.
     *
     * @param index the index of the noble to choose
     * @return the result of the move
     */
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

        MoveResult finishResult = finishTurn(player);
        return MoveResult.success(player.getName() + " attracted noble: " + chosen + " " + finishResult.getMessage());
    }

    /**
     * Finishes the current player's turn and advances the game state.
     * This includes triggering the final round, moving to the next player,
     * and ending the game once the final round is complete.
     *
     * @param player the player whose turn is ending
     * @return the result of finishing the turn
     */
    private MoveResult finishTurn(Player player) {
        boolean reachedWinningCondition = player.getPoints() >= winningCondition;

        // triger final round once a player reaches winning condition
        if (reachedWinningCondition && !lastRoundTriggered) {
            lastRoundTriggered = true;
        }

        boolean endOfRound = (currentPlayerIndex == players.size() - 1);

        // game ends after the full round is completed (all players had the same # of turns)
        if (endOfRound) {
            if (lastRoundTriggered) {
                gameOver = true;
                return MoveResult.success("Final round complete.");
            }

            turnNumber++;
        }

        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();

        if (reachedWinningCondition) {
            return MoveResult.success("Final round triggered. Finish the current round.");
        }

        return MoveResult.success("Turn ended.");
    }

    /**
     * Lets the current player take one gold token from the bank.
     *
     * @return the result of the move
     */
    public MoveResult takeGold() {
        if (discardMode) {
            return MoveResult.fail("You must discard tokens first.");
        }

        Player player = getCurrentPlayer();

        if (player.totalTokens() + 1 > 10) {
            return MoveResult.fail("You cannot take gold because you would exceed 10 tokens.");
        }

        if (tokenBank.get(TokenBank.GOLD) > 0) {
            tokenBank.remove(TokenBank.GOLD, 1);
            player.addTokens(TokenBank.GOLD, 1);
            return MoveResult.success(player.getName() + " took 1 gold token.");
        }

        return MoveResult.fail("No more gold.");
    }


    /**
     * Checks whether the given color is a valid non-gold token color.
     *
     * @param c the color to check
     * @return true if the color can be taken normally, false otherwise
     */
    private boolean isTakeColor(String c) {
        for (String color : TAKE_COLORS) {
            if (color.equals(c)) return true;
        }
        return false;
    }

    /**
     * Debug method that gives bonus points of a chosen color to the current player.
     *
     * @param color the bonus color to grant
     * @param amount the number of bonuses to grant
     * @return the result of the debug action
     */
    public MoveResult debugGrantBonus(String color, int amount) {
        Player player = getCurrentPlayer();
        color = color.toUpperCase();

        player.addBonus(color, amount);
        return MoveResult.success(
            "Player " + (currentPlayerIndex + 1) + " got + " + amount + " " + color + " bonus."
        );
    }

    /**
     * Discards one token of the given color from the current player back to the bank.
     *
     * @param color the token color to discard
     * @return the result of the move
     */
    public MoveResult discardToken(String color) {
        if (!discardMode) {
            return MoveResult.fail("You do not need to discard.");
        }

        Player player = getCurrentPlayer();
        color = color.toUpperCase();

        if (color.equals(TokenBank.GOLD) || !isTakeColor(color) && !color.equals(TokenBank.GOLD)) {
            return MoveResult.fail("Invalid color: " + color);
        }

        try {
            player.removeTokens(color, 1);
            tokenBank.add(color, 1);
            tokensToDiscard--;

            if (tokensToDiscard <= 0) {
                discardMode = false;
                tokensToDiscard = 0;
                return MoveResult.success("Discard complete. You may now end your turn.");
            }

            return MoveResult.success(
                "Discarded 1 " + color + ". " + tokensToDiscard + " more token(s) to discard."
            );
        } catch (Exception e) {
            return MoveResult.fail("You do not have any " + color + " token to discard.");
        }
    }

    /**
     * Checks whether the current player must discard tokens after taking tokens.
     * If the player now has more than 10 total tokens, discard mode is activated
     * and the number of required discards is recorded.
     *
     * @param successMessage the message describing the successful token-taking action
     * @return a success result describing either the discard requirement or the completed action
     */
    private MoveResult checkDiscardAfterTake(String successMessage) {
        Player player = getCurrentPlayer();
        int total = player.totalTokens();

        if (total > 10) {
            discardMode = true;
            tokensToDiscard = total - 10;
            return MoveResult.success(
                successMessage + " You must now discard " + tokensToDiscard + " token(s)."
            );
        }

        return successMessage == null
            ? MoveResult.success("Tokens taken successfully.")
            : MoveResult.success(successMessage);
    }

    /**
     * Returns whether the current player is in discard mode.
     * Discard mode means the player must discard tokens before continuing or ending the turn.
     *
     * @return true if the player must discard tokens, false otherwise
     */   
    public boolean isDiscardMode() {
        return discardMode;
    }

    /**
     * Returns how many more tokens the current player must discard.
     *
     * @return the number of tokens still required to be discarded
     */    
    public int getTokensToDiscard() {
        return tokensToDiscard;
    }


}
