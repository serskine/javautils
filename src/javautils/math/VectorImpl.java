package javautils.math;

import javautils.parser.Parsable;
import javautils.parser.ParsableArray;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class VectorImpl extends ParsableArray implements Vector {

    public static Vector createOfDimension(int numDimensions) {
        return new VectorImpl(new Double[numDimensions]);
    }

    public VectorImpl(int numDimensions) {
        this(new Double[numDimensions]);
    }

    public VectorImpl(double... values) {
        Double[] newValues = new Double[values.length];
        for(int i=0; i<values.length; i++) {
            newValues[i] = values[i];
        }
        this(newValues);
    }

    public VectorImpl(Double[] values) {
        assert values != null;
        this.setElements(values);
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

    @Override
    public String getElementAsString(Object e) {
        return Double.toString((Double) e);
    }

    @Override
    public Double parseElementFromString(String tokenValue) {
        return Double.parseDouble(tokenValue);
    }
}
