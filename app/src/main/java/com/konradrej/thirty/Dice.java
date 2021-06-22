package com.konradrej.thirty;

import java.util.Observable;
import java.util.Random;

public class Dice extends Observable {
    private static final Random sNumberGenerator = new Random();

    private final int mSideAmount;
    private boolean mLocked;
    private int mValue;

    public Dice(int sideAmount){
        mSideAmount = sideAmount;
        this.throwDice();
    }

    public void throwDice(){
        mValue = sNumberGenerator.nextInt(mSideAmount) + 1;

        setChanged();
        notifyObservers(mValue);
    }

    public int getValue(){
        return mValue;
    }

    public boolean getLocked(){
        return mLocked;
    }

    public void setLocked(boolean locked){
        mLocked = locked;
    }

    public void toggleLocked(){
        mLocked = !mLocked;
    }
}
