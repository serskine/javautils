package javautils.ai.ir;

import javautils.parser.Parser;

public class WordParser implements Parser<Word> {
    @Override
    public Word parse(String input) {
        if (input==null) {
            return null;
        } else {
            final String token = input.trim().toLowerCase();
            if (token.isBlank()) {
                return null;
            } else {
                return new Word(token);
            }
        }
    }

    @Override
    public String describe(Word element) {
        return element.text;
    }
}
