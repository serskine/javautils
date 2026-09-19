package javautils.parser;

import javautils.Logger;
import javautils.Text;

import java.util.Map;

public interface Parser<T> {

    default String openTokenParam() { return "{";   }
    default String closeTokenParam() { return "}";  }
    default String paramToken(final String name) { return openTokenParam() + name + closeTokenParam();}


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
        final Map<String, String> tokens = Text.extractTokens(format, input, openTokenParam(), closeTokenParam());
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

    default Double parseDoubleOrDefault(final String string, final Double dValue) {
        try {
            return Double.parseDouble(string);
        } catch (NumberFormatException e) {
            Logger.warn(e.getMessage(), e);
            return dValue;
        }
    }

    default Float parseFloatOrDefault(final String string, final Float dValue) {
        try {
            return Float.parseFloat(string);
        } catch (NumberFormatException e) {
            Logger.warn(e.getMessage(), e);
            return dValue;
        }
    }

    default Boolean parseBooleanOrDefault(final String string, Boolean dValue) {
        try {
            return Boolean.parseBoolean(string);
        } catch (NumberFormatException e) {
            Logger.warn(e.getMessage(), e);
            return dValue;
        }
    }

    default Byte parseByteOrDefault(final String string, Byte dValue) {
        try {
            return Byte.parseByte(string);
        } catch (Exception e) {
            Logger.warn(e.getMessage(), e);
            return dValue;
        }
    }

    default Short parseShortOrDefault(final String string, Short dValue) {
        try {
            return Short.parseShort(string);
        } catch (Exception e) {
            Logger.warn(e.getMessage(), e);
            return dValue;
        }
    }

    default Integer parseIntegerOrDefault(final String string, Integer dValue) {
        try {
            return Integer.parseInt(string);
        } catch (Exception e) {
            Logger.warn(e.getMessage(), e);
            return dValue;
        }
    }

    default Long parseLongOrDefault(final String string, Long dValue) {
        try {
            return Long.parseLong(string);
        } catch (Exception e) {
            Logger.warn(e.getMessage(), e);
            return dValue;
        }
    }

    default Character parseCharacterOrDefault(final String string, final Character dValue) {
        try {
            return string.charAt(0);
        } catch (Exception e) {
            Logger.warn(e.getMessage(), e);
            return dValue;
        }
    }


}
