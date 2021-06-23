package com.konradrej.thirty;

import java.io.Serializable;
import java.util.Random;

/**
 * Handles dice state and actions.
 *
 * @author Konrad Rej
 */
public class Dice implements Serializable {
    private static final Random sNumberGenerator = new Random();

    private final int sideAmount;
    private boolean locked;
    private int value;

    public Dice(int sideAmount) {
        this.sideAmount = sideAmount;
        this.throwDice();
    }

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
}
