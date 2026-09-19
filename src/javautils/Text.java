package javautils;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Text {

    public static String asTable(final String[][] t) {
        if (t == null || t.length == 0) {
            return "";
        }

        int numRows = t.length;
        int numCols = 0;
        for (String[] row : t) {
            if (row != null) {
                numCols = Math.max(numCols, row.length);
            }
        }

        final Map<Integer, Integer> colWidth = new HashMap<>();
        final Map<Integer, Integer> rowHeight = new HashMap<>();

        for (int row = 0; row < numRows; row++) {
            final String[] r = (t[row] == null) ? new String[0] : t[row];
            for (int col = 0; col < numCols; col++) {
                final String token = (col < r.length) ? r[col] : null;
                final Dimension d = getTextSize(token);
                rowHeight.put(row, Math.max(rowHeight.getOrDefault(row, 0), d.height));
                colWidth.put(col, Math.max(colWidth.getOrDefault(col, 0), d.width));
            }
        }

        final List<String> renderedRows = new ArrayList<>();
        for (int row = 0; row < numRows; row++) {
            final String[] r = (t[row] == null) ? new String[0] : t[row];
            final List<String> rowLines = new ArrayList<>();

            final int maxCellHeight = rowHeight.getOrDefault(row, 0);
            for (int lineNum = 0; lineNum < maxCellHeight; lineNum++) {
                rowLines.add("");
            }

            for (int col = 0; col < numCols; col++) {
                final String token = (col < r.length) ? r[col] : null;
                final Dimension cellSize = new Dimension(
                        colWidth.getOrDefault(col, 0),
                        rowHeight.getOrDefault(row, 0)
                );
                final String[] cellLines = cellString(cellSize, token).split("\n", -1);

                for (int lineNum = 0; lineNum < maxCellHeight; lineNum++) {
                    final String prefix = rowLines.get(lineNum);
                    final String cellLine = (lineNum < cellLines.length) ? cellLines[lineNum] : "";
                    rowLines.set(lineNum, prefix + (prefix.isEmpty() ? "" : " ") + cellLine);
                }
            }

            String rowText = String.join("\n", rowLines);
            rowText = rowText.replaceFirst("\\s+$", "");
            renderedRows.add(rowText);
        }

        final StringBuilder sb = new StringBuilder();
        for (String rowText : renderedRows) {
            sb.append(rowText).append('\n');
        }
        return sb.toString();
    }

    public static Dimension getTextSize(String text) {
        if (text == null) {
            return new Dimension(0, 0);
        }

        final String[] lines = text.split("\n", -1);
        int maxCols = 0;
        for (String line : lines) {
            maxCols = Math.max(maxCols, line.length());
        }
        return new Dimension(maxCols, lines.length);
    }

    public static String cellString(final Dimension size, final String text) {
        if (size == null) {
            return "";
        }

        final String value = (text == null) ? "" : text;
        final String[] lines = value.split("\n", -1);

        final StringBuilder sb = new StringBuilder();
        for (int row = 0; row < size.height; row++) {
            String line = (row < lines.length) ? lines[row] : "";
            if (line.length() > size.width) {
                line = line.substring(0, size.width);
            }
            line += pad(" ", size.width - line.length());
            sb.append(line);
            if (row + 1 < size.height) {
                sb.append('\n');
            }
        }
        return sb.toString();
    }

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

    public static String substituteTokens(final Map<String, String> tokenMap, String output) {
        for(Map.Entry<String, String> e : tokenMap.entrySet()) {
            final String target = "{" + e.getKey() + "}";
            final String replacement = e.getValue();
            output = output.replace(target, e.getValue());
        }
        return output;
    }

    public static java.util.Map<String, String> extractTokens(final String format, final String input) {
        if (format == null || input == null) {
            return java.util.Collections.emptyMap();
        }

        // Parse the format into literal pieces and token names, and build a regex.
        final java.util.List<String> tokenNames = new java.util.ArrayList<>();
        final StringBuilder regex = new StringBuilder();
        int idx = 0;
        final int len = format.length();

        while (idx < len) {
            int open = format.indexOf('{', idx);
            if (open == -1) {
                // remaining literal
                String literal = format.substring(idx);
                if (!literal.isEmpty()) {
                    regex.append(java.util.regex.Pattern.quote(literal));
                }
                idx = len;
                break;
            }
            // append literal before '{'
            if (open > idx) {
                String literal = format.substring(idx, open);
                regex.append(java.util.regex.Pattern.quote(literal));
            }
            int close = format.indexOf('}', open + 1);
            if (close == -1) {
                throw new IllegalArgumentException("Unclosed token in format: " + format);
            }
            String name = format.substring(open + 1, close);
            if (name.isEmpty()) {
                throw new IllegalArgumentException("Empty token name in format: " + format);
            }
            tokenNames.add(name);

            // choose a non-greedy group for intermediate tokens; greedy for last token
            if (close == len - 1) {
                // token at end -> capture everything (including empty)
                regex.append("(.*)");
            } else {
                // non-greedy to allow following literal to match
                regex.append("(.*?)");
            }
            idx = close + 1;
        }

        // Anchor pattern to entire input
        final java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(
                "^" + regex.toString() + "$",
                java.util.regex.Pattern.DOTALL
        );
        final java.util.regex.Matcher matcher = pattern.matcher(input);
        if (!matcher.matches()) {
            return java.util.Collections.emptyMap();
        }

        final java.util.Map<String, String> result = new java.util.LinkedHashMap<>();
        for (int i = 0; i < tokenNames.size(); i++) {
            String name = tokenNames.get(i);
            String value = matcher.group(i + 1);
            // If same token appears more than once, ensure consistency
            if (result.containsKey(name)) {
                String prev = result.get(name);
                if (!java.util.Objects.equals(prev, value)) {
                    // conflicting values for same token name -> treat as no match
                    return java.util.Collections.emptyMap();
                }
            } else {
                result.put(name, value);
            }
        }
        return result;
    }
}
