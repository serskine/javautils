package javautils.rolltable;


public class Record {
    private HitRange range;
    private String result;
    private int qty;
    private boolean isReroll;

    public static final String FORMAT = "{range}{qty} x {result} isReroll={isReroll}";

    public Record(final HitRange range, final String result, final int qty, final boolean isReroll) {
        this.range = range;
        this.result = result;
        this.qty = qty;
        this.isReroll = isReroll;
    }

    public HitRange getRange() {
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
