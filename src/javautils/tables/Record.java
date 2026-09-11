package javautils.tables;

import javautils.common.Range;

public class Record extends Domain<String> {
    private Range range = Range.NO_RANGE;
    private String title, description;



    public Record(String id, String description) {
        super(id);
        this.description = description;
    }

    public Range getRange() {
        return range;
    }

    public void setRange(Range range) {
        this.range = (range==null) ? Range.NO_RANGE : range;
    }
}
