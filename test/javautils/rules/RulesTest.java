package javautils.rules;

import javautils.Logger;
import javautils.fuzzy.Fuzzy;
import org.junit.Test;

import static javautils.fuzzy.Fuzzy.and;
import static javautils.fuzzy.Fuzzy.or;
import static javautils.rules.TriggerImpl.when;

public class RulesTest {

    @Test
    public void rules() {
        final RuleSet ruleSet = new RuleSet();
        final Fuzzy a = new Fuzzy(0.5D);
        final Fuzzy b = new Fuzzy(0.5D);

        final Fuzzy aOrB = or(a, b);
        final Fuzzy aAndB = and(a, b);

        ruleSet.tests.add(a);
        ruleSet.tests.add(b);

        final Trigger testAllTriggers = when(ruleSet.testAll().getProb())
                .then(() -> Logger.info("Triggered"))
                .otherwiseDoNothing();

        final Trigger onTrigger = when(testAllTriggers)
                .then(() -> Logger.info("trigger triggered"))
                .otherwise(() -> Logger.info("trigger did not trigger"));


        onTrigger.get();


    }
}
