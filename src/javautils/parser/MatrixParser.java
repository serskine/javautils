package javautils.parser;

import javautils.math.Matrix;
import javautils.math.MatrixImpl;
import javautils.math.Vector;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MatrixParser implements Parser<Matrix> {


    private static final Parser<Double> DOUBLE_PARSER = Parser.getDoubleParser(0D);
    private static final Parser<Integer> INTEGER_PARSER = Parser.getIntegerParser(0);

    private static final String MATRIX_OPEN  = "=======================\n";
    private static final String MATRIX_CLOSE = "\n" + MATRIX_OPEN;
    private static final String ROW_DELIM = "\n";
    private static final VectorParser VECTOR_PARSER = new VectorParser();
    private static final ArrayParser<Vector> VECTOR_ARRAY_PARSER = new ArrayParserImpl<>(VECTOR_PARSER, MATRIX_OPEN, MATRIX_CLOSE, ROW_DELIM);

    @Override
    public Matrix parse(String input) {
        final List<Vector> rowVectors = VECTOR_ARRAY_PARSER.parse(input);
        final Matrix m = new MatrixImpl();
        m.initFromRows(rowVectors);
        return m;
    }

    @Override
    public String describe(Matrix m) {
        return VECTOR_ARRAY_PARSER.describe(m.getRows());
    }
}
