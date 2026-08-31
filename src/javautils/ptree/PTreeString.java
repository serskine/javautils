package javautils.ptree;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This represents a patricia tree. It can only use strings as it's keys.
 * The tree is actually a map of characters up to this point and is an
 * excellent way of determining suggestions for partial text
 * @param <T>
 */
public class PTreeString<T> implements Map<String, T> {

    public class Entry implements Map.Entry<String, T> {
        private String key;
        private T value;

        public Entry(final String key, final T value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() { return this.key;   }

        @Override
        public T getValue() {   return this.value;  }

        @Override
        public T setValue(T value) {
            final T old = value;
            this.value = value;
            return old;
        }
    }

    public final Map<Character, PTreeString<T>> children;
    private T info;
    private Integer size = null;

    public PTreeString() {
        this(null);
    }

    protected PTreeString(T info) {
        this.info = info;
        this.children = new HashMap<>();
    }

    private PTreeString<T> find(String key) {

        if (key==null) {
            return null;
        }
        if (key.length()==0) {
            return this;
        }
        PTreeString<T> root = this;
        for(int i=0; i<key.length(); i++) {
            PTreeString<T> child = root.children.get(key.charAt(i));
            if (child==null) {
                return null;
            } else {
                root = child;
            }
        }
        return root;
    }

    protected final T getInternal(String key) {
        final PTreeString<T> node = find(key);
        if (node != null) {
            return node.info;
        } else {
            return null;
        }
    }

    @Override
    public int size() {
        if (size==null) {
            size = (info==null) ? 0 : 1;
            children.values().forEach(t -> size += t.size());
        }
        return size;
    }

    @Override
    public boolean isEmpty() {
        return (size==0);
    }

    @Override
    public boolean containsKey(Object key) {
        if (key instanceof String) {
            final PTreeString<T> node = find((String) key);
            return (node != null) && (node.info != null);
        } else {
            return false;
        }
    }

    @Override
    public boolean containsValue(Object value) {
        throw new UnsupportedOperationException("We can't search by value in a PTree");
    }

    @Override
    public T get(Object key) {
         return (key instanceof String) ? getInternal((String) key) : null;
    }

    @Override
    public T put(String key, T value) {
        PTreeString<T> root = this;
        for(int i=0; i<key.length(); i++) {
            final char c = key.charAt(i);
            PTreeString<T> child = root.children.get(c);
            if (child==null) {
                child = new PTreeString<>(value);
                root.children.put(c, child);
            }
            root = child;
        }
        final T prev = root.info;
        root.info = value;
        return prev;
    }

    @Override
    public T remove(Object key) {
        return (key instanceof String) ? removeInternal((String) key) : null;
    }

    protected final T removeInternal(String text) {
        PTreeString<T> tree = find(text);
        if (tree!=null) {
            final T x = tree.info;
            tree.info = null;
            return x;
        }
        return null;
    }

    @Override
    public void putAll(Map<? extends String, ? extends T> m) {
        m.entrySet().stream().forEach(e -> put(e.getKey(), e.getValue()));
    }

    @Override
    public void clear() {
        children.clear();
        info = null;
    }

    @Override
    public Set<String> keySet() {
        return entrySet().stream().map(e -> e.getKey()).collect(Collectors.toSet());
    }

    @Override
    public Collection<T> values() {
        return entrySet().stream().map(e -> e.getValue()).collect(Collectors.toList());
    }

    @Override
    public Set<Map.Entry<String, T>> entrySet() {
        final Set<Map.Entry<String, T>> entries = new TreeSet<>((a, b) -> a.getKey().compareTo(b.getKey()));

        if (info != null) {
            Entry entry = new Entry("", info);
            entries.add(entry);
        }

        children.entrySet().stream()
                .forEach(e -> {
                final char c = e.getKey();
                final Set<Map.Entry<String, T>> childEntries = e.getValue().entrySet();
                childEntries.stream()
                    .forEach(entry -> {
                        final Map.Entry<String, T> newEntry =new Entry(c + entry.getKey(), entry.getValue());
                        entries.add(newEntry);
                    });
            }
        );
        return entries;
    }

    public final Set<T> getSuggestions(final String prefix) {
        final PTreeString<T> root = find(prefix);
        final Set<T> theSet = new HashSet<>(); // Going with a hashmap for this one... for now.

        if (root != null) {
            if (root.info != null) {
                theSet.add(root.info);
            }
            root.entrySet().stream().forEach(e -> {
                theSet.add(e.getValue());
            });
        }
        return theSet;
    }
}
