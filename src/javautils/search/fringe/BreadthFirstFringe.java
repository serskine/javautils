package javautils.search.fringe;

import javautils.search.Fringe;
import javautils.search.Path;

import java.util.LinkedList;
import java.util.Queue;

public class BreadthFirstFringe<State, Action> implements Fringe<State, Action> {
    private final Queue<Path<State,Action>> queue = new LinkedList<>();

    @Override
    public void push(Path<State, Action>... paths) {
        for(Path<State,Action> path : paths) {
            queue.add(path);
        }
    }

    @Override
    public boolean push(Path<State, Action> path) {
        return queue.offer(path);
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
