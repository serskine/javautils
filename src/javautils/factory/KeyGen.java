package javautils.factory;

import java.util.function.Supplier;

public class KeyGen implements Supplier<Long> {

    private long nextId;

    public KeyGen() {
        this(System.currentTimeMillis());
    }

    public KeyGen(final long seed) {
        this.nextId = seed;
    }

    @Override
    public Long get() {
        return nextId++;
    }
}
