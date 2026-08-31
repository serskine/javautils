package javautils.search;

public class Outcome {
    public final double prob;
    public final double value;
    public final double expectedValue;

    public Outcome() {
        this(1D, 0D);
    }

    public Outcome(final double prob, final double value) {
        this.prob = Math.max(0D, Math.min(1D, prob));
        this.value = value;
        this.expectedValue = this.prob * this.value;
    }
}
