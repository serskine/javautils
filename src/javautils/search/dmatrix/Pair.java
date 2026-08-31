package javautils.search.dmatrix;

import java.util.Map;
import java.util.Objects;

public class Pair<Key, Value> implements Map.Entry<Key, Value> {

    private Key key;
    private Value value;

    public Pair(final Map.Entry<Key, Value> entry) {
        this.key = entry.getKey();
        this.value = entry.getValue();
    }

    public Pair(Key key, Value value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public Key getKey() {
        return this.key;
    }

    @Override
    public Value getValue() {
        return this.value;
    }

    @Override
    public Value setValue(Value value) {
        final Value oldValue = this.value;
        this.value = value;
        return oldValue;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Pair) {
            final Pair<?, ?> other = (Pair<?, ?>) o;
            return Objects.equals(this.key, other.key) && Objects.equals(this.value, other.value);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.key, this.value);
    }
}
