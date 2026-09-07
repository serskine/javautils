package javautils.fuzzy;

import javautils.Logger;
import javautils.rules.Trigger;
import javautils.rules.TriggerImpl;
import org.junit.Test;

import static javautils.rules.TriggerImpl.when;
import static javautils.fuzzy.Fuzzy.or;

public class FuzzyTest {

    @Test
    public void testFuzzy() {
        final Fuzzy a = new Fuzzy(0.5);
        final Fuzzy b = new Fuzzy(0.5);

        final Trigger whenAorB = when(or(a, b))
            .then(() -> Logger.info("Fuzzy whenAorB is true"))
            .otherwise(() -> Logger.info("Fuzzy whenAorB is false"));

        whenAorB.get();
    }
}
