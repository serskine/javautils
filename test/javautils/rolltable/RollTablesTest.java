package javautils.rolltable;

import javautils.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.HashMap;
import java.util.Map;

public class RollTablesTest {


    private Map<String, Integer> results;
    private RollTables rollTables;

    @Before
    public void onSetup() {
        rollTables = new RollTables();

        final Table rarityTable = rollTables.createTable("rarity",
            new Roll(0, new Die(6)),
            new Record(new Range(1,3), "common", 1, false),
            new Record(new Range(4,2), "uncommon", 1, false),
            new Record(new Range(6,1), "rare", 1, false)
        );
    }

    @Test
    public void rarityTable() {
        final Map<String, Integer> results = rollTables.rollResultsFor("rarity", 60000);
        Logger.info(describeMap(results));
    }

    @After
    public void onTearDown() {

    }

    String describeMap(final Map<String, Integer> map) {
        final StringBuilder sb = new StringBuilder();
        sb.append("\n{\n");
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            sb.append(" - " + entry.getValue() + " x " + entry.getKey() + "\n");
        }
        sb.append("}\n");
        return sb.toString();
    }
}
