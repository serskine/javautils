package javautils.ai.ir;

import javautils.Text;
import javautils.math.Range;
import javautils.parser.Parser;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SentenceParser implements Parser<Sentence> {

    private final WordExtractor wordExtractor = new WordExtractor();

    @Override
    public Sentence parse(String tokenText) {
        final List<Word> words = wordExtractor.findAll(tokenText)
            .stream()
            .map(r -> new Word(Text.substring(tokenText, r)))
            .toList();
        if (!words.isEmpty()) {
            return Sentence.create(words);
        } else {
            return null;
        }
    }

    @Override
    public String describe(Sentence element) {
        return element.toString();
    }
}
