package javautils.common.graph;

import java.util.*;
import java.util.stream.Collectors;

public class GraphImpl<Vertex> implements Graph<Vertex> {
    private final Map<Vertex, Set<Vertex>> childMap;
    private final Map<Vertex, Set<Vertex>> parentMap;
    private final Set<Vertex> allVertices;
    private int numEdges = 0;

    public GraphImpl() {
        childMap = createMap();
        parentMap = createMap();
        allVertices = createSet();
    }

    protected Map<Vertex, Set<Vertex>> createMap() {
        return new LinkedHashMap<>();
    }

    protected Set<Vertex> createSet() {
        return new LinkedHashSet<>();
    }

    @Override
    public boolean containsVertex(Vertex v) {
        return allVertices.contains(v);
    }

    @Override
    public boolean addVertex(Vertex vertex) {
        if (allVertices.add(vertex)) {
            childMap.put(vertex, new HashSet<>());
            parentMap.put(vertex, new HashSet<>());
            allVertices.add(vertex);
            return true;
        }
        return false;
    }

    @Override
    public boolean removeVertex(Vertex vertex) {
        boolean changed = isolate(vertex);
        changed = allVertices.remove(vertex) || changed;
        if (changed) {
            childMap.remove(vertex);
            parentMap.remove(vertex);
        }
        return changed;
    }

    @Override
    public List<Edge<Vertex>> getEdgesFrom(Vertex source) {
        return childMap.get(source).stream()
            .map(target -> new EdgeImpl<>(source, target))
            .collect(Collectors.toList());
    }

    @Override
    public List<Edge<Vertex>> getEdgesTo(Vertex target) {
        return childMap.get(target).stream()
            .map(source -> new EdgeImpl<>(source, target))
            .collect(Collectors.toList());
    }

    @Override
    public Set<Vertex> getVertices() {
        return Collections.unmodifiableSet(allVertices);
    }

    @Override
    public boolean clearAllVertex() {
        boolean changed = !allVertices.isEmpty();
        childMap.clear();
        parentMap.clear();
        allVertices.clear();
        return changed;
    }

    @Override
    public boolean isolateAllVertex() {
        boolean changed = false;
        for (Vertex v : allVertices) {
            final Set<Vertex> children = childMap.get(v);
            final Set<Vertex> parents = parentMap.get(v);

            // We are going to empty out all links anyways
            // If one map has it, so does the other.
            changed = changed || !children.isEmpty();
            children.clear();
            parents.clear();
        }
        numEdges = 0;
        return changed;
    }

    @Override
    public boolean containsLink(Edge<Vertex> edge) {
        return parentMap.getOrDefault(edge.getSource(), new HashSet<>()).contains(edge.getTarget());
    }

    @Override
    public int numVertex() {
        return allVertices.size();
    }

    @Override
    public int numEdges() {
        return numEdges;    // parentMap and childMap must be the same size.
    }

    @Override
    public boolean addEdge(Edge<Vertex> edge) {
        final Vertex from = edge.getSource();
        final Vertex to = edge.getTarget();
        boolean changed = addVertex(from);
        changed = changed | addVertex(to);
        changed = changed | parentMap.get(from).add(to);
        changed = changed | childMap.get(to).add(from);
        if (changed) {
            numEdges++;
        }
        return changed;
    }

    @Override
    public boolean removeEdge(Edge<Vertex> edge) {
        boolean changed = childMap.getOrDefault(edge.getSource(), new HashSet<>()).remove(edge.getTarget());
        changed = (changed) | parentMap.getOrDefault(edge.getTarget(), new HashSet<>()).remove(edge.getSource());
        if (changed) {
            numEdges--;
        }
        return changed;
    }

    private boolean clearFromOther(
            final Vertex source,
            final Map<Vertex, Set<Vertex>> sourceMap,
            final Map<Vertex, Set<Vertex>> targetMap
    ) {
        if (allVertices.contains(source)) {
            final Set<Vertex> targets = sourceMap.get(source);
            if (!targets.isEmpty()) {
                targets.forEach(target -> targetMap.get(target).remove(source));
                numEdges -= targets.size();
                targets.clear();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean clearLinksFrom(Vertex source) {
        return clearFromOther(source, childMap, parentMap);
    }

    @Override
    public boolean clearLinksTo(Vertex to) {
        return clearFromOther(to, parentMap, childMap);
    }

    @Override
    public Set<Edge<Vertex>> getAllEdges() {
        final Set<Edge<Vertex>> edges = new LinkedHashSet<>();
        for(Vertex vertex : allVertices) {
            edges.addAll(getEdgesFrom(vertex));
        }
        return edges;
    }

}
