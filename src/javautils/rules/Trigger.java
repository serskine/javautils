package javautils.rules;

import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

public interface Trigger extends Consumer<Boolean>, Supplier<Boolean> {
    default void notifyObservers(Boolean value) {
        for (Consumer<Boolean> observer : getObservers()) {
            observer.accept(value);
        }
    }

    Set<Consumer<Boolean>> getObservers();

}
