package javautils.search;

import org.w3c.dom.Node;

import java.util.Objects;

public class Path<State, Action> {
    public final State state;
    public final State initialState;
    public final Action action;
    public final Action firstAction;
    public final Path<State, Action> prev;

    public Path(final State initialState) {
        this(initialState, null, null);
    }

    public Path(final State state, final Action action, final Path<State, Action> prev) {
        if (state==null) {
            throw new IllegalArgumentException("All paths must contain at least one state");
        }
        this.state = state;
        this.action = action;
        this.prev = prev;
        if (prev == null) {
            initialState = state;
            firstAction = action;
        } else {
            initialState = prev.initialState;
            firstAction = prev.firstAction;
        }
    }

    public Path<State, Action> findPathUntil(final State state) {
        if (state==null) {
            throw new IllegalArgumentException("Paths can't contain null states.");
        } else if (this.state.equals(state)) {
            return this;
        } else if (prev!=null) {
            return prev.findPathUntil(state);
        } else {
            return null;
        }
    }

    public final boolean isEquivelentTo(final Path<State, Action> other) {
        return isEquivelent(this, other);
    }

    public final boolean containsPath(final Path<State, Action> other) {
        return contains(this, other);
    }

    public final boolean contains(final State state) {
        if (state==null) {
            throw new IllegalArgumentException("Paths must contain at least one state");
        } else if (this.state.equals(state)) {
            return true;
        } else if (prev!=null) {
            return prev.contains(state);
        } else {
            return false;
        }
    }

    private static <S, A> boolean isEquivelent(final Path<S, A> left, final Path<S, A> right) {
        if (left==null) return (right==null);
        if (right==null) return false;
        if (Objects.equals(left.state, right.state)) {
            return isEquivelent(left.prev, right.prev);
        } else {
            return false;
        }
    }

    private static <S, A> boolean contains(final Path<S, A> parent, final Path<S, A> child) {
        if (parent==null) return child==null;
        Path<S, A> left = parent;
        Path<S, A> right = child;
        boolean done = false;
        do {
            if (!isNodeSame(left, right)) {
                if (right==null) {
                    return true;
                }
                done = true;
            }
            left = left.prev;
            right = right.prev;
        } while(!done);

        return contains(parent.prev, child);
    }

    private static boolean isNodeSame(final Path<?, ?> left, final Path<?, ?> right) {
        if (left==null) return (right==null);
        if (right==null) return false;
        return Objects.equals(left.state, right.state) && Objects.equals(left.action, right.action);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Path {");
        Path<State, Action> ptr = this;
        int n = 0;
        while(ptr != null) {
            if (n>0) {
                sb.append(" <- ");
            }
            sb.append("[");
            sb.append(ptr.state);
            sb.append("]");
            ptr = ptr.prev;
            n++;
        }
        sb.append("}");
        return sb.toString();
    }

    @Override
    public int hashCode() {
        return state.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof Path<?,?>) {
            final Path<?,?> other = (Path<?,?>) obj;
            return isNodeSame(this, other);
        } else {
            return false;
        }
    }
}
