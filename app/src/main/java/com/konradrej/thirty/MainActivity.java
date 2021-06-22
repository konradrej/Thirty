package com.konradrej.thirty;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class MainActivity extends AppCompatActivity implements Observer, View.OnClickListener {
    private final List<Dice> mDiceList = new ArrayList<>();
    private final List<ImageView> mDiceIVList = new ArrayList<>();
    private final Integer[] mDiceDrawable = {
            R.drawable.white1,
            R.drawable.white2,
            R.drawable.white3,
            R.drawable.white4,
            R.drawable.white5,
            R.drawable.white6
    };
    private ResultModel results;
    private Button mBtn;
    private TextView mCurrentRoundTv;
    private TextView mRemainingThrowsTv;
    private Spinner mOptionSp;
    private ArrayAdapter<CharSequence> mOptionSpAd;
    private int currentRound = 1;
    private int remainingThrows = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){}

        /* Populate spinner with values, add select item listener */
        mOptionSp = (Spinner) findViewById(R.id.spinner);
        List<String> items = new ArrayList(Arrays.asList(getResources().getStringArray(R.array.point_options)));
        mOptionSpAd = new ArrayAdapter(this, android.R.layout.simple_spinner_item, items);
        mOptionSpAd.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mOptionSp.setAdapter(mOptionSpAd);

        /* Set round and remaining throws text with variable */
        mCurrentRoundTv = findViewById(R.id.currentRound);
        updateRound();
        mRemainingThrowsTv = findViewById(R.id.remainingThrows);
        updateRemainingThrows();

        /* Add dice to list */
        for(int i = 0; i < 6; i++){
            Dice dice = new Dice(6);
            dice.addObserver(this);

            mDiceList.add(dice);
        }

        /* Add diceIVs to list */
        mDiceIVList.add(findViewById(R.id.diceView1));
        mDiceIVList.add(findViewById(R.id.diceView2));
        mDiceIVList.add(findViewById(R.id.diceView3));
        mDiceIVList.add(findViewById(R.id.diceView4));
        mDiceIVList.add(findViewById(R.id.diceView5));
        mDiceIVList.add(findViewById(R.id.diceView6));

        /* Add click handlers to diceIVs */
        for(ImageView dice : mDiceIVList){
            dice.setOnClickListener(v -> {
                ImageView diceIV = v.findViewById(v.getId());
                int pos = mDiceIVList.indexOf(diceIV);
                Dice dice1 = mDiceList.get(pos);

                dice1.toggleLocked();

                if(dice1.getLocked()){
                    diceIV.setAlpha(0.3f);
                }else{
                    diceIV.setAlpha(1f);
                }
            });
        }

        results = new ResultModel(getString(R.string.point_option_low));

        /* Set click handler for action button */
        mBtn = findViewById(R.id.button);
        mBtn.setOnClickListener(this);

        throwDice();
    }

    public void updateRound(){
        mCurrentRoundTv.setText(getString(R.string.current_round, currentRound));
    }

    public void updateRemainingThrows(){
        mRemainingThrowsTv.setText(getString(R.string.remaining_throws, remainingThrows));
    }

    public void throwDice(){
        for(int i = 0; i < mDiceList.size(); i++){
            if(!mDiceList.get(i).getLocked()){
                mDiceList.get(i).throwDice();
            }
        }

        remainingThrows--;
        updateRemainingThrows();
    }

    @Override
    public void update(Observable o, Object arg) {
        Dice dice = (Dice) o;
        int pos = mDiceList.indexOf(dice);

        mDiceIVList.get(pos).setImageResource(mDiceDrawable[mDiceList.get(pos).getValue()-1]);
    }

    @Override
    public void onClick(View v) {
        //Throw/Calc point
        if(remainingThrows > 0){
            throwDice();

            if(remainingThrows == 0){
                mBtn.setText(R.string.button_score);
            }
        }else{
            CharSequence selectedItem = (CharSequence) mOptionSp.getSelectedItem();
            mOptionSpAd.remove(selectedItem);

            int[] diceValues = new int[6];
            for(int i = 0; i < mDiceList.size(); i++){
                Dice dice = mDiceList.get(i);
                diceValues[i] = dice.getValue();
                dice.setLocked(false);
                mDiceIVList.get(i).setAlpha(1f);
            }

            results.addResult(selectedItem, diceValues, diceValues.length);

            if(currentRound == 10){
                Intent intent = new Intent(this, ResultActivity.class);
                intent.putExtra(getString(R.string.EXTRA_OPTION_SCORES), results);
                startActivity(intent);
                this.finish();
            }else{
                remainingThrows = 3;
                throwDice();
                updateRemainingThrows();
                mBtn.setText(R.string.button_throw);
                currentRound++;
                updateRound();
            }
        }
    }
}