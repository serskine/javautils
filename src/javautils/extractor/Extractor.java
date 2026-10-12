package javautils.extractor;

import javautils.math.Range;
import javautils.parser.Parser;

import java.util.*;

public interface Extractor<T> {
    Parser<T> getParser();
    List<Range> findAll(String content);

    default Map<Range, T> extractAll(final String content) {
        final Map<Range, T> map = new LinkedHashMap<>();  // We want to preserve the order if possible.
        final List<Range> allPositions = findAll(content);
        final Parser<T> parser = getParser();
        for(Range r : allPositions) {
            final String token = content.substring(r.start.intValue(), r.end.intValue());
            final T value = parser.parse(token);
            if (value != null) {
                map.put(r, value);
            }
        }
        return map;
    }
}
