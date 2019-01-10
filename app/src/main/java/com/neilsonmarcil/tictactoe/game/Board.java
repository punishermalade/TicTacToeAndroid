package com.neilsonmarcil.tictactoe.game;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Arrays;

/**
 * A {@code Board} represents the TicTacToe playable area. This is used by the AI to play the
 * next move. It uses the Composition pattern by having a reference of the BoardFragment in
 * the class member.
 */
public class Board implements Parcelable {

    /**
     * Represents an empty Board
     */
    private static final int[] EMPTY_BOARD = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0 };

    /**
     * Represents the actual state of the Board
     */
    private int[] mBoard = new int[0];

    /**
     * Create a new Board
     */
    public Board() {
        mBoard = Arrays.copyOf(EMPTY_BOARD, EMPTY_BOARD.length) ;
    }


    /**
     * Add a token from the player at a specific position. It does not check if there
     * is already a token from the other player. It will override any existing value.
     * @param index the box index to add the token
     * @param player the player symbol to add on the board
     */
    public void addToken(int index, int player) {
        if (index >= 0 && index < mBoard.length) {
            mBoard[index] = player;
        }
    }

    /**
     * Get the value of the from the board at the specified index.
     * @param index the index to get the value from
     * @return the value from the board at the index, if the index is out of bound, returns -1
     */
    public int getToken(int index) {
        if (index >= 0 && index < mBoard.length) {
            return mBoard[index];
        }
        return -1;
    }

    /**
     * Returns a list of all possible moves from the board. A possible move is any box that has
     * the value 0.
     * @return an native int array that contains the indexes of empty boxes.
     */
    public int[] getNextPossibleMoves() {
        int[] moves = new int[9];
        int c = 0;
        for (int i = 0; i < mBoard.length; i++) {
            if (mBoard[i] == 0) {
                moves[c++] = i;
            }
        }
        return Arrays.copyOf(moves, c);
    }

    /**
     * Returns a list of box that currently has the specified player token.
     * @param player the player to look for
     * @return the list of box (index) that contains the player token
     */
    public int[] getPlayerPosition(int player) {
        int[] pos = new int[5];
        int c = 0;
        for (int i = 0; i < mBoard.length && c < 5; i++) {
            if (mBoard[i] == player) {
                pos[c++] = i;
            }
        }
        return Arrays.copyOf(pos, c);
    }

    /**
     *  Reset the board by copying the empty one into the current one. It also enables it and
     *  refresh the UI to show the new board state.
     */
    public void resetBoard() {
        mBoard = Arrays.copyOf(EMPTY_BOARD, EMPTY_BOARD.length);
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
        dest.writeIntArray(mBoard);
    }

    public static final Parcelable.Creator<Board> CREATOR = new Parcelable.Creator<Board>() {
        public Board createFromParcel(Parcel in) {
            return new Board(in);
        }
        public Board[] newArray(int size) {
            return new Board[size];
        }
    };

    private Board(Parcel in) {
        mBoard = new int[9];
        in.readIntArray(mBoard);
    }
}
