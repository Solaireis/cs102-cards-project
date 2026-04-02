package Test;

/**
 * Represents the result of a game action.
 * A move result stores whether the action succeeded
 * and a message describing the outcome.
 */
public class MoveResult {
    private final boolean success;
    private final String message;

    /**
     * Creates a move result with the given success state and message.
     *
     * @param success whether the move succeeded
     * @param message the message describing the result
     */
    private MoveResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    /**
     * Creates a successful move result.
     *
     * @param message the success message
     * @return a successful move result
     */
    public static MoveResult success(String message) {
        return new MoveResult(true, message);
    }

    /**
     * Creates a failed move result.
     *
     * @param message the failure message
     * @return a failed move result
     */
    public static MoveResult fail(String message) {
        return new MoveResult(false, message);
    }

    /**
     * Returns whether the move was successful.
     *
     * @return true if the move succeeded, false otherwise
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Returns the message describing the move result.
     *
     * @return the result message
     */
    public String getMessage() {
        return message;
    }
}