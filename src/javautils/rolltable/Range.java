package javautils.rolltable;

import javautils.parser.Parsable;

import java.util.HashMap;
import java.util.Map;

public class Range {
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

}
