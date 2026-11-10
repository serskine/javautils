package javautils.common.graph;

import java.util.*;

@SuppressWarnings({"UnusedReturnValue", "unchecked"})
public interface Graph<Vertex> {
    boolean containsVertex(Vertex v);

    List<Edge<Vertex>> getEdgesFrom(Vertex source);
    List<Edge<Vertex>> getEdgesTo(Vertex target);

    boolean containsLink(final Edge<Vertex> edge);
    default boolean containsLink(final Vertex source, final Vertex target) {
        return containsLink(new EdgeImpl<>(source, target));
    }

    default boolean contains(Edge<Vertex> edge) {
        return containsLink(edge);
    }
    default boolean isLinked(Vertex from, Vertex to) {
        return contains(new EdgeImpl<>(from, to));
    }
    default boolean isLinkedBothWays(Vertex from, Vertex to) {
        return isLinked(from, to) && isLinked(to, from);
    }

}
