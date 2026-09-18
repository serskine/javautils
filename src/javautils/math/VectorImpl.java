package javautils.math;

import javautils.parser.ArrayParser;

public class VectorImpl implements Vector {

    public static Vector createOfDimension(int numDimensions) {
        return new VectorImpl(new Double[numDimensions]);
    }

    private Double[] elements;

    public VectorImpl(int numDimensions) {
        this(new Double[numDimensions]);
    }

    public VectorImpl(double... values) {
        this(new Double[0]);
        init(values);   // Old array will be replaced.
    }

    public VectorImpl(Double[] values) {
        assert values != null;
        this.init(values);
    }

    public final void init(Double... elements) {
        this.elements = elements;
    }

    public final Double[] getElements() {
        return this.elements;
    }

    @Override
    public int numDimensions() {
        return getElements().length;
    }

    @Override
    public double get(int dimension) {
        return (Double)(getElements()[dimension]);
    }

    @Override
    public void set(int dimension, double value) {
        getElements()[dimension] = value;
    }

    @Override
    public boolean equals(final Object other) {
        if (other instanceof Vector) {
            return Vector.isSame(this, (Vector) other);
        } else {
            return false;
        }
    }

}
