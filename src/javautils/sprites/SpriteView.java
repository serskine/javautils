package javautils.sprites;

import javautils.math.Range;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.*;
import java.util.List;

public class SpriteView extends JPanel implements Comparator<ISprite> {

    public static final int DEFAULT_FPS = 30;
    public static final int DEFAULT_APF = 10000;

    private PriorityQueue<ISprite> renderedSprites = new PriorityQueue<ISprite>(this);
    private PriorityQueue<ISprite> unrenderedSprites = new PriorityQueue<ISprite>(this);
    private final Object lock = new Object();
    private int actorsPerFrame;
    private ViewController viewController;
    private Range solidRenderRange = new Range(10D, null);

    public SpriteView() {
        this(DEFAULT_FPS, DEFAULT_APF );
    }

    public SpriteView(final int framesPerSecond, final int actorsPerFrame) {
        this.actorsPerFrame = Math.max(1, actorsPerFrame);
        this.viewController = new ViewController();
        viewController.attachTo(this);
    }

    @Override
    protected void paintComponent(final Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        if (viewController != null) {
            viewController.applyTransform(g2d);
        }
        final SpriteGraphics sg = new SpriteGraphics(g2d);
        renderNextFrame(sg);
    }

    protected final Graphics2D getGraphics2D() {
        return (Graphics2D) super.getGraphics();
    }

    public final void addSprite(final Sprite sprite) {
        synchronized (lock) {
            unrenderedSprites.add(sprite);
        }
    }

    private boolean isSpriteRenderable(ISprite sprite) {
        return sprite != null && sprite.isVisible() && meetsMinimumDimension(sprite);
    }

    private boolean meetsMinimumDimension(ISprite sprite) {
        if (sprite == null || sprite.getShape() == null) {
            return false;
        }
        if (solidRenderRange == null) {
            return true;
        }

        final Rectangle2D bounds = sprite.getShape().getBounds2D();
        final double zoom = viewController == null
            ? 1.0
            : Math.abs(viewController.getScale());
        return solidRenderRange.contains(Math.min(bounds.getWidth(), bounds.getHeight()) * zoom);
    }

    private List<ISprite> fetchSpritesToRender(final int max) {
        final List<ISprite> list = new ArrayList<>(max);
        synchronized (lock) {
            int i=0;
            while (i<max) {
                if (unrenderedSprites.isEmpty()) {
                    // swap buffers
                    final PriorityQueue<ISprite> tmp = renderedSprites;
                    renderedSprites = unrenderedSprites;
                    unrenderedSprites = tmp;
                    if (unrenderedSprites.isEmpty()) {
                        break;  // no more sprites to render
                    }
                }
                ISprite s = unrenderedSprites.remove();
                list.add(s);
                i++;
            }
        }
        return list;
    }

    public void renderNextFrame(final SpriteGraphics sg) {
        final List<ISprite> toRender;
        synchronized (lock) {
            final int numNewSprites = Math.min(actorsPerFrame, unrenderedSprites.size());
            renderedSprites.addAll(fetchSpritesToRender(numNewSprites));
            toRender = new ArrayList<>(renderedSprites);
        }
        Collections.sort(toRender, this);

        final long now = System.currentTimeMillis();

        for (final ISprite s : toRender) {
            if (!isSpriteRenderable(s)) {
                continue;
            }
            sg.render(s);
            s.setLastRendered(now);
        }
    }

    @Override
    public int compare(ISprite s1, ISprite s2) {
        if (s1 == s2) return 0;
        if (s1 == null) return 1;
        if (s2 == null) return -1;

        int zDiff = Double.compare(s2.getZ(), s1.getZ());
        if (zDiff != 0) return zDiff;

        int tDiff = Long.compare(s1.getCreated(), s2.getCreated());
        if (tDiff != 0) return tDiff;

        if (!s1.isVisible()) return 1;
        if (s2.isVisible()) return -1;
        return 0;
    }

    public final void setSolidRenderRange(final Range solidRenderRange) {
        this.solidRenderRange = solidRenderRange;
    }
}
