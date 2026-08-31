package javautils.time;

public class Timer implements TimeSource {
    protected TimeSource source;
    protected long sourceTimePerTick;
    protected long sourceTimeCreated;
    protected Long sourceTimeLastStopped;
    protected long sourceTimePaused;

    public Timer(final TimeSource source) {
        this(source, 1);
    }

    public Timer(final long sourceTimePerTick) {
        this(new SystemTimeSource(), sourceTimePerTick);
    }

    public Timer(final TimeSource source, final long sourceTimePerTick) {
        this.source = source;
        this.sourceTimePerTick = sourceTimePerTick;
        this.sourceTimeCreated = source.getTime();
        this.sourceTimePaused = 0;
    }

    @Override
    public final long getTime() {
        return (source.getTime() - sourceTimeCreated - sourceTimePaused) / sourceTimePerTick;
    }

    public void start() {
        if (sourceTimeLastStopped != null) {
            final long sourceTime = source.getTime();
            sourceTimePaused += (sourceTime - sourceTimeLastStopped);
            sourceTimeLastStopped = null;
        }
    }

    public void stop() {
        if (sourceTimeLastStopped == null) {
            sourceTimeLastStopped = source.getTime();
        }
    }
}
