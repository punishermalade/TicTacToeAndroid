package com.neilsonmarcil.tictactoe;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.neilsonmarcil.tictactoe.game.Score;

/**
 * A {@code ScoreFragment} is used to display a score containing the number of wins, losses and draw.
 * The user interface may not include all the information.
 */
public class ScoreFragment extends Fragment implements View.OnLongClickListener{

    private static final String SCORE_OBJECT_KEY = "sok";

    /**
     * The underlying score object
     */
    private Score mScoreObject;

    /**
     * UI Element
     */
    private TextView mWinsTxt;
    private TextView mLossesTxt;
    private TextView mDrawsTxt;

    /**
     * Create a new instance of the ScoreFragment that needs a Score object
     * @param s a valid Score object instance
     * @return a ScoreFragment
     */
    public static ScoreFragment newInstance(Score s) {
        ScoreFragment f = new ScoreFragment();
        Bundle b = new Bundle();
        b.putParcelable(SCORE_OBJECT_KEY, s);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Bundle args = getArguments();

        if (args.containsKey(SCORE_OBJECT_KEY)) {
            mScoreObject = args.getParcelable(SCORE_OBJECT_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_score, container, false);
        view.setOnLongClickListener(this);
        mWinsTxt = (TextView)view.findViewById(R.id.game_score_wins);
        mLossesTxt = (TextView)view.findViewById(R.id.game_score_losses);
        mDrawsTxt = (TextView)view.findViewById(R.id.game_score_draws);
        updateScore();
        return view;
    }

    @Override
    public boolean onLongClick(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.reset_score_title);
        builder.setMessage(R.string.reset_score_msg);
        builder.setNegativeButton(R.string.reset_score_no, null);
        builder.setPositiveButton(R.string.reset_score_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getActivity().finish();
                getActivity().startActivity(
                        getActivity().getPackageManager().getLaunchIntentForPackage(getActivity().getPackageName()));
            }
        });
        builder.create().show();
        return true;
    }

    /**
     * Update the scores information
     */
    public void updateScore() {
        mWinsTxt.setText(getString(R.string.game_score_wins_format, mScoreObject.getScoreWins()));
        mLossesTxt.setText(getString(R.string.game_score_losses_format, mScoreObject.getScoreLosses()));
        mDrawsTxt.setText(getString(R.string.game_score_draws_format, mScoreObject.getScoreDraws()));
    }

}
