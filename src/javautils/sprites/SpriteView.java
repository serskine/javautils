package javautils.sprites;

import javautils.Logger;

import javax.swing.*;
import java.awt.*;
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

    public final void addSprite(final Sprite sprite) {
        synchronized (lock) {
            unrenderedSprites.add(sprite);
        }
    }

    private List<ISprite> fetchSpritesToRender(final int max) {
        final List<ISprite> list = new ArrayList<>(max);
        synchronized (lock) {
            for (int i = 0; i < max; i++) {
                if (unrenderedSprites.isEmpty()) {
                    // swap buffers
                    final PriorityQueue<ISprite> tmp = renderedSprites;
                    renderedSprites = unrenderedSprites;
                    unrenderedSprites = tmp;
                    if (unrenderedSprites.isEmpty()) {
                        break;
                    }
                }
                ISprite s = unrenderedSprites.remove();
                list.add(s);
            }
        }
        return list;
    }

    public void renderNextFrame(final SpriteGraphics sg) {
        final int numActors;
        synchronized (lock) {
            numActors = Math.min(actorsPerFrame, unrenderedSprites.size() + 1);
        }

        Logger.info("Rendering next frame: " + numActors + " actors to render");

        final List<ISprite> toRender = fetchSpritesToRender(numActors);
        if (toRender.isEmpty()) {
            return;
        }

        final long now = System.currentTimeMillis();

        for (final ISprite s : toRender) {
            sg.render(s);
            s.setLastRendered(now);
        }

        synchronized (lock) {
            for (final ISprite s : toRender) {
                renderedSprites.add(s);
            }
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
}
