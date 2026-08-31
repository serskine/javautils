package javautils.search.fringe;

import javautils.search.Market;
import javautils.search.Outcome;
import javautils.search.dmatrix.Assumption;
import javautils.search.dmatrix.DecisionMatrix;
import javautils.search.Fringe;
import javautils.search.Path;
import javautils.search.dmatrix.Pair;

import java.util.Optional;
import java.util.function.Function;

public class DecisionMatrixFringe<State, Action> implements Fringe<State, Action> {
    private DecisionMatrix<Path<State, Action>, Action> matrix;
    private Function<State, Outcome> outcomeFactory;
    final DecisionMatrix.Strategy strategy;

    public DecisionMatrixFringe(final Function<State, Outcome> outcomeFactory, final DecisionMatrix.Strategy strategy) {
        this.matrix = new DecisionMatrix<>();
        this.outcomeFactory = outcomeFactory;
        this.strategy = strategy;
    }

    @Override
    public boolean push(Path<State, Action> path) {
        final Action action = path.firstAction;
        final State state = path.state;
        final Outcome outcome = outcomeFactory.apply(state);
        matrix.addOutcome(action, path, outcome);
        return true;
    }

    @Override
    public Path<State, Action> pop() {
        Pair<Action, Double> bestOption = matrix.getBestOption(strategy);
        Action action = bestOption.getKey();
        
        // Get the first (or only) path associated with this action
        // The matrix stores Path objects as the "State" key in the outcomes map
        final Market<Path<State, Action>> market = matrix.getOutcomes(action);
        final Path<State, Action> choice = market.chooseRandomByProbability();
        matrix.removeOutcome(action, choice);

        return choice;
    }

    @Override
    public void push(Path<State, Action>... paths) {
        for (Path<State, Action> path : paths) {
            push(path);
        }
    }

    @Override
    public boolean hasNext() {
        return !matrix.isEmpty();
    }
}
