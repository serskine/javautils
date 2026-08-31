package javautils.ptree;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static javautils.ptree.PTreeStringTest.describeMap;
import static javautils.ptree.PTreeStringTest.describeSet;
import static org.junit.Assert.assertEquals;

public class PTreeTest {


    @Test
    public void getSuggestions_planned() {
        final PTree<Character, Integer> ptree = new PTree<>();

        ptree.put(asKey("stuart"), 1);
        ptree.put(asKey("stuart erskine"), 2);
        ptree.put(asKey("stuart marr erskine"), 3);
        ptree.put(asKey("wesley"), 4);
        ptree.put(asKey("wesley erskine"), 5);
        ptree.put(asKey("wesley kenneth erskine"), 6);

        final Set<Integer> observed = ptree.getSuggestions(asKey("stuart"));
        System.out.println(describeSet("observed", observed));

        assertEquals(3, observed.size());


    }

    Iterable<Character> asKey(final String string) {
        final List<Character> key = new ArrayList<>();
        for (char c : string.toCharArray()) {
            key.add(c);
        }
        return key;
    }
}
