package com.neilsonmarcil.tictactoe.game;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * A {@code Score} object holds three number that represents wins, losses and draws information.
 */
public class Score implements Parcelable {

    /**
     * Public keys to access the data
     */
    private static final int SCORE_WINS = 0;
    private static final int SCORE_LOSSES = 1;
    private static final int SCORE_DRAWS = 2;

    /**
     * an array containing the different score data
     */
    private int[] mScore;

    /**
     *  Create a new Score object with the default value of 0 for all the data.
     */
    public Score() {
        this(0 , 0, 0);
    }

    /**
     * Create a new Score object with the specified value for all the data. None of the parameter
     * can be less than 0.
     * @param w the number of wins
     * @param l the number of losses
     * @param d the nuber of draws
     */
    public Score(int w, int l, int d) {
        if (w < 0 || l < 0 || d < 0) {
            throw new IllegalArgumentException("Score information cannot be less than 0");
        }
        mScore = new int[3];
        mScore[SCORE_WINS] = w;
        mScore[SCORE_LOSSES] = l;
        mScore[SCORE_DRAWS] = d;
    }

    /**
     * Get/Set for the Score data
     */

    public int getScoreWins() {
        return mScore[SCORE_WINS];
    }

    public int getScoreLosses() {
        return mScore[SCORE_LOSSES];
    }

    public int getScoreDraws() {
        return mScore[SCORE_DRAWS];
    }

    public void addScoreWin() {
        mScore[SCORE_WINS]++;
    }

    public void addScoreLoss() {
        mScore[SCORE_LOSSES]++;
    }

    public void addScoreDraw() {
        mScore[SCORE_DRAWS]++;
    }

    /**
     * Reset the score to 0 for all data
     */
    public void resetScore() {
        mScore = new int[3];
        mScore[0] = 0;
        mScore[1] = 0;
        mScore[2] = 0;
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
        dest.writeIntArray(mScore);
    }

    public static final Parcelable.Creator<Score> CREATOR = new Parcelable.Creator<Score>() {
        public Score createFromParcel(Parcel in) {
            return new Score(in);
        }
        public Score[] newArray(int size) {
            return new Score[size];
        }
    };

    private Score(Parcel in) {
        mScore = new int[3];
        in.readIntArray(mScore);
    }
}
