package javautils.search.fringe;

import javautils.search.Fringe;
import javautils.search.Path;

import java.util.ArrayList;
import java.util.Stack;

public class DepthFirstFringe<State, Action> implements Fringe<State, Action> {

    private final Stack<Path<State,Action>> stack = new Stack<>();

    @Override
    public void push(Path<State, Action>... paths) {
        for(Path<State,Action> path : paths) {
            stack.push(path);
        }
    }

    @Override
    public boolean hasNext() {
        return !stack.isEmpty();
    }

    @Override
    public boolean push(Path<State, Action> path) {
        stack.push(path);
        return true;
    }

    @Override
    public Path<State, Action> pop() {
        return stack.pop();
    }
}
