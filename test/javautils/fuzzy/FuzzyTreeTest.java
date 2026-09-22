package javautils.fuzzy;

import javautils.Logger;
import javautils.Text;
import javautils.ai.ir.Word;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class FuzzyTreeTest {

    private FuzzyTree<Character, Integer> fuzzyTree;

    @Before
    public void onSetup() {
        fuzzyTree = new FuzzyTree<>();
    }

    @Test
    public void getFuzzySuggestions() {

        suggest("stuart", 1);
        suggest("stuart erskine", 2);
        suggest("stuart marr erskine", 3);
        suggest("wesley", 4);
        suggest("wesley erskine", 5);
        suggest("wesley kenneth erskine", 6);

        final Set<Fuzzy<Integer>> observed = fuzzyTree.getFuzzySuggestions(new Word("stuart"));

        Logger.info("observed: " + Text.describeIterable(observed, f -> f.describe()));

        assertEquals(3, observed.size());
    }

    @Test
    public void getAverageFuzzy() {
        suggest("stuart", 1);
        suggest("stuart erskine", 2);
        suggest("stuart marr erskine", 3);
        suggest("wesley", 4);
        suggest("wesley erskine", 5);
        suggest("wesley kenneth erskine", 6);

        showAverageFuzzy("stuart");
        showAverageFuzzy("wesley");
        showAverageFuzzy("wesley kenneth erskine");

    }

    private Fuzzy<Integer> showAverageFuzzy(final String text) {
        final Word word = new Word(text);
        final Fuzzy<Integer> observed = fuzzyTree.getAverageFuzzy(word);
        Logger.info("=== Word(\"" + word + "\") ===\n" + observed.describe());
        return observed;
    }


    private void suggest(String word, Integer match) {
        suggest(word, match, 1D);
    }


    private void suggest(String word, Integer match, double prob) {
        final Word wordToken = new Word(word);
        final Probability probability = new Probability(prob);
        fuzzyTree.suggest(wordToken, match, probability);
    }



}
