package com.konradrej.thirty;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.Random;

/**
 * Handles dice state and actions.
 *
 * @author Konrad Rej
 */
public class Dice implements Parcelable {
    private static final Random sNumberGenerator = new Random();

    private final int sideAmount;
    private boolean locked;
    private int value;

    public Dice(int sideAmount) {
        this.sideAmount = sideAmount;
        this.throwDice();
    }

    protected Dice(Parcel in) {
        sideAmount = in.readInt();
        locked = in.readByte() != 0;
        value = in.readInt();
    }

    public static final Creator<Dice> CREATOR = new Creator<Dice>() {
        @Override
        public Dice createFromParcel(Parcel in) {
            return new Dice(in);
        }

        @Override
        public Dice[] newArray(int size) {
            return new Dice[size];
        }
    };

    /**
     * Generates new dice value.
     */
    public void throwDice() {
        value = sNumberGenerator.nextInt(sideAmount) + 1;
    }

    public int getValue() {
        return value;
    }

    public boolean getLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public void toggleLocked() {
        locked = !locked;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(sideAmount);
        dest.writeByte((byte) (locked ? 1 : 0));
        dest.writeInt(value);
    }
}
