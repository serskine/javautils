package javautils.ai.ir;

import javautils.extractor.Extractor;
import javautils.math.Range;
import javautils.parser.Parser;

import java.util.LinkedList;
import java.util.List;

public class SentenceExtractor implements Extractor<Sentence> {
    @Override
    public Parser<Sentence> getParser() {
        return new SentenceParser();
    }

    @Override
    public List<Range> findAll(String text) {
        List<Range> ranges = new LinkedList<>();

        if (text == null || text.isBlank()) {
            return ranges;
        }

        int start = 0;
        int i = 0;

        while (i < text.length()) {
            char ch = text.charAt(i);

            if (ch == '.' || ch == '!' || ch == '?') {
                // Include the delimiter in the sentence
                int end = i + 1;

                // Skip extra whitespace after punctuation
                int next = end;
                while (next < text.length() && Character.isWhitespace(text.charAt(next))) {
                    next++;
                }

                // Add the sentence range
                if (start < end) {
                    ranges.add(new Range(start, end));
                }

                // Move to next sentence
                start = next;
                i = next;
            } else {
                i++;
            }
        }

        // Handle trailing text that does not end with a sentence delimiter
        if (start < text.length()) {
            ranges.add(new Range(start, text.length()));
        }

        return ranges;
    }

}
