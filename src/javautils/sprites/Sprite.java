package javautils.sprites;

import javautils.Announcer;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.Objects;

public class Sprite implements ITransformSprite {
    private Shape shape;
    private Color lineColor;
    private Color fillColor;
    private boolean visible;
    private double zIndex;
    private final String name;
    private boolean changed = true;
    private BufferedImage texture;
    private long created;

    private Announcer<ChangeListener> listeners = new Announcer<>(ChangeListener.class);

    public Sprite(final String name, final long time, final Shape shape) {
        this.name = name;
        this.shape = shape;
        this.created = time;
    }

    public Sprite(final String name, final long time, final Shape shape, final Color lineColor, final Color fillColor) {
        this.name = name;
        this.shape = Objects.requireNonNull(shape);
        this.lineColor = lineColor;
        this.fillColor = fillColor;
        this.visible = true;
        this.zIndex = 0D;
        this.created = time;
    }

    public void setColors(final Color lineColor, final Color fillColor) {
        this.lineColor = lineColor;
        this.fillColor = fillColor;
        this.visible = (lineColor != null || fillColor != null);
        invalidate();
    }

    public void setTextures(final BufferedImage texture) {
        this.texture = texture;
    }

    @Override
    public Shape getShape() {
        return this.shape;
    }

    @Override
    public Color getFillColor() {
        return this.fillColor;
    }

    @Override
    public Color getLineColor() {
        return this.lineColor;
    }

    @Override
    public BufferedImage getTexture() {
        return this.texture;
    }

    @Override
    public double getZ() {
        return this.zIndex;
    }

    public final void setZ(final double z) {
        this.zIndex = z;
    }

    @Override
    public void setLastRendered(Long now) {
        invalidate();
    }

    @Override
    public boolean isVisible() {
        return this.visible;
    }

    @Override
    public void invalidate() {
        if (this.changed != true) {
            this.changed = true;
            listeners.announce();
        }
    }

    @Override
    public String toString() {
        return this.name;
    }

    public final void addChangeListener(final ChangeListener listener) {
        this.listeners.addListener(listener);
    }

    @Override
    public long getCreated() {
        return created;
    }


    @Override
    public void setShape(Shape shape) {
        this.shape = shape;
        invalidate();
    }
}
