package javautils.common.graph;

import javautils.common.SetTest;
import org.junit.jupiter.api.BeforeEach;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public class GraphSetTest extends SetTest<String> {

    private Graph<String> graph;
    private String A, B, C, D, E;

    @Override
    protected Collection<String> createCollection() {
        graph = new GraphImpl<>();
        return graph.getEditableVertexSet();
    }

    @Override
    public String createValueA() {
        return "A";
    }

    @Override
    public String createValueB() {
        return "B";
    }

    @Override
    public String createValueC() {
        return "C";
    }
}
