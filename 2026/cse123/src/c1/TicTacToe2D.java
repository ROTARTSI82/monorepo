// **THIS IS AN EXAMPLE IMPLEMENTATION!**
// Brett Wortzman
// CSE 123
// C1: Abstract Strategy Games

import java.util.*;

// A class to represent a game of tic-tac-toe that extends the 
// AbstractStrategyGame class. 
public class TicTacToe2D extends AbstractStrategyGame {
    public static final char PLAYER_1_TOKEN = 'X';
    public static final char PLAYER_2_TOKEN = 'O';
    public static final int PLAYER_1 = 1;
    public static final int PLAYER_2 = 2;
    public static final int TIE = 0;
    public static final int GAME_IS_OVER = -1;
    public static final int GAME_NOT_OVER = -1;

    private char[][] board;
    private boolean isXTurn;

    // Constructs a new TicTacToe game.
    public TicTacToe2D() {
        board = new char[][]{{'1', '2', '3'},
                             {'4', '5', '6'},
                             {'7', '8', '9'}};
        isXTurn = true;
    }

    // Returns the index of the winner of the game.
    // 1 if player 1 (X), 2 if player 2 (O), 0 if a tie occurred,
    // and -1 if the game is not over.
    public int getWinner() {
        for (int i = 0; i < board.length; i++) {
            // check row i
            int rowWinner = getRowWinner(i);
            if (rowWinner != GAME_NOT_OVER) {
                return rowWinner;
            }

            int colWinner = getColWinner(i);
            if (colWinner != GAME_NOT_OVER) {
                return colWinner;
            }
        }

        // check diagonals
        int diagWinner = getDiagWinner();
        if (diagWinner != GAME_NOT_OVER) {
            return diagWinner;
        }
        return checkTie();
    }

    // Returns the index of which player's turn it is.
    // 1 if player 1 (X), 2 if player 2 (O), -1 if the game is over
    public int getNextPlayer() {
        if (isGameOver()) {
            return GAME_IS_OVER;
        }
        
        if(isXTurn) {
            return PLAYER_1;
        } else {
            return PLAYER_2;
        }
    }

    // Reads the move from the given Scanner input and returns
    // a string representation of the cell the player wishes to
    // play their token.
    // Throws an IllegalArgumentException if input is null. 
    public String getMove(Scanner input) {
        if (input == null) {
            throw new IllegalArgumentException();
        }
        System.out.print("Cell? ");
        return Integer.toString(input.nextInt());
    }

    // Given the input, places an X or an O where
    // the player specifies.
    // Throws an IllegalArgumentException if the position is
    // invalid, whether that be out of bounds or already occupied.
    // Board bounds are [1, 9] for cells.
    public void makeMove(String input) {
        char currToken;
        if(isXTurn) {
            currToken = PLAYER_1_TOKEN;
        } else {
            currToken = PLAYER_2_TOKEN;
        }

        makeMove(Integer.parseInt(input), currToken);
        isXTurn = !isXTurn;
    }

    // Private helper method for makeMove.
    // Given a cell number, as well as player token,
    // places that token in the specified cell.
    // Throws an IllegalArgumentException if the position is
    // invalid, whether that be out of bounds or already occupied.
    // Board bounds are [1, 9] for cells.
    private void makeMove(int cell, char token) {
        int row = (cell - 1) / board.length;
        int col = (cell - 1) % board.length;
        if (row < 0 || row >= board.length ||
            col < 0 || col >= board[0].length) {
            throw new IllegalArgumentException("Invalid board position: " + cell);
        }

        if (!isEmpty(row, col)) {
            throw new IllegalArgumentException("Space already occupied: " + cell);
        }
        
        board[row][col] = token;
    }

    // Returns a String containing instructions to play the game.
    public String instructions() {
        String result = "";
        result += "Player 1 is X and goes first. Choose where to play by entering the number\n";
        result += "of the cell (displayed by the board). Spaces shown as a number are empty.\n";
        result += "The game ends when one player marks three spaces in a row, in which that\n";
        result += "player wins, or when the board is full, in which case the game end in a tie.";
        return result;
    }

    // Returns a String representation of the current state of the board.
    // For example, the empty board looks like:
    // "| 1 | 2 | 3 |
    //  | 4 | 5 | 6 |
    //  | 7 | 8 | 9 |"
    // Where each cell is labeled with its number.
    public String toString() {
        String result = "";
        for (int i = 0; i < board.length; i++) {
            result += "|";
            for (int j = 0; j < board[0].length; j++) {
                result += " " + board[i][j] + " |";
            }
            result += "\n";
        }
        return result;
    }

    // Returns the winner of the row.
    // Returns 1 if player 1 won, 2 if player 2 won, -1 otherwise.
    private int getRowWinner(int row) {
        if (isEmpty(row, 0)) {
            return GAME_NOT_OVER;
        }

        if (board[row][0] == board[row][1] && board[row][0] == board[row][2]) {
            return getPlayer(board[row][0]);
        }

        return GAME_NOT_OVER;
    }

    // Returns the winner of the column.
    // Returns 1 if player 1 won, 2 if player 2 won, -1 otherwise.
    private int getColWinner(int col) {
        if (isEmpty(0, col)) {
            return GAME_NOT_OVER;
        }

        if (board[0][col] == board[1][col] && board[0][col] == board[2][col]) {
            return getPlayer(board[0][col]);
        }

        return GAME_NOT_OVER;
    }

    // Returns the winner of either diagonal.
    // Returns 1 if player 1 won, 2 if player 2 won, -1 otherwise.
    private int getDiagWinner() {
        // (1, 1) is the center cell
        if (isEmpty(1, 1)) {
            return GAME_NOT_OVER;
        }

        if (board[0][0] == board[1][1] && board[0][0] == board[2][2]) {
            return getPlayer(board[0][0]);
        }        
        if (board[0][2] == board[1][1] &&  board[0][2] == board[2][0]) {
            return getPlayer(board[0][2]);
        }

        return GAME_NOT_OVER;
    }

    // Returns 0 if there's a tie and -1 otherwise.
    private int checkTie() {
        // check for tie
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (isEmpty(i, j)) {
                    // unfilled space; game not over
                    return GAME_NOT_OVER;
                }
            }
        }

        // it's a tie!
        return TIE;
    }

    // Returns the player associated with the player token.
    // Returns 1 if player 1 (X), 2 if player 2 (O), -1 if the space is empty. 
    private int getPlayer(char token) {
        if (token == PLAYER_1_TOKEN) {
            return PLAYER_1;
        } else if (token == PLAYER_2_TOKEN) {
            return PLAYER_2;
        }

        return GAME_NOT_OVER;
    }

    // Returns true if the specified cell in the board is empty. False otherwise
    private boolean isEmpty(int row, int col) {
        return board[row][col] != PLAYER_1_TOKEN && board[row][col] != PLAYER_2_TOKEN;
    }
}
