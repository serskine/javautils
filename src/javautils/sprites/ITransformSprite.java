package javautils.sprites;

import java.awt.*;
import java.awt.geom.AffineTransform;

public interface ITransformSprite extends ISprite {
    default void translate(double x, double y, double z) {
        final AffineTransform at = new AffineTransform();
        at.translate(x, y);
        applyTransform(at);
    }

    default void rotate(double radians) {
        final AffineTransform at = new AffineTransform();
        at.rotate(radians);
        applyTransform(at);
    }

    default void scale(double scalar) {
        final AffineTransform at = new AffineTransform();
        at.scale(scalar, scalar);
        applyTransform(at);
    }

    default void applyTransform(final AffineTransform transform) {
        final Shape shape = getShape();
        if (shape != null) {
            setShape(transform.createTransformedShape(shape));
        }
        invalidate();
    }

    void setShape(Shape shape);
}
