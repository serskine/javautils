package javautils.sprites;

import java.awt.geom.Point2D;

public interface Camera2DController {
    double getX();
    double getY();

    void setX(double x);
    void setY(double y);

    default Point2D getLocation() {
        return new Point2D.Double(getX(), getY());
    }

    default void setLocation(Point2D location) {
        setX(location.getX());
        setY(location.getY());
    }

    double getRotationRadians();
    default double getRotationDegrees() {
        return Math.toDegrees(getRotationRadians());
    }

    void setRotationRadians(double rotationRadians);
    default void setRotationDegrees(double rotationDegrees) {
        setRotationRadians(Math.toRadians(rotationDegrees));
    }

    double getScale();
    void setScale(double scale);

    default double getZoom() {
        return getScale();
    }

    default void setZoom(double scale) {
        setScale(scale);
    }

    default void init(final double x, final double y, double rotationRadians, double scale) {
        setX(x);
        setY(y);
        setRotationRadians(rotationRadians);
        setScale(scale);
    }
}
