package javautils.rolltable;

import java.util.Random;

public class Die {
    private static Random R = new Random();

    private int size;
    private int value;

    public Die(final int size) {
        this.size = size;
        roll();
    }

    public int roll() {
        value = R.nextInt(size) + 1;
        return value;
    }

    public int getValue() {
        return this.value;
    }

}
