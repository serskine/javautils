package javautils.search.fringe;

import javautils.search.Fringe;
import javautils.search.Path;

import java.util.Comparator;
import java.util.PriorityQueue;

public class BestFirstFringe<State, Action> implements Fringe<State,Action> {

    private final PriorityQueue<Path<State,Action>> queue;

    public BestFirstFringe(final Comparator<Path<State,Action>> comparator) {
        queue = new PriorityQueue<>(comparator);
    }

    @Override
    public void push(Path<State, Action>... paths) {
        for(Path<State, Action> path : paths) {
            queue.add(path);
        }
    }

    @Override
    public boolean push(Path<State, Action> path) {
        return queue.add(path);
    }

    @Override
    public Path<State, Action> pop() {
        return queue.remove();
    }

    @Override
    public boolean hasNext() {
        return !queue.isEmpty();
    }
}
