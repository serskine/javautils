package javautils.math;

import javautils.parser.Parser;

import java.util.Map;

public class VectorParser implements Parser<Vector> {

    @Override
    public String getFormat() {
        return "";
    }

    @Override
    public Map<String, String> getTokens(Vector item) {
        return Map.of();
    }

    @Override
    public Vector createNewItem() {
        return null;
    }

    @Override
    public void setTokens(Vector item, Map<String, String> tokens) {

    }
}
