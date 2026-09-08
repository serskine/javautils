package javautils.fuzzy;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Fuzzy implements Supplier<Boolean> {
    private double probability;

    @Override
    public Boolean get() {
        return (Math.random() < probability);
    }

    public Fuzzy(double probability) {
        this.probability = Math.max(0D, Math.min(1D, probability));
    }

    public Fuzzy(final boolean isTrue) {
        this.probability = isTrue ? 1.0D : 0.0D;
    }

    public static Fuzzy TRUE()  {   return new Fuzzy(1.0D);   }
    public static Fuzzy FALSE() {   return new Fuzzy(0.0D);   }

    public static Fuzzy and(Fuzzy a, Fuzzy b)       {   return new Fuzzy(Math.min(a.probability, b.probability));   }
    public static Fuzzy or(Fuzzy a, Fuzzy b)        {   return new Fuzzy(Math.max(a.probability, b.probability));   }
    public static Fuzzy not(Fuzzy a)                {   return new Fuzzy(1.0D - a.probability);           }
    public static Fuzzy xor(Fuzzy a, Fuzzy b)       {   return and(or(a,b), not(and(a,b)));                         }
    public static Fuzzy implies(Fuzzy a, Fuzzy b)   {   return or(not(a), b);                                       }

    @Override
    public String toString() {
        return String.format("%3.2f%%", probability * 100);
    }

    public final double getProbability() {
        return this.probability;
    }

    public final void setProbability(final double probability) {
        this.probability = probability;
    }

}

