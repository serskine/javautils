package javautils.time;

import javautils.Announcer;

import java.util.EventListener;

public class Ticker extends Timer {

    public interface Listener extends EventListener {
        void onTick(long tick);
        void onStop(long tick);
        void onStart(long tick);
    }

    protected final Announcer<Listener> announcer = new Announcer(Listener.class);

    protected final Thread thread;
    protected long lastChecked;

    public Ticker(final TimeSource source) {
        this(source, Thread.MIN_PRIORITY);
    }

    public Ticker(final TimeSource source, final int threadPriority) {
        super(source);
        this.lastChecked = getTime();
        this.thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    checkForTicks();
                }
            }
        });
        thread.setPriority(threadPriority);
        thread.start();
    }

    protected final void checkForTicks() {
        if (thread.getPriority() >= Thread.MIN_PRIORITY) {
            final long now = getTime();
            if (now != lastChecked) {
                for (long i = lastChecked; i < now; i++) {
                    announcer.announce().onTick(now);
                }
                lastChecked = now;
            }
        }
    }

    public final void setThreadPriority(int threadPriority) {
        thread.setPriority(threadPriority);
    }

    @Override
    public void stop() {
        super.stop();
        thread.interrupt();
        final long time = getTime();
        announcer.announce().onStop(time);
    }

    @Override
    public void start() {
        try {
            super.start();
            final long time = getTime();
            announcer.announce().onStart(time);
            thread.start();
        } catch (IllegalThreadStateException e) {
            // Thread is already started, so we just ignore the exception and start ticking again.
        }
    }

    public final void addListener(final Listener listener) {
        announcer.addListener(listener);
    }

}
