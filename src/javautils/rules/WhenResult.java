package javautils.rules;

public interface WhenResult extends Runnable {
    ThenResult then(Runnable thenAction);
}
