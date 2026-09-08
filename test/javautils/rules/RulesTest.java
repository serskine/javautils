package javautils.rules;

import javautils.Logger;
import javautils.fuzzy.Fuzzy;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static javautils.rules.TriggerImpl.when;
import static org.junit.Assert.assertEquals;

public class RulesTest {

    int numTrials = 100;
    int numA = 0;
    int numB = 0;
    int numRules = 0;

    @Before
    public void onSetup() {
        numTrials = 100;
        numRules = 7;
        numA = 0;
        numB = 0;
    }

    @After
    public void onTearDown() {
        Logger.info(String.format(
              "\n***\n" +
              "*** TEST RESULTS\n" +
              "***\n" +
              "numTrials      = %d\n" +
              "numAllTriggers = %d\n" +
              "numOnTriggers  = %d\n",
            numTrials,
                numA,
                numB)
        );
    }

    public void incrementA() { numA++;  }
    public void incrementB()  { numB++;   }

    @Test
    public void resultSet() {
        final RuleSet ruleSet = new RuleSet();
        final Fuzzy chanceOfPass = new Fuzzy(1);

        for(int i=0; i<numTrials; i++) {
            ruleSet.tests.add(chanceOfPass);
        }

        RuleSet.Result resultUntilPass = ruleSet.testUntilPass();
        RuleSet.Result resultUntilFail = ruleSet.testUntilFail();
        RuleSet.Result resultAll = ruleSet.testAll();

        Logger.info("fPass: " + resultUntilPass.toString());
        Logger.info("fFail: " + resultUntilFail.toString());
        Logger.info("fAll:  " + resultAll.toString());


    }

    @Test
    public void rules() {

        final RuleSet ruleSet = new RuleSet();
        for(int i=0; i<numRules; i++) {
            final Fuzzy fuzzy = new Fuzzy((i+1D) / numRules);
            ruleSet.tests.add(fuzzy);
        }

        for(int i=0; i<numTrials; i++) {

            final RuleSet.Result r = ruleSet.result;
            Logger.info(String.format("r[%d]: %s", i, r));

            final Trigger testAllTriggers = when(r.getProb())
                    .then(this::incrementA)
                    .otherwise(this::incrementA);

            final Trigger onTrigger = when(testAllTriggers)
                    .then(this::incrementB)
                    .otherwise(this::incrementB);
        }


        assertEquals(numTrials, numA);
        assertEquals(numTrials * numRules, numB);

    }
}
