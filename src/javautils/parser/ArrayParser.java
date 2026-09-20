package javautils.parser;

import javautils.math.Matrix;
import javautils.math.MatrixImpl;
import javautils.math.Vector;
import javautils.math.VectorImpl;

import java.util.*;
import java.util.stream.Collectors;

public interface ArrayParser<Element> extends Parser<List<Element>> {

    Parser<Element> getElementParser();

    String getOpenTag();
    String getCloseTag();
    String getDelim();

    default String describe(final Element[] elements) {
        if (elements == null) {
            return getOpenTag() + getCloseTag();
        }

        return getOpenTag()
            + Arrays.stream(elements)
                    .map(getElementParser()::describe)
                    .collect(Collectors.joining(getDelim()))
            + getCloseTag();
    }

    default String describe(final List<Element> elements) {
         if (elements == null) {
            return getOpenTag() + getCloseTag();
        }

        return getOpenTag()
            + elements.stream()
                    .map(getElementParser()::describe)
                    .collect(Collectors.joining(getDelim()))
            + getCloseTag();
    }

    default List<Element> parse(final String input) {
        if (!input.startsWith(getOpenTag())
                || !input.endsWith(getCloseTag())) {
            throw new IllegalArgumentException(
                "Invalid array format: " + input);
        }

        String content = input.substring(
            getOpenTag().length(),
            input.length() - getCloseTag().length());

        if (content.isEmpty()) {
            return new ArrayList<>();
        }

        String[] parts = content.split(
            java.util.regex.Pattern.quote(getDelim()));

        List<Element> result = new ArrayList<>();

        for (int i = 0; i < parts.length; i++) {
            result.add(getElementParser().parse(parts[i]));
        }

        return result;
    }

}