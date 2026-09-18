package javautils.ir;

import javautils.factory.KeyGen;
import javautils.ptree.PTree;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebCrawler {
    private KeyGen keyGen;

    private PTree<Character, Word> words = new PTree<>();
    private PTree<Word, Sentence> sentences = new PTree<>();
    private PTree<Word, Word> nextWords = new PTree<>();

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

    public List<Sentence> getSentences(final String text) {
        final List<Sentence> sentenceList = new LinkedList<>();
        String[] sentences = text.split("(?<=[.!?])\\s+");
        for(String token : sentences) {
            final Optional<String> sentence = getSentence(token);
            if (sentence.isPresent()) {
                final String sentenceText = sentence.get();
                final List<Word> words = getWords(sentenceText);
                if (!words.isEmpty()) {
                    final Sentence aSentence = new Sentence(words);
                    sentenceList.add(aSentence);
                }
            }
        }
        return sentenceList;
    }

    public List<Word> getWords(final String text) {
        final Pattern wordPattern = Pattern.compile("\\b[\\p{L}\\p{N}']+\\b");
        final List<Word> wordList = new LinkedList<>();

        final Matcher matcher = wordPattern.matcher(text);

        while(matcher.find()) {
            final String wordText = matcher.group();
            final Optional<String> wordOpt = getWord(wordText);
            if (wordOpt.isPresent()) {
                final Word word = new Word(wordOpt.get());
                wordList.add(word);
            }
        }

        return wordList;
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
