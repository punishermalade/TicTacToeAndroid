package com.neilsonmarcil.tictactoe.player;

import android.os.Parcelable;

import com.neilsonmarcil.tictactoe.game.Game;

/**
 * A {@code Player} represents an entity that can interact in the game.
 */
public class Player {

    private int mIdentifier;

    /**
     * Constructor that takes a unique identifier for each player. If the same ID is used
     * for different instance, the behavior will be unpredictable.
     * @param id a unique ID
     */
    public Player(int id) {
        if (id == 0) {
            throw new IllegalArgumentException("Player ID cannot be equals to: " + id);
        }
        mIdentifier = id;
    }

    /**
     * Returns the ID associated with this player instance.
     */
    public int getId() {
        return mIdentifier;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Player && ((Player)o).getId() == mIdentifier;
    }
}
