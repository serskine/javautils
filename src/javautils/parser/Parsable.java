package javautils.parser;

import javautils.Text;

import java.util.Map;

public interface Parsable<T> {
    Parser<T> getParser();

    default String describe() {
        return getParser().describe((T) this);
    }
}
