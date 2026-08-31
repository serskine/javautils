package javautils.sprites;

import java.awt.*;
import java.awt.image.BufferedImage;

public interface ISprite {
    Shape getShape();
    Color getFillColor();
    Color getLineColor();
    BufferedImage getTexture();

    double getZ();

    void setLastRendered(Long now);
    boolean isVisible();
    void invalidate();

    long getCreated();

}
