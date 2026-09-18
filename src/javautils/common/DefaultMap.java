package javautils.common;

import java.util.*;

public class DefaultMap<K, V> implements Map<K, V> {
    private V dValue;
    private final Map<K, V> internalMap;

    public DefaultMap(final V dValue) {
        this(dValue, new HashMap<>());
    }

    public DefaultMap(final V dValue, final Map<K, V> internalMap) {
        this.dValue = dValue;
        this.internalMap = internalMap;
    }

    public V getDefaultValue() {
        return this.dValue;
    }

    @Override
    public int size() {
        return internalMap.size();
    }

    @Override
    public boolean isEmpty() {
        return internalMap.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return internalMap.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return internalMap.containsValue(value);
    }

    @Override
    public V get(Object k) {
        return internalMap.getOrDefault(k, getDefaultValue());
    }

    @Override
    public V put(K key, V value) {
        V prev;
        if (Objects.equals(value, getDefaultValue())) {
            prev = internalMap.remove(key);
        } else {
            prev = internalMap.put(key, value);
        }
        return (prev==null) ? getDefaultValue() : prev;
    }

    @Override
    public V remove(Object key) {
        V prev = internalMap.remove(key);
        return (prev==null) ? getDefaultValue() : prev;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        m.entrySet().forEach(e -> put(e.getKey(), e.getValue()));
    }

    @Override
    public void clear() {
        internalMap.clear();
    }

    @Override
    public Set<K> keySet() {
        return internalMap.keySet();
    }

    @Override
    public Collection<V> values() {
        return internalMap.values();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return internalMap.entrySet();
    }

}
