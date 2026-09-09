package javautils.rolltable;

import javautils.parser.Parsable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Table {
    public final List<Record> records = new ArrayList<>();
    private Roll rollFormula;

    public Table(final Roll roll, Record... records) {
        this.rollFormula = roll;
        for(Record r : records) {
            this.records.add(r);
        }
    }

    public List<Record> roll() {
        this.rollFormula.roll();
        return getRecords();
    }

    public List<Record> getRecords() {
        return getRecordsFor(rollFormula.getValue());
    }

    public List<Record> getRecordsFor(final int rollValue) {
        return records.stream().filter(r -> r.getRange().contains(rollValue)).toList();
    }

}
