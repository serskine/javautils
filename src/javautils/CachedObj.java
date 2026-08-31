package javautils;

import java.util.function.Supplier;

public class CachedObj<T> implements Supplier<T> {

    private final Supplier<T> supplier;
    private T cachedValue;
    private boolean invalid;

    public CachedObj(final Supplier<T> supplier) {
        this.supplier = supplier;
        invalid = true;
    }

    @Override
    public T get() {
        if (invalid) {
            this.cachedValue = supplier.get();
            this.invalid = false;
        }
        return this.cachedValue;
    }

    public final boolean isValid() {
        return !invalid;
    }

    public final void invalidate() {
        this.invalid = true;
    }
}
