package javautils.ai.ir;

import javautils.extractor.Extractor;
import javautils.math.Range;
import javautils.parser.Parser;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WordExtractor implements Extractor<Word> {
    @Override
    public Parser<Word> getParser() {
        return new WordParser();
    }

    @Override
    public List<Range> findAll(String text) {
        final Pattern wordPattern = Pattern.compile("\\b[\\p{L}\\p{N}']+\\b");
        final List<Range> ranges = new LinkedList<>();
        final Matcher matcher = wordPattern.matcher(text);

        while(matcher.find()) {
            ranges.add(new Range(matcher.start(), matcher.end()));
        }
        return ranges;
    }

}
