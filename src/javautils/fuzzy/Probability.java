package javautils.fuzzy;

import java.util.function.Supplier;

public class Probability implements Supplier<Boolean> {
    private double value;

    @Override
    public Boolean get() {
        return (Math.random() < value);
    }

    public Probability(double probability) {
        this.value = Math.max(0D, Math.min(1D, probability));
    }

    public Probability(final boolean isTrue) {
        this.value = isTrue ? 1.0D : 0.0D;
    }

    public static Probability always()  {   return new Probability(1.0D);            }
    public static Probability never()   {   return new Probability(0.0D);            }
    public static Probability random()  {    return new Probability(Math.random());  }

    public static Probability and(Probability a, Probability b)       {   return new Probability(Math.min(a.value, b.value));   }
    public static Probability or(Probability a, Probability b)        {   return new Probability(Math.max(a.value, b.value));   }
    public static Probability not(Probability a)                      {   return new Probability(1.0D - a.value);           }
    public static Probability xor(Probability a, Probability b)       {   return and(or(a,b), not(and(a,b)));                         }
    public static Probability implies(Probability a, Probability b)   {   return or(not(a), b);                                       }

    @Override
    public String toString() {
        return String.format("%3.2f%%", value * 100);
    }

    public final double getValue() {
        return this.value;
    }

    public final void setValue(final double value) {
        this.value = value;
    }

}

