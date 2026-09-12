package javautils.math;

import javautils.Logger;
import javautils.Text;
import javautils.parser.Parsable;
import javautils.parser.ParsableArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MatrixImpl extends ParsableArray implements Matrix {

    private int numRows, numCols;

    public MatrixImpl() {
        init(0, 0);
    }

    public MatrixImpl(int numRows, int numCols) {
        init(numRows, numCols);
    }

    public void init(int numRows, int numCols) {
        this.numRows = numRows;
        this.numCols = numCols;
        final Vector[] rows = new Vector[numRows];
        for(int i = 0; i < numRows; i++) {
            rows[i] = new VectorImpl(numCols);
        }
        setElements(rows);
        Logger.info("MatrixImpl init -> " + rows.length + " x " + numCols);
    }

    @Override
    public double get(int row, int col) {
        return getRow(row).get(col);
    }

    @Override
    public void set(int row, int col, double value) {
        final Vector rowVector = (Vector) getRow(row);
        rowVector.set(col, value);
    }

    @Override
    public int numRows() {
        return numRows;
    }

    @Override
    public int numCols() {
        return numCols;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Matrix) {
            return Matrix.isSame(this, (Matrix) other);
        } else {
            return false;
        }
    }

    @Override
    public String getFormat() {
        return "{numRows} x {numCols}\n{cells}";
    }

    @Override
    public Map<String, String> getTokens() {
        final Map<String, String> map = new HashMap<>();
        map.put("numRows", String.valueOf(numRows()));
        map.put("numCols", String.valueOf(numCols()));

        final StringBuilder cells = new StringBuilder();
        for (int row = 0; row < numRows(); row++) {
            for (int col = 0; col < numCols(); col++) {
                cells.append(String.format("[%5.2f]", get(row, col)));
            }
            cells.append("\n");
        }
        cells.append("\n");
        map.put("cells", cells.toString());
        return map;
    }

    @Override
    public void setTokens(Map<String, String> tokens) {
        final int rowCount;
        final int colCount;

        try {
            rowCount = Integer.parseInt(tokens.get("numRows"));
        } catch (NumberFormatException e) {
            throw new RuntimeException("Failed to parse numRows", e);
        }

        try {
            colCount = Integer.parseInt(tokens.get("numCols"));
        } catch (NumberFormatException e) {
            throw new RuntimeException("Failed to parse numCols", e);
        }

        init(rowCount, colCount);

        final String cellsValue = tokens.get("cells");
        if (cellsValue == null) {
            return;
        }

        final List<String> rows = new ArrayList<>();
        for (String line : cellsValue.split("\\R")) {
            if (!line.trim().isEmpty()) {
                rows.add(line);
            }
        }

        if (rows.size() != rowCount) {
            throw new RuntimeException("Expected " + rowCount + " rows but found " + rows.size() + " in cells token");
        }

        final Pattern numberPattern = Pattern.compile("[-+]?\\d*\\.?\\d+(?:[eE][-+]?\\d+)?");
        for (int row = 0; row < rowCount; row++) {
            final Matcher matcher = numberPattern.matcher(rows.get(row));
            int col = 0;
            while (matcher.find() && col < colCount) {
                final double value = Double.parseDouble(matcher.group());
                set(row, col++, value);
            }
            if (col != colCount) {
                throw new RuntimeException("Failed to parse the expected " + colCount + " values from row " + row + ": " + rows.get(row));
            }
        }
    }

    @Override
    public String getElementAsString(Object e) {
        final Vector v = (Vector) e;
        return v.describe();
    }

    @Override
    public Object parseElementFromString(String tokenValue) {
        final Vector v = new VectorImpl();
        v.parseFromText(tokenValue);
        return v;
    }

    @Override
    public Vector getRow(int row) {
        return (Vector) getElements()[row];
    }
}
