import java.util.*;

/**
* A strategy game where all players have perfect information and no theme
* or narrative around gameplay.
*/
public abstract class AbstractStrategyGame {
    
    /**
     * Constructs and returns a String describing how to play the game. Should include
     * any relevant details on how to interpret the game state as returned by toString(),
     * how to make moves, the game end condition, and how to win.
     */
    public abstract String instructions();

    /**
    * Constructs and returns a String representation of the current game state. 
    * This representation should contain all information that should be known to
    * players at any point in the game, including board state (if any) and scores (if any).
    */
    public abstract String toString();

    /**
    * Returns true if the game has ended, and false otherwise.
    */
    public boolean isGameOver() {
        return getWinner() != -1;
    }

    /**
    * Returns the index of the player who has won the game,
    * or -1 if the game is not over.
    */
    public abstract int getWinner();

    /**
    * Returns the index of the player who will take the next turn.
    * If the game is over, returns -1.
    */
    public abstract int getNextPlayer();

    /**
    * Takes input from the parameter to specify the move the player
    * with the next turn wishes to make. 
    * Returns a string representation of said move.
    * If input is null, throw an IllegalArgumentException.
    */
    public abstract String getMove(Scanner input);

    /**
    * Takes in a string representation of the move the next player 
    * wishes (the output from getMove() ) and executes the move.
    * If input is null, throw an IllegalArgumentException.
    * If any part of the move is illegal, throw an IllegalArgumentException.
    */
    public abstract void makeMove(String input);
}
