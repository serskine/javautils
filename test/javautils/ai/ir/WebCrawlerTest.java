package javautils.ai.ir;

import javautils.Logger;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class WebCrawlerTest {

    private WebCrawler webCrawler;

    @Before
    public void onSetup() {
        webCrawler = new WebCrawler();
    }

    @Test
    public void getSentence() {
        final Optional<String> observed1 = webCrawler.getSentence("  Stuart Erskine was here     ");

        assertTrue(observed1.isPresent());
        assertEquals("stuart erskine was here", observed1.get());

        final Optional<String> observed2 = webCrawler.getSentence("      ");
        assertFalse(observed2.isPresent());
    }

    @Test
    public void getWord() {
        final Optional<String> observed1 = webCrawler.getWord("  Stuart Erskine was here     ");

        assertTrue(observed1.isPresent());
        assertEquals("stuart erskine was here", observed1.get());

        final Optional<String> observed2 = webCrawler.getWord("      ");
        assertFalse(observed2.isPresent());
    }

    @Test
    public void getSentences() {
        final List<Sentence> observed = webCrawler.getSentences("the quick brown fox jumped over the lazy dogs. Stuart Marr Erskine was here. Hello world! Do you want some coffee?");
        assertEquals(4, observed.size());
        assertEquals("the", observed.get(0).get(0).text);
        assertEquals("quick", observed.get(0).get(1).text);
        assertEquals("brown", observed.get(0).get(2).text);
        assertEquals("fox", observed.get(0).get(3).text);
        assertEquals("jumped", observed.get(0).get(4).text);
        assertEquals("over", observed.get(0).get(5).text);
        assertEquals("the", observed.get(0).get(6).text);
        assertEquals("lazy", observed.get(0).get(7).text);
        assertEquals("dogs", observed.get(0).get(8).text);

        assertEquals("stuart", observed.get(1).get(0).text);
        assertEquals("marr", observed.get(1).get(1).text);
        assertEquals("erskine", observed.get(1).get(2).text);
        assertEquals("was", observed.get(1).get(3).text);
        assertEquals("here", observed.get(1).get(4).text);

        assertEquals("hello", observed.get(2).get(0).text);
        assertEquals("world", observed.get(2).get(1).text);

        assertEquals("do", observed.get(3).get(0).text);
        assertEquals("you", observed.get(3).get(1).text);
        assertEquals("want", observed.get(3).get(2).text);
        assertEquals("some", observed.get(3).get(3).text);
        assertEquals("coffee", observed.get(3).get(4).text);

    }

    @Test
    public void getWords() {
        final List<Word> observed = webCrawler.getWords("the quick brown fox jumped over the lazy dogs. Stuart Marr Erskine was here. Hello world! Do you want some coffee?");
        assertEquals(21, observed.size());
    }

    @Test
    public void getPredictionTrees() {
        final DocumentId documentId = new DocumentId("./");
        final String content = "the quick brown fox jumped over the lazy dogs. Stuart Marr Erskine was here. Hello world! Do you want some coffee?";

        final PredictionTrees predictionTrees = webCrawler.getPredictionTrees(documentId, content);

        final Set<Sentence> sentenceSuggestions = predictionTrees.wordToSentence.getSuggestions(Arrays.asList(new Word("do"), new Word("you")));
        for(Sentence sentence : sentenceSuggestions) {
            Logger.info(sentence.toString());
        }
        assertEquals(1, sentenceSuggestions.size());


        final Set<String> wordSuggestions = predictionTrees.characterToWord.getSuggestions(Arrays.asList('w', 'a')).stream().map(w -> w.text).collect(Collectors.toSet());
        for(String word : wordSuggestions) {
            Logger.info(word.toString());
        }

        assertEquals(2, wordSuggestions.size());
        assertTrue(wordSuggestions.contains("want"));
        assertTrue(wordSuggestions.contains("was"));
        assertFalse(wordSuggestions.contains("world"));

        final Sentence s0 = new Sentence("the", "quick", "brown", "fox", "jumped", "over", "the", "lazy", "dogs");
        final Sentence s1 = new Sentence("stuart", "marr", "erskine", "was", "here");
        final Sentence s2 = new Sentence("hello", "world");
        final Set<DocumentId> documentIds = predictionTrees.sentenceToDocument.getSuggestions(Arrays.asList(s0, s1, s2));
        assertEquals(1, documentIds.size());
        assertTrue(documentIds.contains(documentId));
    }


}

