package javautils.math;

import javautils.parser.Parsable;
import javautils.parser.Parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MatrixParser implements Parser<Matrix> {

    public static final String TOKEN_NUM_ELEMENTS = "numElements";
    public static final String TOKEN_ELEMENTS = "elements";
    public static final String FORMAT = String.format("(x{numElements}): {elements}");

    @Override
    public String getFormat() {
        return "{numRows} x {numCols}\n{cells}";
    }

    @Override
    public Map<String, String> getTokens(Matrix m) {
        final Map<String, String> map = new HashMap<>();
        map.put("numRows", String.valueOf(m.numRows()));
        map.put("numCols", String.valueOf(m.numCols()));

        final StringBuilder cells = new StringBuilder();
        for (int row = 0; row < m.numRows(); row++) {
            for (int col = 0; col < m.numCols(); col++) {
                cells.append(String.format("[%5.2f]", m.get(row, col)));
            }
            cells.append("\n");
        }
        cells.append("\n");
        map.put("cells", cells.toString());
        return map;
    }

    @Override
    public Matrix createNewItem() {
        return new MatrixImpl(1,1); // By default just one entry
    }

    @Override
    public void setTokens(final Matrix m, Map<String, String> tokens) {
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

        m.init(rowCount, colCount);

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
                m.set(row, col++, value);
            }
            if (col != colCount) {
                throw new RuntimeException("Failed to parse the expected " + colCount + " values from row " + row + ": " + rows.get(row));
            }
        }
    }


}
