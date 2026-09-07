package javautils.rules;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class TriggerImpl implements Trigger {
    protected Runnable whenTrueAction;
    protected Runnable whenFalseAction;
    private Supplier<Boolean> observable;
    protected final Set<Consumer<Boolean>> observers = new HashSet<>();


    protected TriggerImpl(final Supplier<Boolean> conditionCheck) {
        this.whenFalseAction = null;
        this.whenTrueAction = null;
        this.observable = conditionCheck;
        if (observable instanceof TriggerImpl) {
            final TriggerImpl triggerImpl = (TriggerImpl) observable;
            triggerImpl.observers.add(this);
        }
    }

    @Override
    public void accept(Boolean aBoolean) {
        if (aBoolean) {
            if (whenTrueAction != null) {
                whenTrueAction.run();
            }
        } else {
            if (whenFalseAction != null) {
                whenFalseAction.run();
            }
        }
        notifyObservers(aBoolean);
    }

    @Override
    public Boolean get() {
        final boolean result = observable.get();
        accept(result);
        return result;
    }

    @Override
    public Set<Consumer<Boolean>> getObservers() {
        return this.observers;
    }

    public static class ThenResult {
        private final TriggerImpl triggerImpl;

        public ThenResult(TriggerImpl triggerImpl) {
            this.triggerImpl = triggerImpl;
        }

        public TriggerImpl otherwise(Runnable otherwiseAction) {
            triggerImpl.whenFalseAction = otherwiseAction;
            return triggerImpl;
        }

        public TriggerImpl otherwiseDoNothing() {
            return otherwise(() -> {});
        }
    }

    public static class WhenResult {
        private final TriggerImpl triggerImpl;

        protected WhenResult(final TriggerImpl triggerImpl) {
            this.triggerImpl = triggerImpl;
        }

        public ThenResult then(Runnable thenAction) {
            triggerImpl.whenTrueAction = thenAction;
            return new ThenResult(triggerImpl);
        }
    }

    public static WhenResult when(final Supplier<Boolean> conditionCheck) {
        final TriggerImpl triggerImpl = new TriggerImpl(conditionCheck);
        return new WhenResult(triggerImpl);
    }



}
