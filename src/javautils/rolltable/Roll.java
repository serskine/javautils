package javautils.rolltable;

import javautils.parser.Parsable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Roll implements Parsable {
    private final List<Die> dice = new ArrayList<>();
    private int modifier;
    private int value;

    public Roll(final int modifier, final Die... dice) {
        this.modifier = modifier;
        for(Die die : dice) {
            this.dice.add(die);
        }
    }

    public int getValue() {
        return this.value;
    }

    public int roll() {
        value = modifier;
        for(Die die : dice) {
            value += die.roll();
        }
        return value;
    }

    public Die getMax() {
        return dice.stream().max((a, b) -> Integer.compare(a.getValue(), b.getValue())).orElseThrow();
    }

    public Die getMin() {
        return dice.stream().min((a, b) -> Integer.compare(a.getValue(), b.getValue())).orElseThrow();
    }

    @Override
    public String getFormat() {
        return "{rolls} + {modifier}";
    }

    @Override
    public Map<String, String> getTokens() {
        return Map.of();
    }

    @Override
    public void setTokens(Map<String, String> tokens) {
        this.modifier = Integer.parseInt(tokens.get("modifier"));

    }
}
