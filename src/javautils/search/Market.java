package javautils.search;

import java.util.Optional;

public class Market<Key> extends Histogram<Key> {

    public double getProb(Key key) {
        final double sumWeight = getSumWeight();
        if (sumWeight == 0.0) {
            return 0.0;
        } else {
            return get(key) / sumWeight;
        }
    }

    public Key chooseRandomByProbability() { return getKeyForProbability(Math.random()); }
    public Key getKeyForProbability(final double probabilitySeed) {
        double sumProb = 0D;
        Key choice = null;
        for(Key key : keySet()) {
            choice = key;
            sumProb += getProb(key);
            if (probabilitySeed <= sumProb) {
                break;
            }
        }
        return Optional.of(choice).orElseThrow(() -> new IllegalStateException("Market is empty"));
    }

}
