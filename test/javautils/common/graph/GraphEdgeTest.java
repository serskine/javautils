package javautils.common.graph;

import javautils.common.CollectionTest;
import javautils.common.SetTest;
import org.junit.jupiter.api.BeforeEach;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GraphEdgeTest extends SetTest<Edge<String>> {

    @Override
    protected Collection<Edge<String>> createCollection() {
        final Graph<String> graph = new GraphImpl<>();
        graph.addVertex("A");
        graph.addVertex("B");
        graph.addVertex("C");
        return graph.getEditableEdgeSet();
    }

    @Override
    public Edge<String> createValueA() {
        return new EdgeImpl<>("A", "B");
    }

    @Override
    public Edge<String> createValueB() {
        return new EdgeImpl<>("A", "C");
    }

    @Override
    public Edge<String> createValueC() {
        return new EdgeImpl<>("B", "A");
    }
}
