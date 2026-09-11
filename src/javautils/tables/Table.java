package javautils.tables;

import java.util.ArrayList;
import java.util.List;

public class Table extends Domain<String> {

    public final List<Record> rows = new ArrayList<>();

    public Table(String name, Record... records) {
        super(name);
        for(Record r : records) {
            rows.add(r);
        }
    }

    public List<Record> getMatchingRecordsForRoll(int roll) {
        return rows.stream().filter(row -> row.getRange().contains(roll)).toList();
    }
}
