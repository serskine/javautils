package javautils.ai.ir;

import java.util.ArrayList;
import java.util.List;

public class Word extends Token implements Comparable<Word> {
    public Word(String text) {
        super(text);
    }

    @Override
    public int compareTo(Word o) {
        return this.text.compareToIgnoreCase(o.text);
    }

    public List<Character> getCharacters() {
        final List<Character> characters = new ArrayList<>();
        for(int i=0; i<text.length(); i++) {
            characters.add(text.charAt(i));
        }
        return characters;
    }

    @Override
    public String toString() {
        return text;
    }
}
