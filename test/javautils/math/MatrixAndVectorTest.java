package javautils.math;

import javautils.Logger;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MatrixAndVectorTest {

    @Test
    public void testCrossProduct() {
        Vector v1 = new VectorImpl(1, 1);
        Vector v2 = new VectorImpl(2, 2);
    }

    @Test
    public void describeVector() {
        final Vector expected = new VectorImpl(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);

        final String expectedDescription = expected.describe();

        Logger.info("expectedDescription = " + expectedDescription);

        final Vector observed = new VectorImpl();
        observed.parseFromText(expectedDescription);

        assertEquals(expected, observed);

        final String observedDescription = observed.describe();

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

        final String format = expected.getFormat();
        Logger.info("--- format ---\n" + format);

        final String expectedDescription = expected.describe();

        Logger.info("--- expectedDescription ---\n" + expectedDescription);

        final Matrix observed = new MatrixImpl(10, 10);

        observed.parseFromText(expectedDescription);

        assertEquals(expected, observed);

        final String observedDescription = observed.describe();

        Logger.info("--- observedDescription ---\n" + observedDescription);

        assertEquals(expectedDescription, observedDescription);



    }
}
