package com.neilsonmarcil.tictactoe.game;

import com.neilsonmarcil.tictactoe.player.ComputerPlayer;
import com.neilsonmarcil.tictactoe.player.Player;

import java.util.Arrays;

/**
 * A {@code Game} object holds the game properties (scores, last player played, etc...) and
 * reference to the different object (Board, Player, etc...)
 */
public class Game {

    /**
     * Represents the winning condition. Index 0 to 2 represents the first row, 3 to 5 the diagonal
     * from top right to lower left, etc...
     */
    private static final int[] WINNING_CONDITION = new int[]
            { 0, 1, 2, 0, 4, 8, 0, 3, 6, 3, 4, 5, 6, 7, 8, 2, 4, 6, 1, 4, 7, 2, 5, 8 };

    /**
     * The Board
     */
    private Board mBoard;

    /**
     * The Score
     */
    private Score mScore;

    /**
     * Computer player, active player and all the players id
     */
    private ComputerPlayer mComputerPlayer;
    private int mActivePlayer;
    private int[] mPlayersId;

    /**
     * A listener that is interested to be notified when a move has been played by the
     * computer.
     */
    private ComputerPlayerListener mListener;

    /**
     * Create a new Game
     * @param b the Board on which to play the game
     * @param s the Score for this game
     * @param p the players that will play the game. The first one must be the user player, the second one
     *          the computer player
     * @param l the ComputerPlayerListener that is used when the AI player has done his turn. Cannot be null.
     */
    public Game(Board b, Score s, Player[] p, ComputerPlayerListener l) {
        mBoard = b;
        mScore = s;

        if (p.length == 2) {
            mComputerPlayer = (ComputerPlayer)p[1];
            mPlayersId = new int[] { p[0].getId(), mComputerPlayer.getId() };
        }
        else {
            throw new IllegalArgumentException("Game must have two players");
        }
        mListener = l;
    }

    /**
     * Get the current Board
     * @return the current Board instance.
     */
    public Board getBoard() {
        return mBoard;
    }

    /**
     * Get the current Score
     * @return the current Score instance
     */
    public Score getScore() { return mScore; }

    /**
     * Returns the players id array
     */
    public int[] getPlayersId() {
        return mPlayersId;
    }

    /**
     * Start a new game by emptying the board and letting the active player play the first move.
     */
    public void startNewGame() {
        /**
         * No active player detected, this is the first game and the user plays first.
         */
        if (mActivePlayer == 0) {
            mActivePlayer = mPlayersId[0];
        }

        /**
         * Computer player either won or was the last to play in the last game,
         * starts the game.
         */
        if (mActivePlayer == mPlayersId[1]) {
            makeComputerPlay();
        }
    }

    /**
     *  Switch the next player as the active player. If the new active player is the
     *  computer player, then it plays right now.
     */
    public void nextPlayer() {
        if (mActivePlayer == mPlayersId[0]) {
            mActivePlayer = mPlayersId[1];
            makeComputerPlay();
        }
        else {
            mActivePlayer = mPlayersId[0];
        }
    }

    /**
     * Update the score according to the winner. If the winner is the user, it's a win, otherwise
     * it's a loss. If no winner, then it's a draw.
     * @param winner the winner id.
     */
    public void updateScore(int winner) {
        if (winner == mPlayersId[0]) {
            mScore.addScoreWin();
        }
        else if (winner == mPlayersId[1]) {
            mScore.addScoreLoss();
        }
        else {
            mScore.addScoreDraw();
        }
    }

    /**
     * Determine if the move to the desired box is valid. It is only valid if the board returns
     * a value of 0 (meaning empty).
     * @param box the desired box to analyse the move
     * @return true if the value from the board at the index is equals to zero, otherwise false. If
     * the value returned from the board is -1, an IndexOutOfBoundsException will be thrown.
     */
    public boolean isValidMove(int box) {
        int value = mBoard.getToken(box);
        if (value == -1) {
            throw new IndexOutOfBoundsException("Index value is not valid: " + box);
        }
        return value == 0;
    }

    /**
     * Determine if the game is over. A game is considered over when one of the following condition
     * are true:<br/>
     * <li>
     *     <ol>There is no more possible moves (the board is full)</ol>
     *     <ol>There is a winner for this current game state</ol>
     * </li>
     * @return true if the game is over, otherwise false.
     */
    public boolean isGameOver() {
        int[] possibleMove = mBoard.getNextPossibleMoves();
        int winner = determineWinner();
        return possibleMove.length == 0 || winner != 0;
    }

    /**
     * Determine which player is the winner
     * @return the player id that is the winner. If no winner, returns the value 0
     */
    public int determineWinner() {
        boolean p1posWinner = isPlayerWinning(mPlayersId[0]);
        if (p1posWinner) {
            return mPlayersId[0];
        }
        boolean p2posWinner = isPlayerWinning(mPlayersId[1]);
        if (p2posWinner) {
            return mPlayersId[1];
        }
        return 0;
    }

    /**
     * Get the active player
     * @return the player id active player
     */
    public int getActivePlayer() {
        return mActivePlayer;
    }

    /**
     * Set the active player
     * @param id the player id
     */
    public void setActivePlayer(int id) {
        mActivePlayer = id;
    }

    /**
     * Call the AI player to play the next move and send the result to the listener.
     */
    private void makeComputerPlay() {
        int move = mComputerPlayer.playMove(this);
        if (isValidMove(move)) {
            mListener.computerPlayerMove(move, mPlayersId[1]);
        }
    }

    /**
     * Determine if the player is winning for the current state of the game
     * @param p the player id
     * @return true if the player is the winner, otherwise false.
     */
    private boolean isPlayerWinning(int p) {
        return isPlayerWinning(WINNING_CONDITION, mBoard.getPlayerPosition(p));
    }

    /**
     * Checks for three values that are equals in the array.
     * @param block the array
     * @param maxIndex the max index to check
     * @return true if three value are found between index 0 and maxIndex.
     */
    private boolean hasThreeOccurences(int[] block, int maxIndex) {
        boolean result = false;
        int[] b = Arrays.copyOf(block, maxIndex);
        Arrays.sort(b);

        for (int i = 2; i < b.length && !result && i < maxIndex; i++ ) {
            if (b[i - 2] == b[i - 1] && b[i - 1] == b[i]) {
                result = true;
            }
        }
        return result;
    }

    /**
     * This function checks for all the player position and for each of them, determine which
     * winning block it belongs to. For example, position 0, 1, 2 belongs to a winning block
     * (representing the first row). Each position index value from the winning condition block are
     * divided by three, resulting in a value for a block. When there is three values that
     * represents the same block, the player is in a winning condition.
     * @param wc the winning condition array
     * @param p the player position
     * @return true if the player is winning.
     */
    private boolean isPlayerWinning(int[] wc, int[] p) {
        boolean winner = false;
        int blockIndex = 0;
        int[] block = new int[20];
        for (int i = 0; i < p.length && !winner; i++) {
            for (int j = 0; j < wc.length && !winner; j++) {
                if (p[i] == wc[j]) {
                    block[blockIndex++] = j / 3;
                    winner = hasThreeOccurences(block, blockIndex);
                }
            }
        }
        return winner;
    }

    /**
     * This interface represents an observer that has an interest into knowing when the computer
     * player has completed his turn.
     */
    public interface ComputerPlayerListener {
        /**
         * Represents the computer player wanting to play his token to the desired
         * box.
         * @param box the index representing the box
         * @param i the player id
         */
        void computerPlayerMove(int box, int i);
    }
}
