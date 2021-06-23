package com.konradrej.thirty;

import android.os.Parcel;
import android.os.Parcelable;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Handles game logic and data storage.
 *
 * @author Konrad Rej
 */
public class GameModel implements Parcelable {
    private final int THROW_AMOUNT = 3;

    private final PropertyChangeSupport pclSupport = new PropertyChangeSupport(this);
    private int currentRound;
    private int remainingThrows;
    private ResultModel results;
    private ArrayList<Dice> diceList;

    public GameModel() {
        startNewGame();
    }

    protected GameModel(Parcel in) {
        currentRound = in.readInt();
        remainingThrows = in.readInt();
        results = in.readParcelable(ResultModel.class.getClassLoader());
        diceList = in.createTypedArrayList(Dice.CREATOR);
    }

    public static final Creator<GameModel> CREATOR = new Creator<GameModel>() {
        @Override
        public GameModel createFromParcel(Parcel in) {
            return new GameModel(in);
        }

        @Override
        public GameModel[] newArray(int size) {
            return new GameModel[size];
        }
    };

    /**
     * Create and add dice to diceList
     */
    private void initializeDice() {
        // Add new dice to mDiceList
        diceList = new ArrayList<>();

        for (int i = 0; i < 6; i++) {
            Dice dice = new Dice(6);

            diceList.add(dice);
        }
    }

    /**
     * Throws all non locked dice and decreases remaining throw count.
     */
    public void throwDice() {
        for (int i = 0; i < diceList.size(); i++) {
            Dice dice = diceList.get(i);

            if (!dice.getLocked()) {
                dice.throwDice();
                updateDice(dice);
            }
        }

        setRemainingThrows(remainingThrows - 1);
    }

    /**
     * Starts new game by resetting values.
     */
    public void startNewGame() {
        results = new ResultModel();

        setCurrentRound(1);
        setRemainingThrows(THROW_AMOUNT);

        initializeDice();

        // Perform first throw
        throwDice();
    }

    /**
     * Starts new round by resetting remaining throws, incrementing round and throwing dice.
     */
    public void startNewRound() {
        setRemainingThrows(THROW_AMOUNT);
        setCurrentRound(getCurrentRound() + 1);
        throwDice();
    }

    public int getCurrentRound() {
        return currentRound;
    }

    public void setCurrentRound(int currentRound) {
        this.currentRound = currentRound;
        updateCurrentRound();
    }

    public int getRemainingThrows() {
        return remainingThrows;
    }

    public void setRemainingThrows(int remainingThrows) {
        this.remainingThrows = remainingThrows;
        updateRemainingThrows();
    }

    /**
     * Gets dice values and calls ResultModel (results) to store the value.
     *
     * @param selectedItem selected countOption to save result under
     */
    public void saveRoundResult(CharSequence selectedItem) {
        int[] diceValues = new int[6];
        for (int i = 0; i < diceList.size(); i++) {
            Dice dice = diceList.get(i);
            diceValues[i] = dice.getValue();

            dice.setLocked(false);

            updateDice(dice);
        }

        results.addResult(selectedItem, diceValues);
    }

    public ResultModel getResultModel() {
        return results;
    }

    public int getDiceIndex(Dice dice) {
        return diceList.indexOf(dice);
    }

    public Dice getDiceByIndex(int index) {
        return diceList.get(index);
    }

    private void updateCurrentRound() {
        pclSupport.firePropertyChange("currentRound", null, currentRound);
    }

    private void updateRemainingThrows() {
        pclSupport.firePropertyChange("remainingThrows", null, remainingThrows);
    }

    private void updateDice(Dice dice) {
        pclSupport.firePropertyChange("mDiceListSingle", null, dice);
    }

    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        pclSupport.addPropertyChangeListener(pcl);
    }

    public void removePropertyChangeListener(PropertyChangeListener pcl) {
        pclSupport.removePropertyChangeListener(pcl);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(THROW_AMOUNT);
        dest.writeInt(currentRound);
        dest.writeInt(remainingThrows);
        dest.writeParcelable(results, flags);
        dest.writeTypedList(diceList);
    }
}