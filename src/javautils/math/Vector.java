package javautils.math;

import javautils.parser.Parsable;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public interface Vector extends Parsable {
    int numDimensions();
    double get(int dimension);
    void set(int dimension, double value);

    static void verifySameDimensions(Vector v1, Vector v2) {
        assert (v1.numDimensions() == v2.numDimensions());
    }

    default void forEachDimensionDo(BiConsumer<Vector, Integer> func) {
        for(int i=0; i<numDimensions(); i++) {
            func.accept(this, i);
        }
    }

    default void setAll(double value) {
        forEachDimensionDo((v,i) -> v.set(i, value));
    }

    default void increment(final Vector other) {
        verifySameDimensions(this, other);
        forEachDimensionDo( (v,i) -> v.set(i, v.get(i) + other.get(i)));
    }

    default void decrement(final Vector other) {
        verifySameDimensions(this, other);
        forEachDimensionDo( (v,i) -> v.set(i, v.get(i) - other.get(i)));
    }

    default void scale(final double scalar) {
        forEachDimensionDo( (v, i) -> v.set(i, v.get(i) * scalar));
    }

    default void negate() {
        scale(-1D);
    }

    default double dotProduct(Vector a) {
        double sum = 0.0;
        for(int i=0; i<numDimensions(); i++) {
            sum += a.get(i) * this.get(i);
        }
        return sum;
    }

    default double lengthSquared() {
        return dotProduct(this);
    }

    default double length() {
        return Math.sqrt(lengthSquared());
    }

    default Vector copy() {
        final Vector v = VectorImpl.createOfDimension(numDimensions());
        v.forEachDimensionDo((v2,i) -> v2.set(i, get(i)));
        return v;
    }

    default Matrix cartesianProduct(Vector v, BiFunction<Double, Double, Double> func) {
        final Matrix m = new MatrixImpl(this.numDimensions(), v.numDimensions());
        for(int row=0; row<this.numDimensions(); row++) {
            for(int col=0; col<v.numDimensions(); col++) {
                m.set(row, col, func.apply(get(row), v.get(col)));
            }
        }
        return m;
    }

    static boolean isSame(Vector v1, Vector v2) {
        if (v1==v2) {
            return true;
        } else if (v1==null || v2==null) {
            return false;
        } else {
            if (v1.numDimensions() != v2.numDimensions()) {
                return false;
            } else {
                for(int i=0; i<v1.numDimensions(); i++) {
                    if (v1.get(i) != v2.get(i)) {
                        return false;
                    }
                }
                return true;
            }
        }
    }

    static Matrix skewMatrix(Vector a, Vector b) {
        int n = a.numDimensions();
        verifySameDimensions(a, b);

        Matrix m = new MatrixImpl(n, n);
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                m.set(i, j, a.get(i) * b.get(j) - b.get(i) * a.get(j));
            }
        }
        return m;
    }

    static Vector crossProduct(Vector a, Vector b) {
        int n = a.numDimensions();
        if (n != b.numDimensions()) {
            throw new IllegalArgumentException("Dimensions must match");
        }

        Matrix m = skewMatrix(a, b); // n x n skew matrix

        int count = n * (n - 1) / 2; // number of upper-triangular entries
        Vector result = VectorImpl.createOfDimension(count);

        int k = 0;
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                result.set(k++, m.get(i, j));
            }
        }

        return result;
    }

    static double crossProduct2D(Vector a, Vector b) {
        if (a.numDimensions() != 2 || b.numDimensions() != 2) {
            throw new IllegalArgumentException("2D cross product requires 2D vectors");
        }

        return crossProduct(a,b).get(0);
    }

    static Vector crossProduct3D(Vector a, Vector b) {
        if (a.numDimensions() != 3 || b.numDimensions() != 3) {
            throw new IllegalArgumentException("3D cross product requires 3D vectors");
        }

        return crossProduct(a, b);
    }

    default Stream<Double> stream() {
        return IntStream.range(0, numDimensions()).mapToObj(this::get);
    }


}
