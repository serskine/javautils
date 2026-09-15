package javautils.parser;

import javautils.Text;

import java.util.Map;

public interface Parsable<T> {
    default Parser<T> getParser() {
        return new UnsupportedParser<>();
    }

}
