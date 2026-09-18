package javautils.fuzzy;

import javautils.Logger;
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

    }

    static void logFuzzy(final String name, final Fuzzy<String> fuzzy) {
        Logger.info("===== " + name + " =====\n" + fuzzy.describe());
    }
}
