package javautils.rules;

import javautils.fuzzy.Fuzzy;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class RuleSet {
    public final List<Supplier<Boolean>> tests = new ArrayList<>();

    public class Result {
        final Set<Supplier<Boolean>> passed = new HashSet<>();
        final Set<Supplier<Boolean>> failed = new HashSet<>();
        final Set<Supplier<Boolean>> skipped = new HashSet<>();

        public Fuzzy getProb() {
            final int testedSize = passed.size() + failed.size();
            if (testedSize < 1) {
                return new Fuzzy(false);
            } else {
                return new Fuzzy((double) passed.size() / (double) testedSize);
            }
        }
    }

    public final Result testUntilPass() {
        final Result result = new Result();
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
        final Result result = new Result();
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
        final Result result = new Result();
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
