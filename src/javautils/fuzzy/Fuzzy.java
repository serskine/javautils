package javautils.fuzzy;

import javautils.Text;
import javautils.common.DefaultMap;
import javautils.common.SetUtils;

import java.awt.*;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class Fuzzy<K> extends DefaultMap<K, Probability> {

    public Fuzzy() {
        this(Probability.never());
    }

    public Fuzzy(Probability dValue) {
        super(dValue);
    }

    public static <K> Fuzzy<K> and(Fuzzy<K> a, Fuzzy<K> b)       {
        Probability newDefault = Probability.and(a.getDefaultValue(), b.getDefaultValue());
        final Fuzzy<K> r = new Fuzzy<>(newDefault);
        final Set<K> newKeySet = SetUtils.union(a.keySet(), b.keySet());
        for(K key : newKeySet) {
            final Probability aProb = a.get(key);
            final Probability bProb = b.get(key);
            final Probability rProb = Probability.and(aProb, bProb);
            r.put(key, rProb);
        }

        return r;
    }

    public static <K> Fuzzy<K> or(Fuzzy<K> a, Fuzzy<K> b)       {
        Probability newDefault = Probability.or(a.getDefaultValue(), b.getDefaultValue());
        final Fuzzy<K> r = new Fuzzy<>(newDefault);
        final Set<K> newKeySet = SetUtils.union(a.keySet(), b.keySet());
        for(K key : newKeySet) {
            final Probability aProb = a.get(key);
            final Probability bProb = b.get(key);
            final Probability rProb = Probability.or(aProb, bProb);
            r.put(key, rProb);
        }

        return r;
    }

    public static <K> boolean isSame(Fuzzy<K> a, Fuzzy<K> b) {
        if (!Objects.equals(a.getDefaultValue(), b.getDefaultValue())) {
            return false;
        }
        final Set<K> newKeySet = SetUtils.union(a.keySet(), b.keySet());
        for(K k : newKeySet) {
            final Probability aProb = a.get(k);
            final Probability bProb = b.get(b);
            if (!Objects.equals(aProb, bProb)) {
                return false;
            }
        }
        return true;
    }

    public static <K> Fuzzy<K> not(Fuzzy<K> a) {
        final Probability newDefault = Probability.not(a.getDefaultValue());
        final Fuzzy<K> r = new Fuzzy<>(newDefault);
        for(K key : a.keySet()) {
            final Probability aProb = a.get(key);
            r.put(key, Probability.not(aProb));
        }
        return r;
    }

    public static <K> Fuzzy<K> xor(Fuzzy<K> a, Fuzzy<K> b) {
        return and(or(a,b), not(and(a,b)));
    }

    public static <K> Fuzzy<K> implies(Fuzzy<K> a, Fuzzy<K> b) {
        return or(not(a), b);
    }

    public String describe() {
        List<K> sortedKeys = keySet().stream().sorted((a,b) -> a.toString().compareToIgnoreCase(b.toString())).toList();
        final Dimension maxCellSize = Text.maxCellSize(sortedKeys.stream().map(k -> k.toString()).collect(Collectors.toSet()));
        final StringBuilder sb = new StringBuilder();
        for(K key : sortedKeys) {
            final String keyField = Text.fstring(maxCellSize.width, key.toString());
            final String valueField = get(key).toString();
            sb.append(keyField).append(": ").append(valueField).append("\n");
        }
        return sb.toString();
    }

}
