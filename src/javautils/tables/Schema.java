package javautils.tables;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Schema extends Domain<String> {
    public final Map<String, Table> tableIndex = new HashMap<>();

    public Schema(String name, Table... tables) {
        super(name);
        for(Table table : tables) {
            tableIndex.put(table.id, table);
        }
    }

}
