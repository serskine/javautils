package javautils.sprites;

import javautils.Logger;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Objects;

public class SpriteGraphics {

    private final Graphics2D g;

    public SpriteGraphics(final Graphics2D g) {
          this.g = Objects.requireNonNull(g);
    }

    /**
     * This method assumes the sprite is not null and is visible.
     * We do check the line colors, but do not check the visibility flags
     * @param sprite is what we expect to render.
     */
    public final void render(final ISprite sprite) {
        final Color oldColor = g.getColor();
        if (sprite.getShape() != null) {
            if (sprite.getFillColor() != null) {
                g.setColor(sprite.getFillColor());
                g.fill(sprite.getShape());
            }
            if (sprite.getLineColor() != null) {
                g.setColor(sprite.getLineColor());
                g.draw(sprite.getShape());
            }
            if (sprite.getTexture() != null) {
                final BufferedImage texture = sprite.getTexture();
                final TexturePaint tp = new TexturePaint(texture, sprite.getShape().getBounds());
                g.setPaint(tp);
                g.fill(sprite.getShape());
            }
        }
        g.setColor(oldColor);
    }
}
