package javautils.common;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;

public class SetUtils {
    public static <T> Set<T> intersection(Set<T> a, Set<T> b) {
        return a.stream().filter(x -> b.contains(x)).collect(Collectors.toSet());
    }

    public static <T> Set<T> union(Set<T> a, Set<T> b) {
        final Set<T> r = new HashSet<>();
        r.addAll(a);
        r.addAll(b);
        return r;
    }

    public static <T> boolean isDisjoint(Set<T> a, Set<T> b) {
        return intersection(a, b).isEmpty();
    }

    public static <T> Set<T> symmetricDifference(Set<T> a, Set<T> b) {
        final Set<T> r = new HashSet<>();
        r.addAll(difference(a, b));
        r.addAll(difference(b, a));
        return r;
    }

    public static <T> Set<T> difference(Set<T> a, Set<T> b) {
        return a.stream().filter(x -> !b.contains(x)).collect(Collectors.toSet());
    }

}
