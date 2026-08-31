package javautils.search;

import java.util.Iterator;

public interface Rules<State, Action> {
    Iterator<Path<State, Action>> expand(Path<State, Action> current);
}
