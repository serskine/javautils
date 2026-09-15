package javautils.parser;

import javautils.Text;

import java.util.Map;

public interface Parser<T> {

    String getFormat();

    Map<String, String> getTokens(T item);

    default String describe(T item) {
        final Map<String, String> tokensMap = getTokens(item);
        final String format = getFormat();
        return Text.substituteTokens(tokensMap, format);
    }

    T createNewItem();

    default T parseFromText(final String input) {
        final T item = createNewItem();
        parseFromText(item, input);
        return item;
    }

    default T parseFromText(final T item, final String input) {
        final String format = getFormat();
        final Map<String, String> tokens = Text.extractTokens(format, input);
        try {
            setTokens(item, tokens);
        } catch (Exception e) {
            throwParsingError(format, tokens, input, e);
        }
        return item;
    }

    void setTokens(final T item, final Map<String, String> tokens);

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
