package com.neilsonmarcil.tictactoe.player;

import android.os.Parcel;
import android.os.Parcelable;

import com.neilsonmarcil.tictactoe.game.Board;
import com.neilsonmarcil.tictactoe.game.Game;

import java.util.Random;

/**
 * A {@code ComputerPlayer} is a AI player that competes against a human player (the user).
 * It uses the minimax algorithm to determine the best next move. The implementation makes
 * the computer player almost unbeatable.
 */
public class ComputerPlayer extends Player implements Parcelable {

    /**
     * Represents the default value that defines the worth of a game state.
     */
    private static final int DEFAULT_WIN_VALUE = 10;

    /**
     * The current state game. The algorithm needs to know from the game logic if a given state
     * is a winning condition.
     */
    private Game mCurrentGame;

    /**
     * Keep in memory the best move found so far during the decision tree.
     */
    private int mNextMove;

    /**
     * Represents the human player. The AI needs to know what is the human player ID
     */
    private int mPlayer1;

    /**
     * Represents the computer player id
     */
    private int mPlayer2;

    /**
     * The default constructor that takes the player id in parameter
     * @param id the player id
     */
    public ComputerPlayer(int id) {
        super(id);
    }

    /**
     * Returns the next move that the AI wants to play. The {@code Game} instance is necessary for
     * the algorithm to detect for any state if there is a winner or if the game is over.
     * @param g the current game being played
     * @return an index from 0 to 8 inclusive representing the box in which the AI wants to play.
     */
    public int playMove(Game g) {
        mPlayer1 = g.getPlayersId()[0];
        mPlayer2 = getId();
        mCurrentGame = g;
        playMoveRecursive(mCurrentGame.getBoard(), getId(), 0);
        return mNextMove;
    }

    /**
     * This recursive method uses the minimax algorithm to find the best possible move for
     * the current board state. It will always try to either win and block the player
     * even if the game is already lost.<br/><br/>
     *
     * It uses a depth value for each recursive level to either put more or less value to
     * specific decision tree. When the algorithm analyses the turn of the opponent, the best
     * score will be the highest one. When it analyses his own turn, the best score will be the
     * lowest one. After each passes, if the current move is the best, it is kept in memory
     * until a new best score is calculated.<br/><br/>
     *
     * A slight optimisation has been done when the board is empty, in that case the algorithm will
     * choose any box on the board to avoid the calculation of all the possible game output.<br/><br/>
     *
     * The Board object and its internal data structure are not copied to keep the memory footprint
     * to the minimum. After every passes, the algorithm will undo the last move and the board
     * will retain it's original state.
     *
     * @param b the board to use.
     * @param pt the player turn
     * @param depth the depth level
     * @return the score based on the victory of the human player (+10) or not (-10). It is adjusted
     * by the depth value.
     */
    private int playMoveRecursive(Board b, int pt, int depth) {
        int bs = pt == mPlayer1 ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        int bm = Integer.MIN_VALUE;
        int cs = 0;

        int[] emptyBoard = b.getNextPossibleMoves();
        if (emptyBoard.length == 9) {
            mNextMove = chooseRandomBox();
            return DEFAULT_WIN_VALUE;
        }

        if (mCurrentGame.isGameOver()) {
            bs = getScore(depth);
        }
        else {
            depth++;
            int[] possibleMove = b.getNextPossibleMoves();

            for (int i = 0; i < possibleMove.length; i++) {
                int moveTo = possibleMove[i];
                b.addToken(moveTo, pt);

                if (pt == mPlayer1) {
                    cs = playMoveRecursive(b, mPlayer2, depth);
                    if (cs > bs) {
                        bs = cs;
                        bm = moveTo;
                    }
                }
                else {
                    cs = playMoveRecursive(b, mPlayer1, depth);
                    if (cs < bs) {
                        bs = cs;
                        bm = moveTo;
                    }
                }
                // removing the move from the board
                b.addToken(moveTo, 0);
                mNextMove = bm;
            }
        }
        return bs;
    }

    private int getScore(int depth) {
        int winner = mCurrentGame.determineWinner();
        if (winner == mPlayer1) {
            return DEFAULT_WIN_VALUE - depth;
        }
        else if (winner == mPlayer2) {
            return depth - DEFAULT_WIN_VALUE;
        }
        return 0;
    }

    private int chooseRandomBox() {
        Random r = new Random(System.currentTimeMillis());
        return r.nextInt(9);
    }

    /**
     * The next section is the implementation of the Parcelable interface
     */

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(getId());
        dest.writeInt(mNextMove);
        dest.writeInt(mPlayer1);
        dest.writeInt(mPlayer2);
    }

    public static final Parcelable.Creator<ComputerPlayer> CREATOR = new Parcelable.Creator<ComputerPlayer>() {
        public ComputerPlayer createFromParcel(Parcel in) {
            return new ComputerPlayer(in);
        }
        public ComputerPlayer[] newArray(int size) {
            return new ComputerPlayer[size];
        }
    };

    private ComputerPlayer(Parcel in) {
        super(in.readInt());
        mNextMove = in.readInt();
        mPlayer1 = in.readInt();
        mPlayer2 = in.readInt();
    }
}
