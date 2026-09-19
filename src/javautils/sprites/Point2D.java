package javautils.sprites;

import javautils.math.VectorImpl;

public class Point2D extends VectorImpl {

    public static final int IDX_X = 0;
    public static final int IDX_Y = 1;

    public Point2D(double x, double y) {
        super(2);
        setLocation(x, y);
    }

    public double getX() {
        return get(IDX_X);
    }

    public double getY() {
        return get(IDX_Y);
    }

    public void setLocation(double x, double y) {
        set(IDX_X, x);
        set(IDX_Y, y);
    }
}
