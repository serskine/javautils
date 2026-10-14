package javautils.common.graph;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AdvancedGraphImpl<Vertex, EdgeValue> implements AdvancedGraph<Vertex, EdgeValue> {

    protected final Graph<Vertex> vertexGraph;
    protected final Map<Edge<Vertex>, List<EdgeValue>> edgeValueMap;

    public AdvancedGraphImpl() {
        this.vertexGraph = new GraphImpl<>();
        this.edgeValueMap = new LinkedHashMap<>();
    }

    protected final List<AdvancedEdge<Vertex, EdgeValue>> createNewAdvancedEdgeList() {
        return new ArrayList<>();
    }


    @Override
    public List<AdvancedEdge<Vertex, EdgeValue>> getEdgesFrom(Vertex source) {
        return expandEdges(vertexGraph.getEdgesFrom(source));
    }

    @Override
    public List<AdvancedEdge<Vertex, EdgeValue>> getEdgesTo(Vertex target) {
        return expandEdges(vertexGraph.getEdgesTo(target));
    }

    private List<AdvancedEdge<Vertex, EdgeValue>> expandEdges(List<Edge<Vertex>> graphEdges) {
        final List<AdvancedEdge<Vertex, EdgeValue>> edges = createNewAdvancedEdgeList();
        for (Edge<Vertex> edge : graphEdges) {
            final List<EdgeValue> edgeValues =
                    edgeValueMap.get(new EdgeImpl<>(edge.getSource(), edge.getTarget()));
            for (EdgeValue edgeValue : edgeValues) {
                edges.add(new AdvancedEdgeImpl<>(edge.getSource(), edge.getTarget(), edgeValue));
            }
        }
        return edges;
    }

    @Override
    public Graph<Vertex> getVertexGraph() {
        return this.vertexGraph;
    }

}
