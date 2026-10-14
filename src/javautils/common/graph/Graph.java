package javautils.common.graph;

import java.util.*;

@SuppressWarnings({"UnusedReturnValue", "unchecked"})
public interface Graph<Vertex> {

    boolean containsVertex(Vertex v);
    boolean addVertex(Vertex vertex);
    boolean removeVertex(Vertex vertex);

    List<Edge<Vertex>> getEdgesFrom(Vertex source);
    List<Edge<Vertex>> getEdgesTo(Vertex target);

    Set<Vertex> getVertices();
    boolean clearAllVertex();
    boolean isolateAllVertex();

    boolean containsLink(final Edge<Vertex> edge);
    default boolean containsLink(final Vertex source, final Vertex target) {
        return containsLink(new EdgeImpl<>(source, target));
    }

    default boolean link(Vertex from, Vertex to) {
        return addEdge(new EdgeImpl<>(from, to));
    }

    default boolean unlink(Vertex from, Vertex to) {
        return removeEdge(new EdgeImpl<>(from, to));
    }

    int numVertex();
    int numEdges();

    default boolean contains(Edge<Vertex> edge) {
        return containsLink(edge);
    }

    boolean addEdge(Edge<Vertex> edge);
    boolean removeEdge(Edge<Vertex> edge);

    default boolean isLinked(Vertex from, Vertex to) {
        return contains(new EdgeImpl<>(from, to));
    }
    default boolean isLinkedBothWays(Vertex from, Vertex to) {
        return isLinked(from, to) && isLinked(to, from);
    }

    default boolean linkBothWays(Vertex from, Vertex to) {
        boolean changed = link(from, to);
        changed = changed | link(to, from);
        return changed;
    }

    default boolean unlinkBothWays(Vertex from, Vertex to) {
        boolean changed = unlink(from, to);
        changed = changed | unlink(to, from);
        return changed;
    }

    boolean clearLinksFrom(Vertex from);
    boolean clearLinksTo(Vertex to);

    default boolean isolate(Vertex vertex) {
        boolean changed = clearLinksFrom(vertex);
        changed = changed | clearLinksTo(vertex);
        return changed;
    }

    Set<Edge<Vertex>> getAllEdges();

    default Set<Vertex> getEditableVertexSet() {
        return new Set<>() {
            @Override
            public int size() {
                return numVertex();
            }

            @Override
            public boolean isEmpty() {
                return numVertex() == 0;
            }

            @Override
            public boolean contains(Object o) {
                try {
                    return containsVertex((Vertex) o);
                } catch (ClassCastException e) {
                    return false;
                }
            }

            @Override
            public Iterator<Vertex> iterator() {
                return getVertices().iterator();
            }

            @Override
            public Object[] toArray() {
                return getVertices().toArray();
            }

            @Override
            public <T> T[] toArray(T[] ts) {
                return getVertices().toArray(ts);
            }

            @Override
            public boolean add(Vertex vertex) {
                return addVertex(vertex);
            }

            @Override
            public boolean remove(Object o) {
                try {
                    return removeVertex((Vertex) o);
                } catch (ClassCastException e) {
                    return false;
                }
            }

            @Override
            public boolean containsAll(Collection<?> collection) {
                return getVertices().containsAll(collection);
            }

            @Override
            public boolean addAll(Collection<? extends Vertex> collection) {
                boolean changed = false;
                for (Vertex v : collection) {
                    changed = changed | addVertex(v);
                }
                return changed;
            }

            @Override
            public boolean retainAll(Collection<?> collection) {
                final Set<Object> toRemove = new HashSet<>(getVertices());
                toRemove.removeAll(collection);
                return removeAll(toRemove);
            }

            @Override
            public boolean removeAll(Collection<?> collection) {
                boolean changed = false;
                for (Object o : collection) {
                    try {
                        changed = changed | removeVertex((Vertex) o);
                    } catch (ClassCastException e) {
                        // Ignore. Try the rest.
                    }
                }
                return changed;
            }

            @Override
            public void clear() {
                clearAllVertex();
            }
        };
    }
    default Set<Edge<Vertex>> getEditableEdgeSet() {
        return new Set<>() {

            @Override
            public int size() {
                return numEdges();
            }

            @Override
            public boolean isEmpty() {
                return numEdges() == 0;
            }

            @Override
            public boolean contains(Object o) {
                try {
                    return containsLink((Edge<Vertex>) o);
                } catch (ClassCastException e) {
                    return false;
                }
            }

            @Override
            public Iterator<Edge<Vertex>> iterator() {
                return getAllEdges().iterator();    // May change
            }

            @Override
            public Object[] toArray() {
                return getAllEdges().toArray();
            }

            @Override
            public <T> T[] toArray(T[] ts) {
                return getAllEdges().toArray(ts);
            }

            @Override
            public boolean add(Edge<Vertex> vertexEdge) {
                return addEdge(vertexEdge);
            }

            @Override
            public boolean remove(Object o) {
                try {
                    return removeEdge((Edge<Vertex>) o);
                } catch (ClassCastException e) {
                    return false;
                }
            }

            @Override
            public boolean containsAll(Collection<?> collection) {
                for (Object o : collection) {
                    if (!contains(o)) {
                        return false;
                    }
                }
                return true;
            }

            @Override
            public boolean addAll(Collection<? extends Edge<Vertex>> collection) {
                boolean changed = true;
                for (Edge<Vertex> edge : collection) {
                    changed = changed & addEdge(edge);
                }
                return changed;
            }

            @Override
            public boolean retainAll(Collection<?> collection) {
                final List<Object> toRemove = new ArrayList<>(getAllEdges());
                toRemove.removeAll(collection);
                return removeAll(toRemove);
            }

            @Override
            public boolean removeAll(Collection<?> collection) {
                boolean changed = false;
                for (Object o : collection) {
                    try {
                        changed = changed | removeEdge((Edge<Vertex>) o);
                    } catch (ClassCastException e) {
                        // Ignore for all
                    }
                }
                return changed;
            }

            @Override
            public void clear() {
                isolateAllVertex();
            }
        };
    }
}
