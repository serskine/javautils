package javautils.common.graph;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class GraphTest {
    private Graph<String> graph;

    private String A = "A";
    private String B = "B";
    private String C = "C";
    private String D = "D";

    @BeforeEach
    public void onSetup() {
        graph = new GraphImpl<>();
    }

    @AfterEach
    public void onTearDown() {
        graph = null;
    }

    @Test
    public void testSetup() {
        assertNotNull(graph);
        assertEquals(0, graph.numVertex());
        assertEquals(0, graph.numEdges());
    }

    @Test
    public void linkBothWays() {
        assertFalse(graph.isLinked(A, B));
        assertFalse(graph.isLinked(B, A));

        graph.linkBothWays(A, B);

        assertTrue(graph.isLinkedBothWays(A, B));
    }

    @Test
    public void testlinking() {
        assertTrue(graph.link(A, B));
        assertTrue(graph.containsLink(A, B));
        assertFalse(graph.containsLink(B, A));

        assertFalse(graph.link(A, B));

        assertTrue(graph.linkBothWays(A, B));
        assertTrue(graph.containsLink(A, B));
        assertTrue(graph.containsLink(B, A));

        assertFalse(graph.linkBothWays(A, B));
    }

    @Test
    public void testUnlinkingAndClear() {
        graph.linkBothWays(A, A);
        graph.linkBothWays(A, B);
        graph.linkBothWays(A, C);

        graph.linkBothWays(B, A);
        graph.linkBothWays(B, B);
        graph.linkBothWays(B, C);

        graph.linkBothWays(C, A);
        graph.linkBothWays(C, B);
        graph.linkBothWays(C, C);

        assertTrue(graph.isLinkedBothWays(A, A));
        assertTrue(graph.isLinkedBothWays(A, B));
        assertTrue(graph.isLinkedBothWays(A, C));

        assertTrue(graph.isLinkedBothWays(B, A));
        assertTrue(graph.isLinkedBothWays(B, B));
        assertTrue(graph.isLinkedBothWays(B, C));

        assertTrue(graph.isLinkedBothWays(C, A));
        assertTrue(graph.isLinkedBothWays(C, B));
        assertTrue(graph.isLinkedBothWays(C, C));

        assertEquals(3, graph.numVertex());
        assertEquals(9, graph.numEdges());

        graph.unlinkBothWays(A, B);

        assertFalse(graph.isLinkedBothWays(A, B));
        assertFalse(graph.isLinkedBothWays(B, A));
        assertFalse(graph.isLinked(A, B));
        assertFalse(graph.isLinked(B, A));

        graph.isolate(B);

        assertEquals(0, graph.getEdgesFrom(B).size());
        assertTrue(graph.isLinked(A, A));
        assertFalse(graph.isLinked(A, B));
        assertTrue(graph.isLinked(A, C));
        assertFalse(graph.isLinked(B, A));
        assertFalse(graph.isLinked(B, B));
        assertFalse(graph.isLinked(B, C));
        assertTrue(graph.isLinked(C, A));
        assertFalse(graph.isLinked(C, B));
        assertTrue(graph.isLinked(C, C));
    }

    @Test
    public void getEditableVertexSet() {
        graph.linkBothWays(A, A);
        graph.linkBothWays(A, B);
        graph.linkBothWays(A, C);
        graph.linkBothWays(B, B);
        graph.linkBothWays(B, C);
        graph.linkBothWays(C, C);

        final Set<String> editableSet = graph.getEditableVertexSet();

        assertTrue(editableSet.contains(A));
        assertTrue(editableSet.contains(B));
        assertTrue(editableSet.contains(C));

        assertEquals(3, editableSet.size());
        assertEquals(3, graph.numVertex());

        editableSet.remove(A);

        assertEquals(2, editableSet.size());
        assertEquals(2, graph.numVertex());

        assertFalse(editableSet.contains(A));
        assertTrue(editableSet.contains(B));
        assertTrue(editableSet.contains(C));

        assertFalse(graph.containsVertex(A));
        assertTrue(graph.containsVertex(B));
        assertTrue(graph.containsVertex(C));

        assertFalse(graph.isLinked(A, A));
        assertFalse(graph.isLinked(A, B));
        assertFalse(graph.isLinked(A, C));
        assertFalse(graph.isLinked(B, A));
        assertTrue(graph.isLinked(B, B));
        assertTrue(graph.isLinked(B, C));
        assertFalse(graph.isLinked(C, A));
        assertTrue(graph.isLinked(C, B));
        assertTrue(graph.isLinked(C, C));

        editableSet.add(D);

        assertTrue(graph.containsVertex(D));
        assertEquals(3, graph.numVertex());
        assertEquals(3, editableSet.size());
    }
}
