package javautils.parser;

import javautils.math.Matrix;
import javautils.math.Vector;

import java.util.HashMap;
import java.util.Map;

public class MatrixParser implements Parser<Matrix> {

    public static final String TOKEN_NUM_ROWS = "numRows";
    public static final String TOKEN_NUM_COLS = "numCols";
    public static final String TOKEN_CELLS = "cells";
    public static final String ROW_DELIM = "\n";

    private static final VectorParser VECTOR_PARSER = new VectorParser();

    @Override
    public String getFormat() {
        return String.format("%s x %s\n%s",
            paramToken(TOKEN_NUM_ROWS),
            paramToken(TOKEN_NUM_COLS),
            paramToken(TOKEN_CELLS));
    }

    @Override
    public Map<String, String> getTokens(Matrix m) {

        final Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put(TOKEN_NUM_ROWS, "" + m.numRows());
        tokenMap.put(TOKEN_NUM_COLS, "" + m.numCols());

        final StringBuilder sb = new StringBuilder();

        for(int r=0; r<m.numRows(); r++) {
            if (r>0) {
                sb.append(ROW_DELIM);
            }
            final Vector row = m.getRow(r);
            sb.append(VECTOR_PARSER.describe(row));
        }
        tokenMap.put(TOKEN_CELLS, sb.toString());
        return tokenMap;
    }

    @Override
    public Matrix createNewItem() {
        return null;
    }

    @Override
    public void setTokens(Matrix m, Map<String, String> tokens) {
        Integer numRows = parseIntegerOrDefault(tokens.get(TOKEN_NUM_ROWS), 0);
        Integer numCols = parseIntegerOrDefault(tokens.get(TOKEN_NUM_ROWS), 0);
        String cells = tokens.get(TOKEN_CELLS);
        final String[] rowTokens = cells.split(ROW_DELIM);

        m.init(numRows, numCols);

        if (cells.length() != numRows) {
            throw new RuntimeException(String.format("Expected numRows = %d but observed %d", numRows, cells.length()));
        }
        for(int r=0; r<numRows; r++) {
            try {
                final Vector vector = VECTOR_PARSER.parseFromText(rowTokens[r]);
                if (vector.numDimensions() != numCols) {
                    throw new RuntimeException(String.format("Expected numCols = %d but observed %d", numCols, vector.numDimensions()));
                }

            } catch (Exception e) {
                throw new RuntimeException(String.format("For row %d: " + e.getMessage(), e));
            }
        }
    }
}
