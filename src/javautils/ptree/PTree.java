package javautils.ptree;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings({"unchecked", "rawtypes"})
public class PTree<K extends Comparable<K>, V> implements Map<Iterable<K>, V> {

    private Integer size = null;
    private V info;
    private final TreeMap<K, PTree<K, V>> children = new TreeMap<>(Comparator.naturalOrder());

    public PTree() {
        this(null);
    }

    protected PTree(final V info) {
        this.info = info;
    }

    public class Entry implements Map.Entry<Iterable<K>, V> {
        private final Iterable<K> key;
        private V value;

        public Entry(final Iterable<K> key, final V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public Iterable<K> getKey() { return this.key;   }

        @Override
        public V getValue() {   return this.value;  }

        @Override
        public V setValue(V value) {
            final V old = this.value;
            this.value = value;
            return old;
        }
    }


    private PTree<K, V> find(Iterable<K> key, boolean clearSize) {
        return find(key.iterator(), clearSize);
    }

    /**
     * Internal method for finding the node for the sequence
     * @param key is an iterable of characters of type K
     * @return the node for this ptree
     */
    private PTree<K, V> find(Iterator<K> key, boolean clearSize) {
        if (key==null) {
            return null;
        }
        if (!key.hasNext()) {
            if (clearSize) {
                size = null;
            }
            return this;
        }

        PTree<K, V> root = this;
        while (key.hasNext()) {
            root.size = (clearSize) ? null : root.size;
            K k = key.next();
            PTree<K, V> child = root.children.get(k);
            if (child==null) {
                return null;
            } else {
                root = child;
            }
        }
        return root;
    }


    protected final V removeInternal(Iterable<K> key) {
        PTree<K, V> tree = find(key, true);
        if (tree!=null) {
            final V x = tree.info;
            tree.info = null;
            return x;
        }
        return null;
    }

    /**
     * TODO: Come up with a clever way to append the two lists without destroying the old list
     */
    protected final Iterable<K> concat(K prefix, Iterable<K> suffix) {
        final LinkedList<K> newList = new LinkedList<>();
        newList.add(prefix);
        for(K k : suffix) {
            newList.add(k);
        }
        return newList;
    }

    // ---- Required for the map interface. Everything else should be internal ---- //

    @Override
    public int size() {
        size = (info == null) ? 0 : 1;
        children.values().forEach(child -> size += child.size);
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean containsKey(Object key) {
        if (key instanceof Iterable) {
            final PTree<K, V> node = find((Iterable<K>) key, false);
            return (node != null) && (node.info != null);
        } else {
            return false;
        }
    }

    @Override
    public boolean containsValue(Object o) {
        return values().stream().anyMatch(v -> Objects.equals(v, o));
    }

    @Override
    public V get(Object obj) {
        try {
            Iterable<K> key = (Iterable<K>) obj;
            final PTree<K, V> node = find(key, false);
            if (node != null) {
                return node.info;
            } else {
                return null;
            }
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("Key must be an Iterable of type K", e);
        }
    }

    public final V put(K[] key, V value) {
        return put(Arrays.asList(key), value);
    }

    @Override
    public final V put(Iterable<K> key, V value) {
        PTree<K, V> root = this;
        for(K c : key) {
            PTree<K, V> child = root.children.get(c);
            if (child==null) {
                child = new PTree<>(value);
                root.children.put(c, child);
            }
            root = child;
        }
        final V prev = root.info;
        root.info = value;

        return prev;
    }

    @Override
    public final V remove(Object key) {
        return (key instanceof Iterable) ? removeInternal((Iterable<K>) key) : null;
    }

    public final V remove(K... key) {
        return removeInternal(Arrays.asList(key));
    }

    @Override
    public void putAll(Map<? extends Iterable<K>, ? extends V> map) {
        map.entrySet().forEach(e -> put(e.getKey(), e.getValue()));
    }

    @Override
    public void clear() {
        children.clear();
        info = null;
        size = null;
    }

    @Override
    @NotNull
    public Set<Iterable<K>> keySet() {
        return this.entrySet().stream()
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    @Override
    @NotNull
    public Collection<V> values() {
        return entrySet().stream().map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }

    @Override
    @NotNull
    public Set<Map.Entry<Iterable<K>, V>> entrySet() {
        final Set<Map.Entry<Iterable<K>, V>> entries = new HashSet<>();

        if (info != null) {
            final Iterable<K> newKey = new LinkedList<>();
            PTree.Entry entry = new PTree.Entry(newKey, info);
            entries.add(entry);
        }

        children.entrySet()
                .forEach(e -> {
                final K c = e.getKey();
                final Set<Map.Entry<Iterable<K>, V>> childEntries = e.getValue().entrySet();
                childEntries.forEach(entry -> {
                        final Iterable<K> newKey = concat(c, entry.getKey());
                        final Map.Entry<Iterable<K>, V> newEntry =new PTree.Entry(newKey, entry.getValue());
                        entries.add(newEntry);
                    });
            }
        );
        return entries;
    }

    public final Set<V> getSuggestions(final Iterable<K> prefix) {
        return getSuggestions(prefix, null);
    }

    public final Set<V> getSuggestions(final Iterable<K> prefix, final Comparator<V> valueComparator) {
        final PTree<K, V> root = find(prefix, false);
        final Set<V> theSet = (valueComparator==null) ? new HashSet<>() : new TreeSet<>(valueComparator);

        if (root != null) {
            if (root.info != null) {
                theSet.add(root.info);
            }
            root.entrySet().forEach(e -> theSet.add(e.getValue()));
        }
        return theSet;
    }

}

