package javautils.fuzzy;

import javautils.Logger;
import javautils.common.Histogram;
import org.junit.Test;

import static javautils.fuzzy.Fuzzy.*;
import static org.junit.Assert.assertEquals;

public class FuzzyTest {
    public static String STUART = "STUART";
    public static String WESLEY = "WESLEY";
    public static String PATRICIA = "PATRICIA";
    public static String KENNETH = "KENNETH";
    public static String LOUISE = "LOUISE";



    @Test
    public void testFuzzy() {
        final Fuzzy<String> maleSex = new Fuzzy<>();
        final Fuzzy<String> femaleSex = new Fuzzy<>();
        final Fuzzy<String> lucky = new Fuzzy<>();

        maleSex.put(STUART, Probability.always());
        maleSex.put(WESLEY, Probability.always());
        maleSex.put(PATRICIA, Probability.never());
        maleSex.put(KENNETH, Probability.always());
        maleSex.put(LOUISE, Probability.never());

        logFuzzy("maleSex", maleSex);

        femaleSex.put(STUART, Probability.never());
        femaleSex.put(WESLEY, Probability.never());
        femaleSex.put(PATRICIA, Probability.always());
        femaleSex.put(KENNETH, Probability.never());
        femaleSex.put(LOUISE, Probability.always());

        logFuzzy("femaleSex", femaleSex);

        lucky.put(STUART, new Probability(0.5));
        lucky.put(WESLEY, new Probability(0.4));
        lucky.put(PATRICIA, new Probability(0.3));
        lucky.put(KENNETH, new Probability(0.2));
        lucky.put(LOUISE, new Probability(0.1));

        logFuzzy("lucky", lucky);

        final Fuzzy<String> notLucky = not(lucky);

        logFuzzy("notLucky", notLucky);

        final Fuzzy<String> maleAndFemale = and(maleSex, femaleSex);

        logFuzzy("maleAndFemale", maleAndFemale);

        for(String key : maleAndFemale.keySet()) {
            assertEquals(0D, maleAndFemale.get(key).getValue(), 0D);
        }

        final Fuzzy<String> luckyOrNotLucky = or(lucky, notLucky);

        logFuzzy("luckyOrNotLucky", luckyOrNotLucky);

        final Fuzzy<String> luckyAndNotLucky = and(lucky, notLucky);

        logFuzzy("luckyAndNotLucky", luckyAndNotLucky);

        final Fuzzy<String> luckyXorNotLucky = xor(lucky, notLucky);

        logFuzzy("luckyXorNotLucky", luckyXorNotLucky);

        final Fuzzy<String> luckyImpliesNotLucky = implies(lucky, notLucky);

        logFuzzy("luckyImpliesNotLucky", luckyImpliesNotLucky);

        final Fuzzy<String> notLuckyImpliesLucky = implies(notLucky, lucky);

        logFuzzy("notLuckyImpliesLucky", notLuckyImpliesLucky);

        final Fuzzy<String> averageAll = Fuzzy.average(maleSex, femaleSex, maleSex);

        logFuzzy("averageAll", averageAll);

    }

    @Test
    public void average() {
        final Fuzzy<String> f1 = new Fuzzy<>();
        final Fuzzy<String> f2 = new Fuzzy<>();
        final Fuzzy<String> f3 = new Fuzzy<>();

        f1.put("A", new Probability(1D));
        f1.put("B", new Probability(1D));
        f1.put("C", new Probability(1D));

        f2.put("A", new Probability(0D));
        f2.put("B", new Probability(0D));
        f2.put("C", new Probability(0D));

        f3.put("A", new Probability(0.25D));
        f3.put("B", new Probability(0.5D));
        f3.put("C", new Probability(0.75D));

        final Fuzzy<String> avg = Fuzzy.average(f1, f2, f3);

        assertEquals("Fuzzy A: ", 1.25D/3D, avg.get("A").getValue(), 0D);
        assertEquals("Fuzzy B: ",1.50D/3D, avg.get("B").getValue(), 0D);
        assertEquals("Fuzzy C: ",1.75D/3D, avg.get("C").getValue(), 0D);


    }

    @Test
    public void average_usingHistogram() {
        final Fuzzy<String> f1 = new Fuzzy<>();
        final Fuzzy<String> f2 = new Fuzzy<>();
        final Fuzzy<String> f3 = new Fuzzy<>();

        f1.put("A", new Probability(1D));
        f1.put("B", new Probability(0.5D));
        f1.put("C", new Probability(0D));

        f2.put("A", new Probability(0.5D));
        f2.put("B", new Probability(0.5D));
        f2.put("C", new Probability(0.5D));

        final Fuzzy<String> expected = Fuzzy.average(f1, f1, f2);

        final Histogram<Fuzzy<String>> h = new Histogram<>();
        h.put(f1, 2D);
        h.put(f2, 1D);

        Fuzzy<String> avg2 = Fuzzy.average(h);
        assertEquals("A: ", expected.get("A").getValue(), avg2.get("A").getValue(), 0D);
        assertEquals("B: ", expected.get("B").getValue(), avg2.get("B").getValue(), 0D);
        assertEquals("C: ", expected.get("C").getValue(), avg2.get("C").getValue(), 0D);

    }

    static void logFuzzy(final String name, final Fuzzy<String> fuzzy) {
        Logger.info("===== " + name + " =====\n" + fuzzy.describe());
    }
}
