package javautils.common.graph;

import java.util.List;
import java.util.Set;

public interface AdvancedGraph<Vertex, Value> {

    List<AdvancedEdge<Vertex, Value>> getEdgesFrom(Vertex vertex);
    List<AdvancedEdge<Vertex, Value>> getEdgesTo(Vertex vertex);

    Graph<Vertex> getVertexGraph();

}
