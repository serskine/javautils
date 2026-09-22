package javautils.common;

import java.util.Arrays;

public class Histogram<K> extends DefaultMap<K, Double> {

    private double sum = 0D;


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

    public static <T> Histogram<T> build(T... items) {
        return build(Arrays.asList(items));
    }

    public static <T> Histogram<T> build(Iterable<T> items) {
        final Histogram<T> histogram = new Histogram<>();
        for(T item : items) {
            histogram.increment(item, 1);
        }
        return histogram;
    }

    @Override
    public final Double put(K key, Double value) {
        Double prev = super.put(key, value);
        double diff = value - prev;
        sum += diff;
        return prev;
    }

    public final double getTotalSum() {
        return sum;
    }
}
