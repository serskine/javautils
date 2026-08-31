package javautils.search;

public interface History<State, Action> extends Fringe<State, Action> {
    boolean contains(Path<State, Action> path);
}
