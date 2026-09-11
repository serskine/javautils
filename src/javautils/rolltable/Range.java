package javautils.rolltable;

import javautils.parser.Parsable;

import java.util.HashMap;
import java.util.Map;

public class Range implements Parsable {
    private int start, size;

    public Range(int start, int size) {
        if (size<0) {
            this.start = start + size;
            this.size = Math.abs(size);
        } else {
            this.start = start;
            this.size = size;
        }
    }

    public final int getStart() {
        return this.start;
    }

    public final int getSize() {
        return this.size;
    }

    public final int getEnd() {
        return getStart() + getSize();
    }

    public final boolean contains(int value) {
        return (value >= getStart()) && (value < getEnd());
    }


    @Override
    public String getFormat() {
        return "[{start} -> {end}]";
    }

    @Override
    public Map<String, String> getTokens() {
        final Map<String,String> map = new HashMap<>();
        map.put("start", "" + getStart());
        map.put("end", "" + getEnd());
        return map;
    }

    @Override
    public void setTokens(Map<String, String> tokens) {
        final String start = tokens.get("start");
        final String end = tokens.get("end");
        this.start = Integer.parseInt(start);
        this.size = Integer.parseInt(end) - this.start;
    }
}
