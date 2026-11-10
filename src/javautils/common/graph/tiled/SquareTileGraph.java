package javautils.common.graph.tiled;

import javautils.common.graph.Edge;
import javautils.common.graph.EdgeImpl;
import javautils.common.graph.Graph;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SquareTileGraph implements Graph<Point> {
    @Override
    public boolean containsVertex(Point v) {
        return true;
    }

    @Override
    public java.util.List<Edge<Point>> getEdgesFrom(Point source) {
        final java.util.List<Edge<Point>> edges = new ArrayList<>();
        edges.add(new EdgeImpl<>(source, new Point(source.x, source.y+1)));
        edges.add(new EdgeImpl<>(source, new Point(source.x, source.y-1)));
        edges.add(new EdgeImpl<>(source, new Point(source.x+1, source.y)));
        edges.add(new EdgeImpl<>(source, new Point(source.x-1, source.y)));
        return edges;
    }

    @Override
    public java.util.List<Edge<Point>> getEdgesTo(Point target) {
        final List<Edge<Point>> edges = new ArrayList<>();
        edges.add(new EdgeImpl<>(new Point(target.x, target.y+1), target));
        edges.add(new EdgeImpl<>(new Point(target.x, target.y-1), target));
        edges.add(new EdgeImpl<>(new Point(target.x+1, target.y), target));
        edges.add(new EdgeImpl<>(new Point(target.x-1, target.y), target));
        return edges;
    }

    @Override
    public boolean containsLink(Edge<Point> edge) {
        return true;
    }
}
