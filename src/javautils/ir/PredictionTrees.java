package javautils.ir;

import javautils.ptree.PTree;

public class PredictionTrees {
    public final PTree<Character, Word> characterToWord = new PTree<>();
    public final PTree<Word, Sentence> wordToSentence = new PTree<>();
    public final PTree<Sentence, DocumentId> sentenceToDocument = new PTree<>();
}
