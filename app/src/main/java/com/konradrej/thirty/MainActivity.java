package com.konradrej.thirty;

import androidx.appcompat.app.ActionBar;
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
import java.util.Collection;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class MainActivity extends AppCompatActivity implements Observer, View.OnClickListener {
    private final ArrayList<Dice> mDiceList = new ArrayList<>();
    private final List<ImageView> mDiceIvList = new ArrayList<>();
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
    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);

        bundle.putSerializable("mDiceList", mDiceList);
        bundle.putSerializable("results", results);

        ArrayList<CharSequence> mOptionSpAdValues = new ArrayList<>();
        for(int i = 0; i < mOptionSpAd.getCount(); i++){
            mOptionSpAdValues.add(mOptionSpAd.getItem(i));
        }
        bundle.putSerializable("mOptionSpAdValues", mOptionSpAdValues);
        bundle.putInt("mOptionSpPos", mOptionSp.getSelectedItemPosition());

        bundle.putInt("currentRound", currentRound);
        bundle.putInt("remainingThrows", remainingThrows);
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_main);

        ActionBar bar = getSupportActionBar();
        if(bar != null){
            bar.hide();
        }

        // Get mCurrentRoundTv and update it
        mCurrentRoundTv = findViewById(R.id.currentRound);
        if(bundle != null && bundle.containsKey("currentRound")){
            currentRound = bundle.getInt("currentRound");
        }
        updateRound();

        // Get mRemainingThrowsTv and update it
        mRemainingThrowsTv = findViewById(R.id.remainingThrows);
        if(bundle != null && bundle.containsKey("remainingThrows")){
            remainingThrows = bundle.getInt("remainingThrows");
        }
        updateRemainingThrows();

        // Initiate results
        if(bundle != null && bundle.containsKey("results")){
            results = (ResultModel) bundle.getSerializable("results");
        }else{
            results = new ResultModel(getString(R.string.point_option_low));
        }

        // Get mBtn and set OnClickListener for all dice imageViews
        mBtn = findViewById(R.id.button);
        mBtn.setOnClickListener(this);

        initializeSpinner(bundle);
        initializeDice(bundle);
    }

    private void updateRound(){
        mCurrentRoundTv.setText(getString(R.string.current_round, currentRound));
    }

    private void updateRemainingThrows(){
        mRemainingThrowsTv.setText(getString(R.string.remaining_throws, remainingThrows));
    }

    private void updateDiceIv(Dice dice, ImageView diceIv){
        if(dice.getLocked()){
            diceIv.setAlpha(0.3f);
        }else{
            diceIv.setAlpha(1f);
        }
    }

    private void initializeSpinner(Bundle bundle){
        // Get mOptionSp and populate it with values
        mOptionSp = findViewById(R.id.spinner);
        if(bundle != null && bundle.containsKey("mOptionSpPos")){
            mOptionSp.setSelection(bundle.getInt("mOptionSpPos"));
        }
        if(bundle != null && bundle.containsKey("mOptionSpAdValues")){
            List<String> items = (ArrayList<String>) bundle.getSerializable("mOptionSpAdValues");
            mOptionSpAd = new ArrayAdapter(this, android.R.layout.simple_spinner_item, items);
        }else{
            List<String> items = new ArrayList<>(
                    Arrays.asList(getResources().getStringArray(R.array.point_options))
            );
            mOptionSpAd = new ArrayAdapter(this, android.R.layout.simple_spinner_item, items);
        }
        mOptionSpAd.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mOptionSp.setAdapter(mOptionSpAd);
    }

    private void initializeDice(Bundle bundle){
        // Add new dice to mDiceList
        if(bundle != null && bundle.containsKey("mDiceList")){
            mDiceList.addAll((ArrayList<Dice>) bundle.getSerializable("mDiceList"));
        }else{
            for(int i = 0; i < 6; i++){
                Dice dice = new Dice(6);
                dice.addObserver(this);

                mDiceList.add(dice);
            }
        }

        // Populate mDiceIvList
        mDiceIvList.add(findViewById(R.id.diceView1));
        mDiceIvList.add(findViewById(R.id.diceView2));
        mDiceIvList.add(findViewById(R.id.diceView3));
        mDiceIvList.add(findViewById(R.id.diceView4));
        mDiceIvList.add(findViewById(R.id.diceView5));
        mDiceIvList.add(findViewById(R.id.diceView6));

        // Set OnClickListener for all dice imageViews
        for(ImageView dice : mDiceIvList){
            dice.setOnClickListener(v -> {
                ImageView diceIv = v.findViewById(v.getId());
                int pos = mDiceIvList.indexOf(diceIv);

                Dice localDice = mDiceList.get(pos);
                localDice.toggleLocked();

                updateDiceIv(localDice, diceIv);
            });
        }

        // Perform first throw
        throwDice();
    }

    private void throwDice(){
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

        mDiceIvList.get(pos).setImageResource(mDiceDrawable[mDiceList.get(pos).getValue()-1]);
    }






    @Override
    public void onClick(View v) {
        if(remainingThrows > 0){
            if(remainingThrows == 1){
                mBtn.setText(R.string.button_score);
            }

            throwDice();
        }else{
            CharSequence selectedItem = (CharSequence) mOptionSp.getSelectedItem();
            mOptionSpAd.remove(selectedItem);

            int[] diceValues = new int[6];
            for(int i = 0; i < mDiceList.size(); i++){
                Dice dice = mDiceList.get(i);
                diceValues[i] = dice.getValue();

                dice.setLocked(false);
                updateDiceIv(dice, mDiceIvList.get(i));
            }

            results.addResult(selectedItem, diceValues, diceValues.length);

            if(currentRound == 10){
                /*
                Intent intent2 = new Intent(this, MainActivity.class);
                this.finish();
                startActivity(intent2);

                Intent intent = new Intent(this, ResultActivity.class);
                intent.putExtra(getString(R.string.EXTRA_OPTION_SCORES), results);
                startActivity(intent);
                */
                Intent intent = new Intent(this, ResultActivity.class);
                intent.putExtra(getString(R.string.EXTRA_OPTION_SCORES), results);
                startActivity(intent);

                startNewGame();
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

    private void startNewGame(){
        currentRound = 1;
        updateRound();

        remainingThrows = 3;
        updateRemainingThrows();

        for(int i = 0; i < mDiceList.size(); i++){
            Dice dice = mDiceList.get(i);
            dice.setLocked(false);
            updateDiceIv(dice, mDiceIvList.get(i));
        }

        initializeSpinner(null);

        results = new ResultModel(getString(R.string.point_option_low));
    }
}