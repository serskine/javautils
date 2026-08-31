package javautils.search;

import java.util.Iterator;
import java.util.function.Supplier;

public interface Fringe<State, Action> extends Iterator<Path<State, Action>>, Supplier<Path<State, Action>> {

    boolean push(Path<State, Action> path);

    Path<State,Action> pop();

    default boolean add(Path<State, Action> current) {
        push(current);
        return false;
    }

    default Path<State,Action> next() {
        return pop();
    }

    void push(final Path<State, Action>... paths);

    default void push(final Iterable<Path<State, Action>> paths) {
        push(paths.iterator());
    }

    default void push(final Iterator<Path<State, Action>> iterator) {
        while (iterator.hasNext()) {
            push(iterator.next());
        }
    }

    default Path<State,Action> get() {
        return next();
    }

}
