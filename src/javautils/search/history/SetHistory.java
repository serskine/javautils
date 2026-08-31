package javautils.search.history;

import javautils.search.History;
import javautils.search.Path;

import java.util.HashSet;
import java.util.Set;

public class SetHistory<State, Action> implements History<State, Action> {

    private final Set<Path<State, Action>> set = new HashSet<>();

    @Override
    public boolean contains(Path<State, Action> path) {
        return set.contains(path);
    }

    @Override
    public boolean push(Path<State, Action> path) {
        return false;
    }

    @Override
    public Path<State, Action> pop() {
        return null;
    }

    @Override
    public boolean add(Path<State, Action> current) {
        return set.add(current);
    }

    @Override
    public boolean hasNext() {
        return !set.isEmpty();
    }

    @Override
    public Path<State, Action> next() {
        return set.iterator().next();
    }

    @Override
    public void push(Path<State, Action>... paths) {

    }
}
