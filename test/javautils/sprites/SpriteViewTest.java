package javautils.sprites;

import javautils.Logger;
import javautils.actors.ActorUpdatesManager;
import javautils.time.SystemTimeSource;
import javautils.time.Ticker;
import javautils.time.TimeSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class SpriteViewTest {

    Graphics2D g;
    SpriteView view;

    public static final int WIDTH = 1024;
    public static final int HEIGHT = 720;
    public static final int NUM_SPRITES = 5000;

    private JFrame frame;

    private Ticker ticker;
    private ActorUpdatesManager actorUpdatesManager;


    @Before
    public void onSetup() {
        ticker = new Ticker(new SystemTimeSource(), Thread.MIN_PRIORITY);
        ticker.addListener(new Ticker.Listener() {
            @Override
            public void onTick(long tick) {
                actorUpdatesManager.update();
            }

            @Override
            public void onStop(long tick) {
                actorUpdatesManager.update();
            }

            @Override
            public void onStart(long tick) {
                actorUpdatesManager.update();
            }
        });
        actorUpdatesManager = new ActorUpdatesManager(ticker);

        frame = new JFrame();

        view = new SpriteView();

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(WIDTH, HEIGHT);
        frame.setResizable(true);
        frame.setLocationRelativeTo(null);
        frame.add(view);
        frame.setBackground(Color.YELLOW);
        frame.setVisible(true);

        view.addSprite(createOriginSprite());
        for(int i=0; i<NUM_SPRITES; i++) {
            final String name = "RandomSprite[" + i + "]";
            final double mass = Math.random() * 100;
            final Sprite sprite = createRandomSprite(name);
            final GravityActor actor = new GravityActor(sprite, mass);
            view.addSprite(actor.sprite);
        }

    }

    @After
    public void onTearDown() {
        while(frame.isVisible()) {

        }
    }

    @Test
    public void testRendering() {

    }

    private Sprite createOriginSprite() {
        final Rectangle rectangle = new Rectangle(-WIDTH/2, WIDTH/2, WIDTH, HEIGHT);
        final Color lineColor = Color.YELLOW;
        final Color fillColor = Color.RED;
        final long time = 0;

        final Sprite sprite = new Sprite("Origin Sprite", time, rectangle, lineColor, fillColor);
        sprite.setZ(1);

        return sprite;
    }

    private Sprite createRandomSprite(final String name) {
        final Random r = new Random();
        final long time = System.currentTimeMillis();
        final int x = r.nextInt(WIDTH);
        final int y = r.nextInt(HEIGHT);
        final int w = r.nextInt(50);
        final int h = r.nextInt(50);

        final Rectangle rect = new Rectangle(x, y, w, h);
        final Color lineColor = Color.BLACK;
        final Color fillColor = (r.nextBoolean()) ? null : new Color(r.nextInt(256), r.nextInt(256), r.nextInt(256));

        final Sprite sprite = new Sprite(name, time, rect, lineColor, fillColor);
        sprite.setZ(1D);

        Logger.info("Created sprite: " + sprite);
        return sprite;

    }
}
