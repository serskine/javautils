package javautils.search.fringe;

import javautils.search.Fringe;
import javautils.search.Path;

import java.util.ArrayList;
import java.util.Random;

public class RandomFringe<State, Action> implements Fringe<State,Action> {

    private ArrayList<Path<State,Action>> paths = new ArrayList<>();
    private Random random = new Random();

    @Override
    public void push(Path<State, Action>... toPush) {
        for(Path<State,Action> path : toPush) {
            paths.add(path);
        }
    }

    @Override
    public boolean push(Path<State, Action> path) {
        return paths.add(path);
    }

    @Override
    public Path<State, Action> pop() {
        final int index = random.nextInt(paths.size());
        return paths.remove(index);
    }

    @Override
    public boolean hasNext() {
        return !paths.isEmpty();
    }
}
