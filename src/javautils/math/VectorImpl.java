package javautils.math;

import java.util.Collection;

public class VectorImpl implements Vector {


    private Double[] elements;

    public VectorImpl() {
        init(new Double[0]);
    }

    public VectorImpl(int numDimensions) {
        init(new Double[numDimensions]);
    }


    public final Vector init(Double... elements) {
        assert elements != null;
        this.elements = elements;
        return this;
    }

    public final Vector init(Collection<Double> elements) {
        assert (elements != null);
        this.elements = new Double[elements.size()];
        int i=0;
        for(Double d : elements) {
            this.elements[i] = d;
            i++;
        }
        return this;
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
