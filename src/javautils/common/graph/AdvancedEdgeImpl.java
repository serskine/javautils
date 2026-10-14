package javautils.common.graph;

public class AdvancedEdgeImpl<Vertex, Value> extends EdgeImpl<Vertex> implements AdvancedEdge<Vertex, Value> {

    private Value value;

    public AdvancedEdgeImpl(Vertex source, Vertex target, Value value) {
        super(source, target);
        this.value = value;
    }

    @Override
    public Value getValue() {
        return this.value;
    }
}
