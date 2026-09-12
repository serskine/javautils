package javautils.math;

import javautils.parser.Parsable;
import javautils.parser.ParsableArray;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public interface Matrix extends Parsable {
    double get(int row, int col);
    void set(int row, int col, double value);

    default double get(Cell cell) {
        return get(cell.row, cell.col);
    }

    default void set(Cell cell, double value) {
        set(cell.row, cell.col, value);
    }

    int numRows();
    int numCols();

    class Cell {
        public final int row, col;

        public Cell(int row, int col) {
            this.row = row;
            this.col = col;
        }
    }

    class InternalVector extends ParsableArray implements Vector {
        private final Matrix m;
        private final int dimension;
        private final boolean isRow;

        public InternalVector(Matrix m, int dimension, boolean isRow) {
            this.m = m;
            this.dimension = dimension;
            this.isRow = isRow;
        }

        @Override
        public int numDimensions() {
            return (isRow) ? m.numCols() : m.numRows();
        }

        @Override
        public double get(int i) {
            return (isRow) ? m.get(dimension, i) : m.get(i, dimension);
        }

        @Override
        public void set(int i, double value) {
            if (isRow) {
                m.set(dimension, i, value);
            } else {
                m.set(i, dimension, value);
            }
        }

        @Override
        public String getElementAsString(Object e) {
            final VectorImpl v = (VectorImpl) m;
            return v.getElementAsString(e);
        }

        @Override
        public Object parseElementFromString(String tokenValue) {
            final VectorImpl v = (VectorImpl) m;
            return v.parseElementFromString(tokenValue);
        }
    }

    static void verifySameDimensions(Matrix m1, Matrix m2) {
        assert (m1.numRows() == m2.numRows());
        assert (m1.numCols() == m2.numCols());
    }

    default Vector getRow(int row) {
        return new InternalVector(this, row, true);
    }

    default Vector getColumn(int col) {
        return new InternalVector(this, col, false);
    }

    default void forEachCellDo(final BiConsumer<Matrix, Cell> consumer) {
        for (int row=0; row<numRows(); row++) {
            for(int col=0; col<numCols(); col++) {
                consumer.accept(this, new Cell(row, col));
            }
        }
    }

    default void forEachRowDo(final BiConsumer<Matrix, Integer> consumer) {
        for(int row=0; row<numRows(); row++) {
            consumer.accept(this, row);
        }
    }

    default void forEachColDo(final BiConsumer<Matrix, Integer> consumer) {
        for(int col=0; col<numCols(); col++) {
            consumer.accept(this, col);
        }
    }

    default Matrix copy() {
        final Matrix m = new MatrixImpl(numRows(), numCols());
        m.forEachCellDo((m2, cell) -> m2.set(cell, get(cell)));
        return m;
    }

    default void increment(final Matrix m) {
        verifySameDimensions(this, m);
        forEachCellDo((m2, cell) -> m2.set(cell, get(cell) + m.get(cell)));
    }

    default void decrement(final Matrix m) {
        verifySameDimensions(this, m);
        forEachCellDo((m2, cell) -> m2.set(cell, get(cell) - m.get(cell)));
    }

    default void scale(final double scalar) {
        forEachCellDo((m, cell) -> m.set(cell, m.get(cell) * scalar));
    }

    default Matrix multiply(final Matrix m) {
        assert (numCols() == m.numRows());
        final Matrix r = new MatrixImpl(numRows(), m.numCols());
        for (int row = 0; row < numRows(); row++) {
            for (int col = 0; col < m.numCols(); col++) {
                double sum = 0.0;
                for (int k = 0; k < numCols(); k++) {
                    sum += get(row, k) * m.get(k, col);
                }
                r.set(row, col, sum);
            }
        }
        return r;
    }

    default void makeIdentity() {
        forEachCellDo((m, c) -> m.set(c, (c.row==c.col) ? 1 : 0));
    }

    default void makeUpperTriangular() {
        final int n = Math.min(numRows(), numCols());

        for (int pivot = 0; pivot < n; pivot++) {
            int pivotRow = pivot;
            double maxAbs = Math.abs(get(pivotRow, pivot));
            for (int r = pivot + 1; r < numRows(); r++) {
                double abs = Math.abs(get(r, pivot));
                if (abs > maxAbs) {
                    maxAbs = abs;
                    pivotRow = r;
                }
            }

            if (Math.abs(get(pivotRow, pivot)) < 1e-12) {
                continue;
            }

            if (pivotRow != pivot) {
                for (int c = 0; c < numCols(); c++) {
                    double temp = get(pivot, c);
                    set(pivot, c, get(pivotRow, c));
                    set(pivotRow, c, temp);
                }
            }

            for (int r = pivot + 1; r < numRows(); r++) {
                double factor = get(r, pivot) / get(pivot, pivot);
                for (int c = pivot; c < numCols(); c++) {
                    set(r, c, get(r, c) - factor * get(pivot, c));
                }
            }
        }
    }

    default double determinant() {
        if (numRows() != numCols()) {
            throw new IllegalArgumentException("Determinant only for square matrices");
        }

        final Matrix m = copy();   // <- copy, so original stays unchanged
        final int n = numRows();

        double det = 1.0;
        int swaps = 0;

        for (int pivot = 0; pivot < n; pivot++) {
            int pivotRow = pivot;
            double maxAbs = Math.abs(m.get(pivot, pivot));

            for (int row = pivot + 1; row < n; row++) {
                double abs = Math.abs(m.get(row, pivot));
                if (abs > maxAbs) {
                    maxAbs = abs;
                    pivotRow = row;
                }
            }

            if (maxAbs < 1e-12) return 0.0;

            if (pivotRow != pivot) {
                for (int col = 0; col < n; col++) {
                    double temp = m.get(pivot, col);
                    m.set(pivot, col, m.get(pivotRow, col));
                    m.set(pivotRow, col, temp);
                }
                swaps++;
            }

            double pivotValue = m.get(pivot, pivot);
            det *= pivotValue;

            for (int row = pivot + 1; row < n; row++) {
                double factor = m.get(row, pivot) / pivotValue;
                for (int col = pivot; col < n; col++) {
                    m.set(row, col, m.get(row, col) - factor * m.get(pivot, col));
                }
            }
        }

        if (swaps % 2 != 0) det = -det;
        return det;
    }

    default Stream<Vector> rowStream() {
        return IntStream.range(0, numRows()).mapToObj(this::getRow);
    }

    default Stream<Vector> colStream() {
        return IntStream.range(0, numCols()).mapToObj(this::getColumn);
    }

    static boolean isSame(Matrix m1, Matrix m2) {
        if (m1==m2) {
            return true;
        } else if (m1.numRows() != m2.numRows() || m1.numCols() != m2.numCols()) {
            return false;
        } else {
            for(int row=0; row<m1.numRows(); row++) {
                for(int col=0; col<m1.numCols(); col++) {
                    if (m1.get(row, col) != m2.get(row, col)) {
                        return false;
                    }
                }
            }
            return true;
        }
    }

    default String describe() {
        final StringBuilder sb = new StringBuilder();
        sb.append(String.format("%d x %d\n", numRows(), numCols()));
        for(int row=0; row<numRows(); row++) {
            for(int col=0; col<numCols(); col++) {
                sb.append(String.format("[%5.2f]", get(row, col)));
            }
            sb.append("\n");
        }
        sb.append("\n");
        return sb.toString();
    }

}
