package javautils;

import java.util.*;

public class Graph<Vertex, Path> {
    private Set<Vertex> allVertex = new HashSet<>();

    private Map<Vertex, Map<Vertex, Path>> childrenOfVertex = new HashMap<Vertex, Map<Vertex, Path>>();
    private Map<Vertex, Map<Vertex, Path>> parentsOfVertex = new HashMap<Vertex, Map<Vertex, Path>>();

    public final void addVertex(final Vertex v) {
        allVertex.add(v);
        childrenOfVertex.putIfAbsent(v, new HashMap<>());
        parentsOfVertex.putIfAbsent(v, new HashMap<>());
    }

    public final Set<Vertex> allVertex() {
        return Collections.unmodifiableSet(allVertex);
    }

    public final void link(Vertex source, Vertex target, Path path) {
        addVertex(source);
        addVertex(target);
        childrenOfVertex.get(source).put(target, path);
        parentsOfVertex.get(target).put(source, path);
    }

    public final void unlink(Vertex source, Vertex target) {
        childrenOfVertex.getOrDefault(source, new HashMap<>()).remove(target);
        parentsOfVertex.getOrDefault(target, new HashMap<>()).remove(source);
    }

    public final Optional<Path> getLink(final Vertex source, final Vertex target) {
        return Optional.ofNullable(childrenOfVertex.getOrDefault(source, new HashMap<>()).get(target));
    }

    public final Map<Vertex, Path> getPathFrom(final Vertex vertex) {
        return new HashMap<>(childrenOfVertex.getOrDefault(vertex, new  HashMap<>()));
    }

    public final Map<Vertex, Path> getPathsTo(final Vertex vertex) {
        return new HashMap<>(parentsOfVertex.getOrDefault(vertex, new  HashMap<>()));
    }

    public final void removePathsFrom(Vertex vertex) {
        if (allVertex.contains(vertex)) {
            final Map<Vertex, Path> pathsFrom = getPathFrom(vertex);
            for(Map.Entry<Vertex, Path> entry : pathsFrom.entrySet()) {
                unlink(vertex, entry.getKey());
            }
            parentsOfVertex.getOrDefault(vertex, new HashMap<>()).clear();
        }
    }

    public final void removePathsTo(Vertex vertex) {
        if (allVertex.contains(vertex)) {
            final Map<Vertex, Path> pathsTo = getPathsTo(vertex);
            for(Map.Entry<Vertex, Path> entry : pathsTo.entrySet()) {
                unlink(vertex, entry.getKey());
            }
            childrenOfVertex.getOrDefault(vertex, new HashMap<>()).clear();
        }
    }

    public final void isolate(final Vertex vertex) {
        if (allVertex.contains(vertex)) {
            removePathsFrom(vertex);
            removePathsTo(vertex);
        }
    }

    public final void remove(final Vertex vertex) {
        isolate(vertex);
        parentsOfVertex.remove(vertex);
        childrenOfVertex.remove(vertex);
    }

}
