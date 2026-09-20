package javautils.parser;

import java.util.Map;

public final class UnsupportedParser<T> implements Parser<T> {
    @Override
    public T parse(String input) {
        throw new UnsupportedOperationException("UnsupportedParser: Parsing not supported yet.");
    }

    @Override
    public String describe(T element) {
        throw new UnsupportedOperationException("UnsupportedParser: Describing not supported yet.");
    }
}
