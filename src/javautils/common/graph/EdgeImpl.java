package javautils.common.graph;

public class EdgeImpl<Vertex> implements Edge<Vertex> {
    private final Vertex source;
    private final Vertex target;

    public EdgeImpl(Vertex source, Vertex target) {
        this.source = source;
        this.target = target;
    }

    @Override
    public Vertex getSource() {
        return this.source;
    }

    @Override
    public Vertex getTarget() {
        return this.target;
    }
}
