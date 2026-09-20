package javautils.parser;


import javautils.math.Vector;
import javautils.math.VectorImpl;

import java.util.Collection;
import java.util.List;

public class VectorParser implements Parser<Vector> {

    private static final String TAG_OPEN = "[";
    private static final String TAG_CLOSE = "]";
    private static final String TAG_DELIM = ", ";
    private static final Parser<Double> DOUBLE_PARSER = Parser.getDoubleParser(0D);
    private static final ArrayParser<Double> DOUBLE_ARRAY_PARSER = new ArrayParserImpl<>(DOUBLE_PARSER, TAG_OPEN, TAG_CLOSE, TAG_DELIM);

    @Override
    public Vector parse(String input) {
        final List<Double> values = DOUBLE_ARRAY_PARSER.parse(input);
        final Vector vector = new VectorImpl().init(values);

        return vector;
    }

    @Override
    public String describe(Vector vector) {
        return DOUBLE_ARRAY_PARSER.describe(vector.stream().toList());
    }
}

