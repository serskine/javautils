package javautils.parser;

import javautils.Text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ParsableCollection<T> implements Parsable {

    private Collection<T> collection;

    public ParsableCollection(Collection<T> c) {
        this.collection = c;
    }

    @Override
    public String getFormat() {
        return "{size} x items\n{items}";
    }

    static final String indexKey(int index) {
        return "[" + index + "]";
    }

    @Override
    public Map<String, String> getTokens() {
        final Map<String, String> map = new HashMap<>();
        map.put("size", Integer.toString(collection.size()));

        final String formatItem = "[{index}]: {value}";

        final StringBuilder sb = new StringBuilder();
        int i=0;
        for(Object item : this.collection) {
            final Map<String, String> rowTokensMap = new HashMap<>();
            rowTokensMap.put("index", indexKey(i));
            rowTokensMap.put("value", ParsableObj.create(item).describe());
            final String row = Text.substituteTokens(rowTokensMap, formatItem);
            sb.append(row);
        }

        map.put("value", sb.toString());
        return map;
    }

    @Override
    public void setTokens(Map<String, String> tokens) {
        int size = Integer.parseInt(tokens.get("size"));
        this.collection.clear();
        for(int i=0; i<size; i++) {
            final String key = indexKey(i);
            final String keyValue = tokens.get(key);
            final ParsableObj parsableObj = ParsableObj.create(null);
            parsableObj.parseFromText(keyValue);
            final T item = parsableObj.getItem();
            this.collection.add(item);
        }
    }
}
