package javautils.common;

public class Histogram<K> extends DefaultMap<K, Double> {
    public Histogram() {
        super(0D);
    }

    public void increment(K key, double amount) {
        put(key, get(key) + amount);
    }

    public void decrement(K key, double amount) {
        put(key, get(key) - amount);
    }

    public void ensureAllPositive() {
        for(K key : keySet()) {
            put(key, Math.max(0D, get(key)));
        }
    }
}
