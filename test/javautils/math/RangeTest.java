package javautils.math;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RangeTest {

    @Test
    public void containsTest_minAndMaxBounds() {
        Range range = new Range(1D, 5D);
        assertTrue(range.contains(3D));
        assertFalse(range.contains(0D));
        assertFalse(range.contains(6D));
    }

    @Test
    public void containsTest_infiniteRange() {
        Range range = new Range(null, null);
        assertTrue(range.contains(3D));
        assertTrue(range.contains(03D));
        assertTrue(range.contains(0D));
        assertTrue(range.contains(-0D));
        assertTrue(range.contains(Double.NEGATIVE_INFINITY));
        assertTrue(range.contains(Double.POSITIVE_INFINITY));
    }

    @Test
    public void containsMinNoMax() {
        Range range = new Range(0D, null);
        assertTrue(range.contains(3D));
        assertTrue(range.contains(0D));
        assertFalse(range.contains(-1D));
        assertFalse(range.contains(-100D));
        assertFalse(range.contains(Double.NEGATIVE_INFINITY));
        assertTrue(range.contains(Double.POSITIVE_INFINITY));
    }
}
