package javautils.parser;

import javautils.Text;

import java.util.Map;

public interface Parsable {
    String getFormat();

    Map<String, String> getTokens();

    default String describe() {
        final Map<String, String> tokensMap = getTokens();
        final String format = getFormat();
        return Text.substituteTokens(tokensMap, format);
    }

    default void parseFromText(final String input) {
        final String format = getFormat();
        final Map<String, String> tokens = Text.extractTokens(format, input);
        try {
            setTokens(tokens);
        } catch (Exception e) {
            throwParsingError(format, tokens, input, e);
        }
    }

    void setTokens(final Map<String, String> tokens);

    static String describeMap(final Map<String, String> tokens) {
        final StringBuilder sb = new StringBuilder();
        for(Map.Entry<String, String> entry : tokens.entrySet()) {
            sb.append(" " + entry.getKey() + " : " + entry.getValue() + "\n");
        }
        return sb.toString();
    }

    default void throwParsingError(final String format, final Map<String, String> tokens, final String input, Exception cause) {
        final String tokensOutput = describeMap(tokens);
        final String message = String.format(
                "Failed to parse the following tokens from the given text\n" +
                "===== input =====\n" +
                "%s\n" +
                "===== format =====\n" +
                "%s\n" +
                "===== All tokens x %d =====\n" +
                "%s",
                input,
                format,
                tokens.size(),
                tokensOutput
            );
            throw new RuntimeException(message, cause);
    }
}
