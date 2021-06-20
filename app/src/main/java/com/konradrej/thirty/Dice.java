package com.konradrej.thirty;

import java.util.Random;

public class Dice {
    private static final Random numberGenerator = new Random();

    private int sides;
    private int value;

    public Dice(){
        this(6);
    }

    public Dice(int sides){
        this.sides = sides;
        throwDice();
    }

    public void throwDice(){
        value = numberGenerator.nextInt(sides) + 1;
    }

    public int getValue(){
        return value;
    }
}
