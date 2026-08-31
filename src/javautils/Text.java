package javautils;

import java.util.Arrays;

public class Text {
    public static String fstring(final int size, final String text) {
        if (text.length() > size) {
            return text.substring(0, size);
        } else {
            return text + pad(" ", size - text.length());
        }
    }

    public static final String[] lines(final String text) {
        return text.split("\n");
    }

    public static String pad(final String token, final int num) {
        final StringBuilder sb = new StringBuilder();
        for(int i=0; i<num; i++) {
            sb.append(token);
        }
        return sb.toString();
    }

    public static String join(final String open, final String delim, final String close, final String... items) {
        final StringBuilder sb = new StringBuilder();
        sb.append(open);
        for(int i=0; i<items.length; i++) {
            if (i>0) {
                sb.append(delim);
            }
            sb.append(items[i]);
        }
        sb.append(close);
        return sb.toString();
    }

    public static String any(String... tokens) {
        return join("(", "||", ")", tokens);
    }

    public static String all(String... tokens) {
        return join("(", "&&", ")", tokens);
    }

    public static String not(String token) {
        return "!" + token;
    }

    public static String concat(String... tokens) {
        return join("", "", "", tokens);
    }

    public static String code(String... statements) {
        return join("{", ";", "}", statements);
    }

}
