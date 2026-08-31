package javautils.ptree;

import javautils.Logger;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import static org.junit.Assert.assertEquals;

public class PTreeStringTest {

    private static char[] CHARS = new char[]{
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
        'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
        'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
        'u', 'v', 'w', 'x', 'y', 'z'
    };
    private PTreeString<Integer> ptree;
    private Map<String, Integer> map;
    private Map<Integer, String> reverseMap;

    public static final int NUM_WORDS = 1000000;

    @Before
    public void onSetup() {
        map = new HashMap<>();
        reverseMap = new HashMap<>();
    }

    public static PTreeString<Integer> createRandomPtree(final int numWords) {
        final PTreeString ptree = new PTreeString<>();

        for(int i=0; i<numWords; i++) {
            final String word = randomDigits();
            ptree.put(word, i);
        }
        return ptree;
    }


    @Test
    public void getSuggestions_random() {
        ptree = createRandomPtree(100000);

        logTime("Getting ptree suggestions.", () -> {
            final Set<Integer> suggestions = ptree.getSuggestions("7");

            System.out.println(describeSet("Num Suggestions", suggestions));
        });
    }

    @Test
    public void getSuggestions_planned() {
        ptree = new PTreeString<>();

        ptree.put("stuart", 1);
        ptree.put("stuart erskine", 2);
        ptree.put("stuart marr erskine", 3);
        ptree.put("wesley", 4);
        ptree.put("wesley erskine", 5);
        ptree.put("wesley kenneth erskine", 6);

        System.out.println(describeMap("ptree", ptree));

        final Set<Integer> observed = ptree.getSuggestions("stuart");
        System.out.println(describeSet("observed", observed));

        assertEquals(3, observed.size());


    }

    public static String describeSet(String title, Set<Integer> set) {
        final StringBuilder sb = new StringBuilder();
        sb.append("\n***\n*** " + title + "\n*** set " + set.size() + " elements\n***\n");
        set.forEach(e -> sb.append(e + "\n"));
        return sb.toString();
    }

    public static String describeMap(String title, Map<String, Integer> map) {
        final StringBuilder sb = new StringBuilder();
        sb.append("\n***\n*** " + title + "\n*** map " + map.size() + " elements\n***\n");
        map.entrySet().forEach(e -> sb.append("" + e.getKey() + "\t = " + e.getValue() + "\n"));
        return sb.toString();
    }


    public static final int randomInt(int max) {    return (int) (Math.random() * max); }

    public static final char randomChar()   {   return CHARS[randomInt(CHARS.length)];  }
    public static final char randomDigit()  {    return CHARS[randomInt(10)];      }

    public static String randomText()   { return randomText(randomInt(10), () -> randomChar());     }
    public static String randomDigits() { return randomText(randomInt(10), () -> randomDigit());    }



    public static String randomText(int len, Supplier<Character> supplier) {
        final StringBuilder sb = new StringBuilder();
        for(int i=0; i<len; i++) {
            final char c = supplier.get();
            sb.append(c);
        }
        return sb.toString();
    }

    public static void logTime(String title, Runnable r) {
        final long start  = System.currentTimeMillis();
        r.run();
        final long end = System.currentTimeMillis();
        Logger.info(title + " took " + (end-start) + " ms");
    }

}
