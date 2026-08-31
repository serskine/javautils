package javautils.search;

import java.util.*;

public class Histogram<T>  implements Map<T, Double> {

    private Map<T, Double> map = new HashMap<>();
    private double sumWeight = 0D;

    public double getSumWeight() {
        return sumWeight;
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean containsKey(Object o) {
        return map.containsKey(o);
    }

    @Override
    public boolean containsValue(Object o) {
        return map.containsValue(o);
    }

    @Override
    public Double get(Object o) {
        return map.getOrDefault(o, 0d);
    }

    @Override
    public Double put(T t, Double aDouble) {
        Double old = get(t);
        if (aDouble <= 0D) {
            map.remove(t);
        } else {
            map.put(t, aDouble);
        }
        sumWeight += aDouble;
        sumWeight -= old;
        return old;
    }

    @Override
    public Double remove(Object o) {
        Double old = get(o);
        map.remove(o);
        sumWeight -= old;
        return old;
    }

    @Override
    public void putAll(Map<? extends T, ? extends Double> map) {
        for (Map.Entry<? extends T, ? extends Double> entry : map.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void clear() {
        map.clear();
        sumWeight = 0D;
    }

    @Override
    public Set<T> keySet() {
        return map.keySet();
    }

    @Override
    public Collection<Double> values() {
        return map.values();
    }

    @Override
    public Set<Entry<T, Double>> entrySet() {
        return map.entrySet();
    }
}
