package javautils.common.graph;

import javautils.common.SetTest;

import java.util.Collection;

public class GraphEdgeTest extends SetTest<Edge<String>> {

    @Override
    protected Collection<Edge<String>> createCollection() {
        final GraphImpl<String> graph = new GraphImpl<>();
        graph.addVertex("A");
        graph.addVertex("B");
        graph.addVertex("C");
        return graph.getAllEdges();
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
