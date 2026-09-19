package javautils.sprites;

import javautils.math.VectorImpl;

public class Point3D extends VectorImpl {
    public static final int IDX_X = 0;
    public static final int IDX_Y = 1;
    public static final int IDX_Z = 2;

    public Point3D(double x, double y, double z) {
        super(3);
        setLocation(x, y, z);
    }

    public double getX() {
        return get(IDX_X);
    }

    public double getY() {
        return get(IDX_Y);
    }

    public double getZ() {
        return get(IDX_Z);
    }

    public void setLocation(double x, double y, double z) {
        set(IDX_X, x);
        set(IDX_Y, y);
        set(IDX_Z, z);
    }
}
