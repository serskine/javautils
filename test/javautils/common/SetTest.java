package javautils.common;

import javautils.Logger;
import javautils.Text;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public abstract class SetTest<T> extends CollectionTest<T> {

    protected final Set<T> getSet() {
        return (Set<T>) collection;
    }

    @Test
    public void testSetupAndCreateSet() {
        final Set<T> theSet = getSet();

        assertNotNull(theSet);
        assertTrue(theSet.isEmpty());
        assertEquals(0, theSet.size());
        assertEquals(a, a);
        assertNotEquals(a, b);
        assertNotEquals(a, c);
        assertNotEquals(b, a);
        assertEquals(b, b);
        assertNotEquals(b, c);
        assertNotEquals(c, a);
        assertNotEquals(c, b);
        assertEquals(c, c);

    }

    @Test
    public void testAdditionsAndRemovals() {
        final Set<T> theSet = getSet();

        assertFalse(theSet.contains(a));
        assertFalse(theSet.contains(b));
        assertFalse(theSet.contains(c));
        assertTrue(theSet.isEmpty());
        assertEquals(0, theSet.size());

        theSet.add(a);

        assertTrue(theSet.contains(a));
        assertFalse(theSet.contains(b));
        assertFalse(theSet.contains(c));
        assertFalse(theSet.isEmpty());
        assertEquals(1, theSet.size());

        theSet.add(b);

        assertTrue(theSet.contains(a));
        assertTrue(theSet.contains(b));
        assertFalse(theSet.contains(c));
        assertFalse(theSet.isEmpty());
        assertEquals(2, theSet.size());

        theSet.add(c);

        assertTrue(theSet.contains(a));
        assertTrue(theSet.contains(b));
        assertTrue(theSet.contains(c));
        assertFalse(theSet.isEmpty());
        assertEquals(3, theSet.size());

        theSet.add(a);
        theSet.add(b);
        theSet.add(c);

        assertTrue(theSet.contains(a));
        assertTrue(theSet.contains(b));
        assertTrue(theSet.contains(c));
        assertFalse(theSet.isEmpty());
        assertEquals(3, theSet.size());

        theSet.remove(b);

        assertTrue(theSet.contains(a));
        assertFalse(theSet.contains(b));
        assertTrue(theSet.contains(c));
        assertFalse(theSet.isEmpty());
        assertEquals(2, theSet.size());

        theSet.remove(a);

        assertFalse(theSet.contains(a));
        assertFalse(theSet.contains(b));
        assertTrue(theSet.contains(c));
        assertFalse(theSet.isEmpty());
        assertEquals(1, theSet.size());

        theSet.remove(a);
        theSet.remove(b);

        assertFalse(theSet.contains(a));
        assertFalse(theSet.contains(b));
        assertTrue(theSet.contains(c));
        assertFalse(theSet.isEmpty());
        assertEquals(1, theSet.size());

        theSet.remove(c);

        assertFalse(theSet.contains(a));
        assertFalse(theSet.contains(b));
        assertFalse(theSet.contains(c));
        assertTrue(theSet.isEmpty());
        assertEquals(0, theSet.size());
    }
}


