package javautils.search.dmatrix;

import javautils.search.Outcome;

import java.util.Map;

public class Assumption<Action, State> implements Map.Entry<Action, Map.Entry<State, Outcome>> {
    private Action action;
    private State state;
    private Outcome outcome;

    public Assumption(Action action, State state, Outcome outcome) {
        this.action = action;
        this.state = state;
        this.outcome = outcome;
    }

    public Assumption(Action action, Map.Entry<State, Outcome> result) {
        this(
                action,
                result.getKey(),
                result.getValue()
        );
    }

    public Assumption(Map.Entry<Action, Map.Entry<State, Outcome>> x) {
        this(
                x.getKey(),
                x.getValue().getKey(),
                x.getValue().getValue()
        );
    }

    @Override
    public Action getKey() {
        return this.action;
    }

    @Override
    public Map.Entry<State, Outcome> getValue() {
        return new Pair<>(this.state, this.outcome);
    }

    @Override
    public Map.Entry<State, Outcome> setValue(final Map.Entry<State, Outcome> stateOutcomeEntry) {
        final Map.Entry<State, Outcome> oldValue = getValue();
        this.state = stateOutcomeEntry.getKey();
        this.outcome = stateOutcomeEntry.getValue();
        return oldValue;
    }

    public Action setAction(Action action) {
        final Action oldAction = this.action;
        this.action = action;
        return oldAction;
    }

    public State setState(State state) {
        final State oldState = this.state;
        this.state = state;
        return oldState;
    }

    public Outcome setOutcome(Outcome outcome) {
        final Outcome oldOutcome = this.outcome;
        this.outcome = outcome;
        return oldOutcome;
    }

}
