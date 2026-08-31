package javautils.time;

public final class SystemTimeSource implements TimeSource {

    private final boolean isNanoTime;

    public SystemTimeSource() {
        this(false);
    }

    public SystemTimeSource(final boolean isNanoTime) {
        this.isNanoTime = isNanoTime;
    }

    @Override
    public long getTime() {
        return (isNanoTime) ? System.nanoTime() : System.currentTimeMillis();
    }
}
