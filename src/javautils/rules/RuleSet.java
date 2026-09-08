package javautils.rules;

import javautils.fuzzy.Fuzzy;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class RuleSet {
    public final List<Supplier<Boolean>> tests = new ArrayList<>();
    public final Result result = new Result();

    public class Result {
        final Set<Supplier<Boolean>> passed = new HashSet<>();
        final Set<Supplier<Boolean>> failed = new HashSet<>();
        final Set<Supplier<Boolean>> skipped = new HashSet<>();
        final Fuzzy fuzzy = new Fuzzy(0D);

        public void clear() {
            passed.clear();
            failed.clear();
            skipped.clear();
        }

        public int numTested() {
            return passed.size() + failed.size();
        }

        public int numPassed() {
            return passed.size();
        }

        public int numFailed() {
            return failed.size();
        }

        public int numSkipped() {
            return skipped.size();
        }

        private void updateFuzzy() {
            if (numTested() > 0) {
                fuzzy.setProbability(numPassed() / numTested());
            } else {
                fuzzy.setProbability(0D);
            }
        }

        public Fuzzy getProb() {
            return this.fuzzy;
        }

        @Override
        public String toString() {
            return String.format("Result{size=%d, tested=%d, passed=%d, failed=%d, skipped=%d, prob=%s}",
                (passed.size() + failed.size() + skipped.size()),
                passed.size(),
                numTested(),
                failed.size(),
                skipped.size(),
                getProb()
            );
        }
    }

    public Result getResult() {
        return this.result;
    }

    public final Result testUntilPass() {
        result.clear();
        for(Supplier<Boolean> triggerImpl : tests) {
            if (!result.passed.isEmpty()) {
                result.skipped.add(triggerImpl);
            } else if (triggerImpl.get()) {
                result.passed.add(triggerImpl);
            } else {
                result.failed.add(triggerImpl);
            }
        }
        return result;
    }

    public final Result testUntilFail() {
        result.clear();
        for(Supplier<Boolean> triggerImpl : tests) {
            if (!result.failed.isEmpty()) {
                result.skipped.add(triggerImpl);
            } else if (triggerImpl.get()) {
                result.passed.add(triggerImpl);
            } else {
                result.failed.add(triggerImpl);
            }
        }

        return result;
    }

    public final Result testAll() {
        result.clear();
        for(Supplier<Boolean> triggerImpl : tests) {
            if (triggerImpl.get()) {
                result.passed.add(triggerImpl);
            } else {
                result.failed.add(triggerImpl);
            }
        }
        return result;
    }


}
