package javautils.rolltable;

import java.util.*;

public class RollTables {
    private Map<String, Table> tableMap = new HashMap<>();

    public final Table createTable(final String name, final Roll roll, Record... initialRecords) {
        final Table table = new Table(roll, initialRecords);
        tableMap.put(name, table);
        return table;
    }

    protected List<Record> rollOnTable(final String tableName) {
        final Table t = tableMap.get(tableName);
        if (t==null) {
            return new ArrayList<>();
        } else {
            return processResults(t.roll());
        }
    }

    protected List<Record> processResults(List<Record> records) {
        final List<Record> newResults = new ArrayList<>();
        for(Record r : records) {
            newResults.addAll(processResult(r));
        }
        return newResults;
    }

    protected List<Record> processResult(Record r) {
        final List<Record> processedResults = new ArrayList<>();
        if (r.isReroll()) {
            for(int i=0; i<r.getQty(); i++) {
                final List<Record> rerolls = rollOnTable(r.getResult());
                processedResults.addAll(rerolls);
            }
        } else {
            processedResults.add(r);
        }
        return processedResults;
    }

    protected final Map<String, Integer> getResultsFor(List<Record> records) {
        final Map<String, Integer> resultCount = new HashMap<>();
        for(Record r : records) {
            int count = resultCount.getOrDefault(r.getResult(), 0);
            count += r.getQty();
            resultCount.put(r.getResult(), count);
        }
        return resultCount;
    }

    public final Map<String, Integer> rollResultsFor(final String tableName) {
        return rollResultsFor(tableName, 1);
    }

    public final Map<String, Integer> rollResultsFor(final String tableName, final int numRolls) {
        final List<Record> results = new ArrayList<>();
        for(int i=0; i<numRolls; i++) {
            results.addAll(rollOnTable(tableName));
        }
        return getResultsFor(results);
    }
}
