package javautils.fuzzy;

import javautils.Logger;
import javautils.rules.Trigger;
import org.junit.Test;

import static javautils.rules.TriggerImpl.when;
import static javautils.fuzzy.Probability.or;

public class ProbabilityTest {

    @Test
    public void testFuzzy() {
        final Probability a = new Probability(0.5);
        final Probability b = new Probability(0.5);

        final Trigger whenAorB = when(or(a, b))
            .then(() -> Logger.info("Fuzzy whenAorB is true"))
            .otherwise(() -> Logger.info("Fuzzy whenAorB is false"));

        whenAorB.get();
    }
}
