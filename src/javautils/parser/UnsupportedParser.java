package javautils.parser;

import java.util.Map;

public class UnsupportedParser<T> implements Parser<T> {
    @Override
    public String getFormat() {
        throw new UnsupportedOperationException("Parsing is currently not supported.");
    }

    @Override
    public Map<String, String> getTokens(T item) {
        throw new UnsupportedOperationException("Parsing is currently not supported.");
    }

    @Override
    public T createNewItem() {
        throw new UnsupportedOperationException("Parsing is currently not supported.");
    }

    @Override
    public void setTokens(T item, Map<String, String> tokens) {
        throw new UnsupportedOperationException("Parsing is currently not supported.");
    }
}
