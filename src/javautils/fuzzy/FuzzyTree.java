package javautils.fuzzy;

import javautils.common.SetUtils;
import javautils.ptree.PTree;

import java.util.Set;

public class FuzzyTree<K extends Comparable<K>, V> {

    private PTree<K, Fuzzy<V>> ptree;

    public FuzzyTree() {
        this.ptree = new PTree<>();
    }

    public final void suggest(Iterable<K> key, V value, Probability probability) {
        final Fuzzy<V> prevKeyFuzzy = ptree.getOrDefault(key, new Fuzzy<>());

        prevKeyFuzzy.put(value, probability);
        ptree.put(key, prevKeyFuzzy);

        final Iterable<K> keyTail = SetUtils.tail(key);
        if (keyTail.iterator().hasNext()) {
            suggest(keyTail, value, probability);
        }
    }

    // This will return all fuzzies that are suggested by the iterable
    public Set<Fuzzy<V>> getFuzzySuggestions(final Iterable<K> key) {
        return ptree.getSuggestions(key);
    }

    // This will return a combined Fuzzy that represents the average of all the fuzzies sugested by the iterable
    public Fuzzy<V> getAverageFuzzy(final Iterable<K> key) {
        final Set<Fuzzy<V>> avg = getFuzzySuggestions(key);
        return Fuzzy.average(avg);
    }
}
