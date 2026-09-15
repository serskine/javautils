package javautils.rolltable;

import javautils.Text;
import javautils.parser.Parsable;

import java.util.HashMap;
import java.util.Map;

public class Record implements Parsable {
    private Range range;
    private String result;
    private int qty;
    private boolean isReroll;

    public static final String FORMAT = "{range}{qty} x {result} isReroll={isReroll}";

    public Record(final Range range, final String result, final int qty, final boolean isReroll) {
        this.range = range;
        this.result = result;
        this.qty = qty;
        this.isReroll = isReroll;
    }

    public Range getRange() {
        return range;
    }

    public String getResult() {
        return result;
    }

    public int getQty() {
        return qty;
    }

    public boolean isReroll() {
        return isReroll;
    }


}
