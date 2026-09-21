package javautils.ai.ir;

import java.util.*;

public class Sentence extends ArrayList<Word> implements Comparable<Sentence> {
    public Sentence(Word... words) {
        Arrays.asList(words);
    }

    public Sentence(String... words) {
        final List<Word> tokens = new ArrayList<>();
        for(String word : words) {
            tokens.add(new Word(word));
        }
        this(tokens);
    }

    public Sentence(Iterable<Word> words) {
        clear();
        for(Word word : words) {
            add(word);
        }
    }

    @Override
    public int compareTo(Sentence o) {
        for(int i=0; i<Math.min(o.size(), size()); i++) {
            final Word myWord = get(i);
            final Word oWord = o.get(i);
            final int wordCompare = myWord.compareTo(oWord);
            if (wordCompare != 0) {
                return wordCompare;
            }
        }
        final int sizeDiff = size() - o.size();
        return sizeDiff;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        for(int i=0; i<size(); i++) {
            if (i>0) {
                sb.append(" ");
            }
            sb.append(get(i));
        }
        return sb.toString();
    }
}
