package javautils.search;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class Search<State, Action> implements Fringe<State, Action> {

    private Rules<State, Action> rules;
    private Fringe<State, Action> history;
    private Fringe<State, Action> fringe;
    private Path<State, Action> current;

    public Search(final Rules rules, final Fringe<State, Action> history, final Fringe<State, Action> fringe) {
        this.rules = rules;
        this.history = history;
        this.fringe = fringe;
    }

    @Override
    public final boolean hasNext() {
        while(current == null && fringe.hasNext()) {
            Path<State, Action> a = fringe.next();
            boolean changed = true;
            history.add(a);
            if(changed) {
                current = a;
            }
        }
        return (current != null);
    }

    @Override
    public final boolean push(Path<State, Action> path) {

        fringe.push(path);
        return true;
    }

    @Override
    public final Path<State, Action> pop() {

        if (hasNext()) {
            final Iterator<Path<State, Action>> childrenItr = rules.expand(current);
            push(childrenItr);
            final Path<State, Action> result = current;
            current = null;
            return result;
        } else {
            throw new NoSuchElementException("Fringe is empty");
        }
    }

    @Override
    public void push(Path<State, Action>... paths) {
        for (Path<State, Action> path : paths) {
            push(path);
        }
    }

}
