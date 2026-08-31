package javautils.search.dmatrix;

import javautils.search.Market;
import javautils.search.Outcome;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class DecisionMatrix<State, Action> {

    private Map<Action, Map<State, Outcome>> matrix = new HashMap<>();

    public enum Strategy {
        HIGHEST_EXPECTED_VALUE,
        LOWEST_EXPECTED_VALUE,
        ACTION_EXPECTED_VALUE,
        DUMB_OPTIMISTIC,
        SMART_OPTIMISTIC,
        DUMB_PESSIMISTIC,
        SMART_PESSIMISTIC,
        DUMB_SUICIDAL_OPTIMISTIC,
        SMART_SUICIDAL_OPTIMISTIC,
        DUMB_SUICIDAL_PESSIMISTIC,
        SMART_SUICIDAL_PESSIMISTIC,
        BEST_OPTION,
        WORST_OPTION
    }

    public boolean isEmpty() {
        return matrix.isEmpty();
    }

    protected final void verifyMatrixNotEmpty() {
        if (matrix.isEmpty()) {
            throw new RuntimeException("A decision can't be made. There are no action options!");
        }
    }

    private Map<State, Outcome> getActionOutcomes(Action action) {
        final Map<State, Outcome> map = matrix.get(action);
        if (map == null) {
            throw new RuntimeException("Action " + action + " does not exist!");
        }
        return map;
    }

    private Map<State, Outcome> getActionOutcomesOrThrow(Action action) {
        final Map<State, Outcome> map = getActionOutcomes(action);
        if (map.isEmpty()) {
            throw new RuntimeException("Action " + action + " does not have any outcomes!");
        }
        return map;
    }

    //----- Maintaining contents -----//

    public void addOutcome(Action action, State state, Outcome outcome) {
        final Map<State, Outcome> map = matrix.put(action, new HashMap<>());
        map.put(state, outcome);
    }

    public void removeOutcome(Action action, State state) {
        final Map<State, Outcome> stateOutcomes = matrix.get(action);
        if (stateOutcomes != null) {
            stateOutcomes.remove(state);
            if (stateOutcomes.isEmpty()) {
                matrix.remove(action);
            }
        }
    }

    public Outcome getOutcome(Action action, State state) {
        final Map<State, Outcome> stateOutcomeMap = getActionOutcomes(action);
        final Outcome outcome = stateOutcomeMap.get(state);
        if (outcome == null) {
            throw new RuntimeException("Action " + action + " does not have an outcome for state " + state);
        }
        return outcome;
    }


    public final Pair<State, Outcome> getHighestValue(Action action) {
        return new Pair(getActionOutcomesOrThrow(action).entrySet().stream()
            .max((e1, e2) -> Double.compare(e1.getValue().value, e2.getValue().value)).get());
    }

    public final Pair<State, Outcome> getLowestValue(Action action) {
        return new Pair(getActionOutcomesOrThrow(action).entrySet().stream()
                .max((e1, e2) -> Double.compare(e2.getValue().value, e1.getValue().value)).get());
    }

    public final Pair<State, Outcome> getHighestExpectedValue(Action action) {
        return new Pair(getActionOutcomesOrThrow(action).entrySet().stream()
                .max((e1, e2) -> Double.compare(e1.getValue().expectedValue, e2.getValue().expectedValue)).get());
    }

    public final Pair<State, Outcome> getLowestExpectedValue(Action action) {
        return new Pair(getActionOutcomesOrThrow(action).entrySet().stream()
                .max((e1, e2) -> Double.compare(e2.getValue().expectedValue, e1.getValue().expectedValue)).get());
    }

    public final Market<State> getOutcomes(final Action action) {
        final Market<State> market = new Market<>();
        getActionOutcomesOrThrow(action).entrySet().forEach(e -> {
            market.put(e.getKey(), e.getValue().prob);
        });
        return market;
    }

    public double getActionExpectedValue(Action action) {
        return getActionOutcomesOrThrow(action).entrySet().stream().mapToDouble(e -> e.getValue().expectedValue).sum();
    }

    //------ Decision making -----//


    /**
     * Selects the action with the highest possible outcome value, ignoring probability.
     * This is a naive optimistic strategy that assumes the best-case scenario will occur.
     *
     * @return an Assumption containing the selected action, its best-case state, and the corresponding outcome
     */
    public Assumption assumeDumbOptimisticOption() {
        verifyMatrixNotEmpty();
        final Action selectedAction = matrix.keySet().stream().max((a1, a2) -> {
            final double v1 = getHighestValue(a1).getValue().value;
            final double v2 = getHighestValue(a2).getValue().value;
            return Double.compare(v1, v2);
        }).get();
        final Pair<State, Outcome> highestValueEntry = getHighestValue(selectedAction);
        return new Assumption(selectedAction, highestValueEntry);
    }

    /**
     * Selects the action whose best outcome has the highest expected value, factoring in probability.
     * This is a realistic optimistic strategy that considers probability-weighted outcomes.
     *
     * @return an Assumption containing the selected action, its best expected outcome state, and the corresponding outcome
     */
    public Assumption assumeSmartOptimisticOption() {
        verifyMatrixNotEmpty();
        Action selectedAction = matrix.keySet().stream().max((a1, a2) -> {
            final double v1 = getHighestValue(a1).getValue().expectedValue;
            final double v2 = getHighestValue(a2).getValue().expectedValue;
            return Double.compare(v1, v2);
        }).get();
        Map.Entry<State, Outcome> highestValueEntry = getHighestValue(selectedAction);
        return new Assumption(selectedAction, highestValueEntry.getKey(), highestValueEntry.getValue());
    }

    /**
     * Selects the action with the best worst-case outcome, ignoring probability.
     * This is a naive pessimistic (conservative/maximin) strategy that assumes the worst-case scenario will occur
     * and chooses the action where that worst case is still the best.
     *
     * @return an Assumption containing the selected action, its worst-case state, and the corresponding outcome
     */
    public Assumption assumeDumbPessimisticOption() {
        verifyMatrixNotEmpty();
        Action selectedAction = matrix.keySet().stream().max((a1, a2) -> {
            final double v1 = getLowestValue(a1).getValue().value;
            final double v2 = getLowestValue(a2).getValue().value;
            return Double.compare(v1, v2);
        }).get();
        Map.Entry<State, Outcome> lowestValueEntry = getLowestValue(selectedAction);
        return new Assumption(selectedAction, lowestValueEntry.getKey(), lowestValueEntry.getValue());
    }

    /**
     * Selects the action whose worst outcome has the highest expected value, factoring in probability.
     * This is a realistic pessimistic (conservative) strategy that considers the worst probable scenario
     * and chooses the action where that worst scenario is most favorable.
     *
     * @return an Assumption containing the selected action, its worst expected outcome state, and the corresponding outcome
     */
    public Assumption assumeSmartPessimisticOption() {
        verifyMatrixNotEmpty();
        Action selectedAction = matrix.keySet().stream().max((a1, a2) -> {
            final double v1 = getLowestExpectedValue(a1).getValue().expectedValue;
            final double v2 = getLowestExpectedValue(a2).getValue().expectedValue;
            return Double.compare(v1, v2);
        }).get();
        Map.Entry<State, Outcome> lowestExpectedValueEntry = getLowestExpectedValue(selectedAction);
        return new Assumption(selectedAction, lowestExpectedValueEntry.getKey(), lowestExpectedValueEntry.getValue());
    }

    /**
     * Selects the action with the lowest highest value outcome, ignoring probability.
     * This is a self-defeating optimistic strategy that chooses the action with the worst best-case scenario.
     * Counterintuitive: picks the action that has the poorest possible positive outcome.
     *
     * @return an Assumption containing the worst-selected action, its best-case state, and the corresponding outcome
     */
    public Assumption assumeDumbSuicidalOptimisticOption() {
        verifyMatrixNotEmpty();
        Action selectedAction = matrix.keySet().stream().min((a1, a2) -> {
            final double v1 = getHighestValue(a1).getValue().value;
            final double v2 = getHighestValue(a2).getValue().value;
            return Double.compare(v1, v2);
        }).get();
        Map.Entry<State, Outcome> highestValueEntry = getHighestValue(selectedAction);
        return new Assumption(selectedAction, highestValueEntry.getKey(), highestValueEntry.getValue());
    }

    /**
     * Selects the action whose best outcome has the lowest expected value, factoring in probability.
     * This is a self-defeating realistic strategy that chooses the action with the poorest best-case expected outcome.
     * Counterintuitive: picks the action that offers the least promising probability-weighted best result.
     *
     * @return an Assumption containing the worst-selected action, its best expected outcome state, and the corresponding outcome
     */
    public Assumption assumeSmartSuicidalOptimisticOption() {
        verifyMatrixNotEmpty();
        Action selectedAction = matrix.keySet().stream().min((a1, a2) -> {
            final double v1 = getHighestExpectedValue(a1).getValue().expectedValue;
            final double v2 = getHighestExpectedValue(a2).getValue().expectedValue;
            return Double.compare(v1, v2);
        }).get();
        Map.Entry<State, Outcome> highestExpectedValueEntry = getHighestExpectedValue(selectedAction);
        return new Assumption(selectedAction, highestExpectedValueEntry.getKey(), highestExpectedValueEntry.getValue());
    }

    /**
     * Selects the action with the lowest worst-case value, ignoring probability.
     * This is a self-defeating pessimistic strategy that picks the action with the absolute worst worst-case scenario.
     * Counterintuitive: combines pessimism with poor judgment to select the most dangerous action.
     *
     * @return an Assumption containing the worst-selected action, its worst-case state, and the corresponding outcome
     */
    public Assumption assumeDumbSuicidalPessimisticOption() {
        verifyMatrixNotEmpty();
        Action selectedAction = matrix.keySet().stream().min((a1, a2) -> {
            final double v1 = getLowestValue(a1).getValue().value;
            final double v2 = getLowestValue(a2).getValue().value;
            return Double.compare(v1, v2);
        }).get();
        Map.Entry<State, Outcome> lowestValueEntry = getLowestValue(selectedAction);
        return new Assumption(selectedAction, lowestValueEntry.getKey(), lowestValueEntry.getValue());
    }

    /**
     * Selects the action whose worst outcome has the lowest expected value, factoring in probability.
     * This is a self-defeating realistic pessimistic strategy that chooses the action with the most likely terrible outcome.
     * Counterintuitive: combines probability analysis with poor judgment to select the most unfavorable action.
     *
     * @return an Assumption containing the worst-selected action, its worst expected outcome state, and the corresponding outcome
     */
    public Assumption assumeSmartSuicidalPessimisticOption() {
        verifyMatrixNotEmpty();
        Action selectedAction = matrix.keySet().stream().min((a1, a2) -> {
            final double v1 = getLowestExpectedValue(a1).getValue().expectedValue;
            final double v2 = getLowestExpectedValue(a2).getValue().expectedValue;
            return Double.compare(v1, v2);
        }).get();
        Map.Entry<State, Outcome> lowestExpectedValueEntry = getLowestExpectedValue(selectedAction);
        return new Assumption(selectedAction, lowestExpectedValueEntry.getKey(), lowestExpectedValueEntry.getValue());
    }

    public Pair<Action, Double> getBestOption() {
        verifyMatrixNotEmpty();
        return matrix.keySet().stream()
            .map(a -> new Pair<>(a, getActionExpectedValue(a)))
            .max((p1, p2) -> Double.compare(p1.getValue(), p2.getValue()))
            .get();
    }

    public Pair<Action, Double> getWorstOption() {
        verifyMatrixNotEmpty();
        return matrix.keySet().stream()
            .map(a -> new Pair<>(a, getActionExpectedValue(a)))
            .min((p1, p2) -> Double.compare(p1.getValue(), p2.getValue()))
            .get();
    }

    public Pair<Action, Double> getBestOption(Strategy strategy) {
        verifyMatrixNotEmpty();
        switch(strategy) {
            case HIGHEST_EXPECTED_VALUE:
                Action selectedAction = matrix.keySet().stream().max((a1, a2) -> {
                    final double v1 = getHighestExpectedValue(a1).getValue().expectedValue;
                    final double v2 = getHighestExpectedValue(a2).getValue().expectedValue;
                    return Double.compare(v1, v2);
                }).get();
                return new Pair<>(selectedAction, getHighestExpectedValue(selectedAction).getValue().expectedValue);
            case LOWEST_EXPECTED_VALUE:
                selectedAction = matrix.keySet().stream().max((a1, a2) -> {
                    final double v1 = getLowestExpectedValue(a1).getValue().expectedValue;
                    final double v2 = getLowestExpectedValue(a2).getValue().expectedValue;
                    return Double.compare(v1, v2);
                }).get();
                return new Pair<>(selectedAction, getLowestExpectedValue(selectedAction).getValue().expectedValue);
            case ACTION_EXPECTED_VALUE:
                return new Pair<>(
                    matrix.keySet().stream().max((a1, a2) -> Double.compare(getActionExpectedValue(a1), getActionExpectedValue(a2))).get(),
                    getActionExpectedValue(matrix.keySet().stream().max((a1, a2) -> Double.compare(getActionExpectedValue(a1), getActionExpectedValue(a2))).get())
                );
            case DUMB_OPTIMISTIC:
                selectedAction = matrix.keySet().stream().max((a1, a2) -> {
                    final double v1 = getHighestValue(a1).getValue().value;
                    final double v2 = getHighestValue(a2).getValue().value;
                    return Double.compare(v1, v2);
                }).get();
                return new Pair<>(selectedAction, getHighestValue(selectedAction).getValue().value);
            case SMART_OPTIMISTIC:
                selectedAction = matrix.keySet().stream().max((a1, a2) -> {
                    final double v1 = getHighestValue(a1).getValue().expectedValue;
                    final double v2 = getHighestValue(a2).getValue().expectedValue;
                    return Double.compare(v1, v2);
                }).get();
                return new Pair<>(selectedAction, getHighestValue(selectedAction).getValue().expectedValue);
            case DUMB_PESSIMISTIC:
                selectedAction = matrix.keySet().stream().max((a1, a2) -> {
                    final double v1 = getLowestValue(a1).getValue().value;
                    final double v2 = getLowestValue(a2).getValue().value;
                    return Double.compare(v1, v2);
                }).get();
                return new Pair<>(selectedAction, getLowestValue(selectedAction).getValue().value);
            case SMART_PESSIMISTIC:
                selectedAction = matrix.keySet().stream().max((a1, a2) -> {
                    final double v1 = getLowestExpectedValue(a1).getValue().expectedValue;
                    final double v2 = getLowestExpectedValue(a2).getValue().expectedValue;
                    return Double.compare(v1, v2);
                }).get();
                return new Pair<>(selectedAction, getLowestExpectedValue(selectedAction).getValue().expectedValue);
            case DUMB_SUICIDAL_OPTIMISTIC:
                selectedAction = matrix.keySet().stream().min((a1, a2) -> {
                    final double v1 = getHighestValue(a1).getValue().value;
                    final double v2 = getHighestValue(a2).getValue().value;
                    return Double.compare(v1, v2);
                }).get();
                return new Pair<>(selectedAction, getHighestValue(selectedAction).getValue().value);
            case SMART_SUICIDAL_OPTIMISTIC:
                selectedAction = matrix.keySet().stream().min((a1, a2) -> {
                    final double v1 = getHighestExpectedValue(a1).getValue().expectedValue;
                    final double v2 = getHighestExpectedValue(a2).getValue().expectedValue;
                    return Double.compare(v1, v2);
                }).get();
                return new Pair<>(selectedAction, getHighestExpectedValue(selectedAction).getValue().expectedValue);
            case DUMB_SUICIDAL_PESSIMISTIC:
                selectedAction = matrix.keySet().stream().min((a1, a2) -> {
                    final double v1 = getLowestValue(a1).getValue().value;
                    final double v2 = getLowestValue(a2).getValue().value;
                    return Double.compare(v1, v2);
                }).get();
                return new Pair<>(selectedAction, getLowestValue(selectedAction).getValue().value);
            case SMART_SUICIDAL_PESSIMISTIC:
                selectedAction = matrix.keySet().stream().min((a1, a2) -> {
                    final double v1 = getLowestExpectedValue(a1).getValue().expectedValue;
                    final double v2 = getLowestExpectedValue(a2).getValue().expectedValue;
                    return Double.compare(v1, v2);
                }).get();
                return new Pair<>(selectedAction, getLowestExpectedValue(selectedAction).getValue().expectedValue);
            case BEST_OPTION:
                return getBestOption();
            case WORST_OPTION:
                return getWorstOption();
            default:
                throw new IllegalStateException("Unknown strategy: " + strategy);
        }
    }

}
