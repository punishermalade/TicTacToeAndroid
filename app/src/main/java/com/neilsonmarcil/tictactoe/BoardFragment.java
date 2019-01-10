package com.neilsonmarcil.tictactoe;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.neilsonmarcil.tictactoe.game.Board;

/**
 * A {@code BoardFragment} is used to display the Board Game and handle the user click on it. It
 * uses the Observer pattern to notify the listener that a click has been done on the fragment.
 */
public class BoardFragment extends Fragment implements View.OnClickListener {

    /**
     * the key to get the object from the arguments
     */
    private static final String BOARD_OBJECT_KEY = "board";
    private static final String PLAYER_X_SYMBOL_ID = "playerX";
    private static final String PLAYER_O_SYMBOL_ID = "playerO";

    private static final String SYMBOL_X = "X";
    private static final String SYMBOL_O = "O";

    /**
     * Key for the tag to identify the box when the user click on the board
     */
    private static final int TAG_BOX_ID = R.string.board_box_id_tag;

    /**
     * The layout that reacts to the user click and contains the TextView
     */
    private static final int[] BOXES_ID_LIST = new int[] {
            R.id.board_0, R.id.board_1, R.id.board_2, R.id.board_3, R.id.board_4,
            R.id.board_5, R.id.board_6, R.id.board_7, R.id.board_8
    };

    /**
     * The TextView id for each boxes.
     */
    private static final int[] CONTENT_ID_LIST = new int[] {
            R.id.board_token_0, R.id.board_token_1, R.id.board_token_2,
            R.id.board_token_3, R.id.board_token_4, R.id.board_token_5,
            R.id.board_token_6, R.id.board_token_7, R.id.board_token_8
    };

    /**
     * The Board internal object
     */
    private Board mBoard;

    /**
     * A listener to receive the data from the board
     */
    private BoardInteractionListener mListener;

    /**
     * represents the player id that clicks on the board. Default value is one.
     */
    private int mPlayerClickingId = 1;

    /**
     * Association between the player Id and their respective symbols (X or O)
     */
    private SparseArray<String> mPlayersSymbol = new SparseArray<>();

    /**
     * A reference to all the TextView, to increase performance when clearing the board for
     * a new game.
     */
    private TextView[] mBoxesTextView = new TextView[9];

    /**
     * Enable/disable the click on the board
     */
    private boolean mEnableBoard = true;

    /**
     * Create a new instance of the BoardFragment that takes a Board object as a parameter.
     * @param b the Board to be used
     * @return an instance of BoardFragment
     */
    public static BoardFragment newInstance(Board b, int idX, int idO) {
        BoardFragment f = new BoardFragment();
        Bundle args = new Bundle();
        args.putParcelable(BOARD_OBJECT_KEY, b);
        args.putInt(PLAYER_X_SYMBOL_ID, idX);
        args.putInt(PLAYER_O_SYMBOL_ID, idO);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        Bundle args = getArguments();
        if (args != null) {
            mBoard = args.getParcelable(BOARD_OBJECT_KEY);
            mPlayerClickingId = args.getInt(PLAYER_X_SYMBOL_ID);

            mPlayersSymbol.put(mPlayerClickingId, SYMBOL_X);
            mPlayersSymbol.put(args.getInt(PLAYER_O_SYMBOL_ID), SYMBOL_O);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_board, container, false);
        setClickListener(view);
        updateBoard();
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BoardInteractionListener) {
            mListener = (BoardInteractionListener) context;
        }
        else {
            throw new RuntimeException(context.toString() + " must implement BoardInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mBoxesTextView = new TextView[0];
        mListener = null;
        mPlayersSymbol.clear();
    }

    @Override
    public void onClick(View v) {
        if (mListener != null && mEnableBoard) {
            mListener.moveCompleted((int)v.getTag(TAG_BOX_ID), mPlayerClickingId);
        }
    }

    /**
     * Draw the token into the specified box for the specified player
     * @param index the index where to draw the token
     * @param id the player id
     */
    public void moveTo(int index, int id) {
        mBoard.addToken(index, id);
        mBoxesTextView[index].setVisibility(View.VISIBLE);
        mBoxesTextView[index].setText(mPlayersSymbol.get(id));
    }

    /**
     * Enable/disable the click on the board
     */
    public void enableBoard(boolean e) {
        mEnableBoard = e;
    }

    /**
     * Return if the board is enabled or not
     */
    public boolean isEnabledBoard() {
        return mEnableBoard;
    }

    /**
     * Update the UI board with the current value from the Board data structure
     */
    public void updateBoard() {
        for (int i = 0; i < 9; i++) {
            int bv = mBoard.getToken(i);
            if (bv != 0) {
                mBoxesTextView[i].setText(mPlayersSymbol.get(bv));
            }
            else {
                mBoxesTextView[i].setText("");
            }
        }
    }

    /**
     *  Set the Click listener for all the boxes and set the tag to keep track of which box
     *  is clicked.
     */
    private void setClickListener(View v) {
        for (int i = 0; i < BOXES_ID_LIST.length; i++) {
            View box = v.findViewById(BOXES_ID_LIST[i]);
            box.setTag(TAG_BOX_ID, i);
            box.setOnClickListener(this);
            mBoxesTextView[i] = (TextView)v.findViewById(CONTENT_ID_LIST[i]);
        }
    }

    /**
     *  The BoardInteractionListener is an observer that is interested to know when the user clicks
     *  on the fragment to play his turn.
     */
    public interface BoardInteractionListener {
        /**
         * When a click is detected, it send the index (contained on the clicked view) and the
         * player id to the observer.
         * @param box the index box
         * @param id the player id
         */
        void moveCompleted(int box, int id);
    }
}
