package javautils.math;

import javautils.parser.Parsable;
import javautils.parser.ParsableArray;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class VectorImpl extends ParsableArray implements Vector {

    public VectorImpl(int numDimensions) {
        super(new Double[numDimensions]);
        setAll(0D);
    }

    public static Vector create(double... values) {
        final Vector v = new VectorImpl(values.length);
        for(int i = 0; i < values.length; i++) {
            v.set(i, values[i]);
        }
        return v;
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
