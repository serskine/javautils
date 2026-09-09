package javautils.parser;

import javautils.Text;

import java.util.Map;

public interface Parsable {
    String getFormat();

    Map<String, String> getTokens();

    default String describe() {
        return Text.substituteTokens(getTokens(), getFormat());
    }

    default void parseFromText(final String input) {
        final Map<String, String> tokens = Text.extractTokens(getFormat(), input);
        setTokens(tokens);
    }

    void setTokens(final Map<String, String> tokens);
}
