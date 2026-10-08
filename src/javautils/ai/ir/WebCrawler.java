package javautils.ai.ir;

import javautils.factory.KeyGen;
import javautils.math.Range;
import javautils.ptree.PTree;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebCrawler {
    private KeyGen keyGen;

    private PTree<Character, Word> words = new PTree<>();
    private PTree<Word, Sentence> sentences = new PTree<>();
    private PTree<Word, Word> nextWords = new PTree<>();

    private final SentenceExtractor sentenceExtractor = new SentenceExtractor();
    private final WordExtractor wordExtractor = new WordExtractor();


    public final Optional<String> getSentence(String tokenText) {
        if (tokenText==null) {
            return Optional.empty();
        } else {
            tokenText = tokenText.trim().toLowerCase();
            if (tokenText.isBlank() || tokenText.isEmpty()) {
                return Optional.empty();
            } else {
                return Optional.of(tokenText);
            }
        }
    }

    public final Optional<String> getWord(String tokenText) {
        final Word word = wordExtractor.getParser().parse(tokenText);
        if (word==null) {
            return Optional.empty();
        } else {
            return Optional.ofNullable(wordExtractor.getParser().describe(word));
        }
    }

    public List<Sentence> getSentences(final String text) {
        final Map<Range, Sentence> map = sentenceExtractor.extractAll(text);
        return map.values().stream().toList();
    }

    public List<Word> getWords(final String text) {
        final Map<Range, Word> map = wordExtractor.extractAll(text);
        return map.values().stream().toList();
    }

    public PredictionTrees getPredictionTrees(final DocumentId docId, final String content) {
        final PredictionTrees predictionTrees = new PredictionTrees();
        final List<Sentence> sentences = getSentences(content);

        for(Sentence sentence : sentences) {
            for(Word word : sentence) {
                final List<Character> characters = word.getCharacters();
                predictionTrees.characterToWord.put(characters, word);
            }
            predictionTrees.wordToSentence.put(sentence, sentence);
        }
        predictionTrees.sentenceToDocument.put(sentences, docId);
        return predictionTrees;
    }

}
