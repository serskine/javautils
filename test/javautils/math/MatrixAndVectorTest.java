package javautils.math;

import javautils.Logger;
import javautils.parser.ArrayParser;
import javautils.parser.MatrixParser;
import javautils.parser.VectorParser;
import org.junit.Test;

import java.util.Arrays;
import java.util.Random;

import static javautils.TestUtils.time;
import static org.junit.Assert.assertEquals;

public class MatrixAndVectorTest {

    static final Random r = new Random();
    static final VectorParser PARSER_VECTOR = new VectorParser();
    static final MatrixParser PARSER_MATRIX = new MatrixParser();

    @Test
    public void testCrossProduct() {
        Vector v1 = new VectorImpl().init(1, 1);
        Vector v2 = new VectorImpl().init(2, 2);
    }

    @Test
    public void describeVector() {
        final Vector expected = new VectorImpl().init(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);

        final String expectedDescription = PARSER_VECTOR.describe(expected);

        Logger.info("expectedDescription = " + expectedDescription);

        final Vector observed = PARSER_VECTOR.parse(expectedDescription);

        assertEquals(expected, observed);

        final String observedDescription = PARSER_VECTOR.describe(observed);

        Logger.info("observedDescription = " + observedDescription);

        assertEquals(expectedDescription, observedDescription);

    }

    @Test
    public void describeMatrix() {
        final Matrix expected = new MatrixImpl(3, 4);
        for(int row=0; row<expected.numRows(); row++) {
            for(int col=0; col<expected.numCols(); col++) {
                expected.set(row, col, row*col);
            }
        }

        final String expectedDescription = expected.describe();

        Logger.info("--- expectedDescription ---\n" + expectedDescription);

        final Matrix observed = PARSER_MATRIX.parse(expectedDescription);

        assertEquals(expected, observed);

        final String observedDescription = observed.describe();

        Logger.info("--- observedDescription ---\n" + observedDescription);

        assertEquals(expectedDescription, observedDescription);
    }

    @Test
    public void performanceOps_Vector() {
        final int numDimensions = 100;
        final int numVectors = 500;

        final Vector[] vectors = new Vector[numVectors];
        time("creation", () -> {
            for (int i = 0; i < vectors.length; i++) {
                vectors[i] = randomVector(numDimensions);
            }
        });
        time("increment", () -> {
            final Vector result = new VectorImpl(numDimensions);
            for(Vector vector: vectors) {
                result.increment(vector);
            }
        });
        time("decrement", () -> {
            final Vector result = new VectorImpl(numDimensions);
            for(Vector vector: vectors) {
                result.decrement(vector);
            }
        });
        time("lengthSquared", () -> Arrays.stream(vectors).sequential().forEach(v -> v.lengthSquared()));
        time("length", () -> Arrays.stream(vectors).sequential().forEach(v -> v.length()));
        time("get", () -> Arrays.stream(vectors).sequential().forEach(v -> v.get(r.nextInt(v.numDimensions()))));
        time("set", () -> Arrays.stream(vectors).sequential().forEach(v -> v.set(r.nextInt(v.numDimensions()), r.nextDouble())));
    }

    @Test
    public void performanceOps_Matrix() {
        final int numRows = 30;
        final int numCols = numRows;
        final int numMatricies = 500;

        final Matrix[] matricies = new Matrix[numMatricies];
        time("creation", () -> {
            for (int i = 0; i < matricies.length; i++) {
                matricies[i] = randomMatrix(numRows, numCols);
            }
        });
        time("increment", () -> {
            final Matrix result = new MatrixImpl(numRows, numCols);
            for(Matrix m : matricies) {
                result.increment(m);
            }
        });
        time("decrement", () -> {
            final Matrix result = new MatrixImpl(numRows, numCols);
            for(Matrix m : matricies) {
                result.decrement(m);
            }
        });
        time("multiply", () -> {
            Matrix result = new MatrixImpl(numRows, numCols);
            for(Matrix m : matricies) {
                result = result.multiply(m);
            }
        });
        time("getRow", () -> {
            for(Matrix m : matricies) {
                m.getRow(r.nextInt(numRows));
            }
        });
        time("getCol", () -> {
            for(Matrix m : matricies) {
                m.getRow(r.nextInt(numRows));
            }
        });
        time("copy", () -> {
            Matrix result = new MatrixImpl(numRows, numCols);
            for(Matrix m : matricies) {
                result = m.copy();
            }
        });
        time("scale", () -> {
            for(Matrix m : matricies) {
                m.scale(r.nextDouble());
            }
        });
        time("makeIdentity", () -> {
            for(Matrix m : matricies) {
                m.makeIdentity();
            }
        });
        time("makeUpperTriangular", () -> {
            for(Matrix m : matricies) {
                try {
                    m.makeUpperTriangular();
                } catch (Exception e) {
                    Logger.err(e.getMessage(), e);
                }
            }
        });
        time("makeIdentity", () -> {
            for(Matrix m : matricies) {
                m.makeIdentity();
            }
        });
        time("determinant", () -> {
            for(Matrix m : matricies) {
                m.determinant();
            }
        });

    }

    static Matrix randomMatrix(int numRows, int numCols) {
        final Matrix m = new MatrixImpl(numRows, numCols);
        m.forEachCellDo((m2, cell) -> m2.set(cell, r.nextDouble() - 0.5D));
        return m;
    }

    static Vector randomVector(int numDimensions) {
        final Vector v = new VectorImpl(numDimensions);
        v.forEachDimensionDo((v2, i) -> v2.set(i, r.nextDouble() - 0.5D));
        return v;
    }
}
