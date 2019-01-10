package com.neilsonmarcil.tictactoe;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.neilsonmarcil.tictactoe.game.Board;
import com.neilsonmarcil.tictactoe.game.Game;
import com.neilsonmarcil.tictactoe.game.Score;
import com.neilsonmarcil.tictactoe.player.ComputerPlayer;
import com.neilsonmarcil.tictactoe.player.Player;

/**
 * The {@code MainActivity} acts as the Controller for the application. It creates the game objects
 * and the UI component (fragments). It also act as the observer for the Board interaction
 * (when the user is playing) and the Computer player interaction (when the computer player is
 * playing).
 */
public class MainActivity extends AppCompatActivity implements BoardFragment.BoardInteractionListener,
                                                               Game.ComputerPlayerListener{

    /**
     * Constants used to identify the two players. Cannot be zero, app won't start.
     */
    private static final int HUMAN_PLAYER_ID = 1;
    private static final int COMPUTER_PLAYER_ID = 2;

    private static final String BOARD_KEY = "board";
    private static final String SCORE_KEY = "score";
    private static final String ACTIVE_PLAYER_KEY = "active";
    private static final String PLAYER_LIST_KEY = "players";
    private static final String RESET_ENABLED_KEY = "reset";
    private static final String GAME_TIP_KEY = "gametip";
    private static final String BOARD_ENABLED_KEY = "boardenabled";

    /**
     * Game component
     */
    private Game mGame;
    private Score mScore;
    private Board mBoard;

    /**
     * UI and fragment component
     */
    private ScoreFragment mScoreFragment;
    private BoardFragment mBoardFragment;
    private Button mReset;
    private TextView mGameTip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createNewGame(savedInstanceState);

        mScoreFragment = ScoreFragment.newInstance(mScore);
        getSupportFragmentManager().beginTransaction().replace(R.id.score_fragment, mScoreFragment, "score").commitNow();

        mBoardFragment = BoardFragment.newInstance(mBoard, HUMAN_PLAYER_ID, COMPUTER_PLAYER_ID);
        getSupportFragmentManager().beginTransaction().replace(R.id.board_fragment, mBoardFragment, "board").commitNow();
        if (savedInstanceState != null && savedInstanceState.containsKey(BOARD_ENABLED_KEY)) {
            mBoardFragment.enableBoard(savedInstanceState.getBoolean(BOARD_ENABLED_KEY));
        }

        setResetButton(savedInstanceState);
        setGameTip(savedInstanceState);

        processBeginOfGame(true);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelable(SCORE_KEY, mScore);
        savedInstanceState.putParcelable(BOARD_KEY, mBoard);
        savedInstanceState.putInt(ACTIVE_PLAYER_KEY, mGame.getActivePlayer());
        savedInstanceState.putIntArray(PLAYER_LIST_KEY, mGame.getPlayersId());
        savedInstanceState.putBoolean(RESET_ENABLED_KEY, mReset.isEnabled());
        savedInstanceState.putString(GAME_TIP_KEY, mGameTip.getText().toString());
        savedInstanceState.putBoolean(BOARD_ENABLED_KEY, mBoardFragment.isEnabledBoard());
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void moveCompleted(int box, int id) {
        if (mGame.isValidMove(box)) {

            mBoardFragment.moveTo(box, id);
            int winner = mGame.determineWinner();

            if (winner == 0 && !mGame.isGameOver()) {
                mGame.nextPlayer();
            }
            else {
                processEndOfGame(winner);
            }
        }
    }

    /**
     * This function passes the move to the Board to update the UI.
     * @param box the index representing the box
     * @param i the player id
     */
    @Override
    public void computerPlayerMove(int box, int i) {
        moveCompleted(box, i);
    }

    /**
     * Process the end of the game by disabling the board, enabling the reset button and updating
     * the score.
     * @param winner the winner. If there is no winner, this is a draw game.
     */
    private void processEndOfGame(int winner) {
        mBoardFragment.enableBoard(false);
        mReset.setEnabled(true);
        mGame.updateScore(winner);
        mScoreFragment.updateScore();
        showGameTip(R.string.game_tip_2_game_over);
    }

    /**
     * Set the click listener to the reset button and restore it if needed
     * @param b the restoration bundle.
     */
    private void setResetButton(Bundle b) {
        mReset = (Button)findViewById(R.id.reset);
        mReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processBeginOfGame(false);
                mReset.setEnabled(false);
            }
        });

        if (b != null) {
            mReset.setEnabled(b.getBoolean(RESET_ENABLED_KEY, false));
        }
        else {
            mReset.setEnabled(false);
        }
    }

    /**
     * Set the first game tip if the restoration data is null
     */
    private void setGameTip(Bundle b) {
        mGameTip = (TextView)findViewById(R.id.game_tip);
        if (b != null && b.containsKey(GAME_TIP_KEY)) {
            mGameTip.setText(b.getString(GAME_TIP_KEY));
        }
        else {
            showGameTip(R.string.game_tip_1_first_install);
        }
    }

    /**
     * Process the beginning of the game by enabling and resetting the board and checking which
     * player should starts the game. The last active player from the last game starts. If this
     * is the first game, the human player starts.
     * @param first determine if this is the first launch of the application.
     */
    private void processBeginOfGame(boolean first) {
        if (!first) {
            mBoard.resetBoard();
            mBoardFragment.enableBoard(true);
            mBoardFragment.updateBoard();
            showGameTip(R.string.game_tip_3_computer_unbeatable);
        }
        mGame.startNewGame();
    }

    /**
     * Create a new game with two player, a board and a score object. It will restore any
     * previous game if the Bundle in parameter is not null.
     */
    private void createNewGame(Bundle b) {
        int p1 = HUMAN_PLAYER_ID;
        int p2 = COMPUTER_PLAYER_ID;
        int activePlayer = HUMAN_PLAYER_ID;

        if (b != null) {
            int[] p = b.getIntArray(PLAYER_LIST_KEY);
            p1 = p[0];
            p2 = p[1];

            mBoard = b.getParcelable(BOARD_KEY);
            mScore = b.getParcelable(SCORE_KEY);
            activePlayer = b.getInt(ACTIVE_PLAYER_KEY);
        }
        else {
            mBoard = new Board();
            mScore = new Score(0, 0, 0);
        }

        Player[] players = new Player[] { new Player(p1), new ComputerPlayer(p2) };
        mGame = new Game(mBoard, mScore, players, this);
        mGame.setActivePlayer(activePlayer);

    }

    private void showGameTip(int res) {
        mGameTip.setText(res);
    }
}