package com.konradrej.thirty;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Handles GameModel instance and gameplay screen activity.
 *
 * @author Konrad Rej
 */
public class MainActivity extends AppCompatActivity implements PropertyChangeListener, View.OnClickListener {
    private final Integer[] diceDrawables = {
            R.drawable.white1,
            R.drawable.white2,
            R.drawable.white3,
            R.drawable.white4,
            R.drawable.white5,
            R.drawable.white6
    };
    private final List<ImageView> mDiceIvList = new ArrayList<>();
    private Button mBtn;
    private TextView mCurrentRoundTv;
    private TextView mRemainingThrowsTv;
    private Spinner mOptionSp;
    private ArrayAdapter<CharSequence> mOptionSpAd;
    private GameModel gameModel;


    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);

        ArrayList<CharSequence> mOptionSpAdValues = new ArrayList<>();
        for (int i = 0; i < mOptionSpAd.getCount(); i++) {
            mOptionSpAdValues.add(mOptionSpAd.getItem(i));
        }
        bundle.putSerializable("mOptionSpAdValues", mOptionSpAdValues);
        bundle.putInt("mOptionSpPos", mOptionSp.getSelectedItemPosition());

        bundle.putSerializable("gameModel", gameModel);
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_main);

        // Hide action bar
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.hide();
        }

        // If bundle contains GameModel instance use it, otherwise create new instance
        if (bundle != null && bundle.containsKey("gameModel")) {
            gameModel = (GameModel) bundle.getSerializable("gameModel");
        } else {
            gameModel = new GameModel();
        }
        gameModel.addPropertyChangeListener(this);

        // Get mCurrentRoundTv and update it
        mCurrentRoundTv = findViewById(R.id.currentRound);
        updateCurrentRound();

        // Get mRemainingThrowsTv and update it
        mRemainingThrowsTv = findViewById(R.id.remainingThrows);
        updateRemainingThrows();

        // Get mBtn and set OnClickListener for all dice imageViews
        mBtn = findViewById(R.id.button);
        mBtn.setOnClickListener(this);

        initializeDiceIv();
        initializeSpinner(bundle);
    }

    /**
     * Populates mDiceIvList and sets OnClickListener for all diceIv
     */
    private void initializeDiceIv() {
        // Populate mDiceIvList
        mDiceIvList.add(findViewById(R.id.diceView1));
        mDiceIvList.add(findViewById(R.id.diceView2));
        mDiceIvList.add(findViewById(R.id.diceView3));
        mDiceIvList.add(findViewById(R.id.diceView4));
        mDiceIvList.add(findViewById(R.id.diceView5));
        mDiceIvList.add(findViewById(R.id.diceView6));

        // Set OnClickListener for all dice imageViews
        for (int i = 0; i < mDiceIvList.size(); i++) {
            ImageView diceIv = mDiceIvList.get(i);

            updateDiceIv(gameModel.getDiceByIndex(i), diceIv);

            diceIv.setOnClickListener(v -> {
                ImageView localDiceIv = v.findViewById(v.getId());
                int index = mDiceIvList.indexOf(localDiceIv);

                Dice dice = gameModel.getDiceByIndex(index);
                dice.toggleLocked();

                updateDiceIv(dice, mDiceIvList.get(index));
            });
        }
    }

    /**
     * Populates count option spinner with values from bundle if they exist otherwise
     * with values from string array in /res/values/strings.xml
     *
     * @param bundle bundle with saved data
     */
    private void initializeSpinner(Bundle bundle) {
        // Get mOptionSp and populate it with values
        mOptionSp = findViewById(R.id.spinner);
        if (bundle != null && bundle.containsKey("mOptionSpAdValues")) {
            List<String> items =
                    (ArrayList<String>) bundle.getSerializable("mOptionSpAdValues");
            mOptionSpAd =
                    new ArrayAdapter(this, android.R.layout.simple_spinner_item, items);
        } else {
            List<String> items =
                    new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.point_options)));
            mOptionSpAd =
                    new ArrayAdapter(this, android.R.layout.simple_spinner_item, items);
        }

        if (bundle != null && bundle.containsKey("mOptionSpPos")) {
            mOptionSp.setSelection(bundle.getInt("mOptionSpPos"));
        }
        mOptionSpAd.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mOptionSp.setAdapter(mOptionSpAd);
    }

    private void updateCurrentRound() {
        mCurrentRoundTv.setText(
                getString(R.string.current_round, gameModel.getCurrentRound()));
    }

    private void updateRemainingThrows() {
        mRemainingThrowsTv.setText(
                getString(R.string.remaining_throws, gameModel.getRemainingThrows()));
    }

    private void updateDiceIv(Dice dice, ImageView diceIv) {
        diceIv.setImageResource(diceDrawables[dice.getValue() - 1]);

        if (dice.getLocked()) {
            diceIv.setAlpha(0.3f);
        } else {
            diceIv.setAlpha(1f);
        }
    }

    @Override
    public void onClick(View v) {
        // Checks if round is over (remainingThrows > 0 => round is not over)
        if (gameModel.getRemainingThrows() > 0) {
            if (gameModel.getRemainingThrows() == 1) {
                mBtn.setText(R.string.button_score);
            }

            gameModel.throwDice();
        } else {
            CharSequence selectedItem = (CharSequence) mOptionSp.getSelectedItem();
            mOptionSpAd.remove(selectedItem);

            gameModel.saveRoundResult(selectedItem);

            // Checks if game is over (currentRound == 10 => game is over)
            if (gameModel.getCurrentRound() == 10) {
                // Starts result activity
                Intent intent = new Intent(this, ResultActivity.class);
                intent.putExtra(getString(R.string.EXTRA_OPTION_SCORES), gameModel.getResultModel());
                startActivity(intent);

                // Prepares for return to current activity
                gameModel.startNewGame();
                initializeSpinner(null);
            } else {
                gameModel.startNewRound();
            }

            mBtn.setText(R.string.button_throw);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case "mDiceListSingle":
                Dice dice = (Dice) evt.getNewValue();
                updateDiceIv(dice, mDiceIvList.get(gameModel.getDiceIndex(dice)));
                break;
            case "remainingThrows":
                updateRemainingThrows();
                break;
            case "currentRound":
                updateCurrentRound();
                break;
            default:
                Log.e("Thirty: ", "Unknown propertyChange: " + evt.getPropertyName());
        }
    }
}