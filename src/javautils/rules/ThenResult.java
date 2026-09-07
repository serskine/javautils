package javautils.rules;

public interface ThenResult extends Runnable {
    OtherwiseResult otherwise(Runnable otherwiseAction);
}
