package javautils.flags;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class HasFlagsTest {

    private Flags flags;

    @Before
    public void setUp() {
        flags = new Flags(0);
    }

    // Constructor tests
    @Test
    public void testDefaultConstructor() {
        Flags f = new Flags();
        assertFalse(f.isFlag(0));
        assertFalse(f.isFlag(31));
    }

    @Test
    public void testConstructorWithSeed() {
        Flags f = new Flags(0xABCDEF01);
        assertTrue(f.isFlag(0));
        assertFalse(f.isFlag(1));
    }

    // isFlag tests
    @Test
    public void testIsFlagTrue() {
        flags = new Flags(0xFFFFFFFF);
        for (int i = 0; i < Flags.NUM_BITS; i++) {
            assertTrue("Flag " + i + " should be true", flags.isFlag(i));
        }
    }

    @Test
    public void testIsFlagFalse() {
        flags = new Flags(0x00000000);
        for (int i = 0; i < Flags.NUM_BITS; i++) {
            assertFalse("Flag " + i + " should be false", flags.isFlag(i));
        }
    }

    @Test
    public void testIsFlagMixed() {
        flags = new Flags(0xAAAAAAAA);
        for (int i = 0; i < Flags.NUM_BITS; i++) {
            if (i % 2 == 1) {
                assertTrue("Flag " + i + " should be true", flags.isFlag(i));
            } else {
                assertFalse("Flag " + i + " should be false", flags.isFlag(i));
            }
        }
    }

    @Test
    public void testIsFlagOutOfBoundsNegative() {
        try {
            flags.isFlag(-1);
            fail("Expected IndexOutOfBoundsException for isFlag(-1)");
        } catch (IndexOutOfBoundsException e) {
            // Expected
        }
    }

    @Test
    public void testIsFlagOutOfBoundsTooHigh() {
        try {
            flags.isFlag(Flags.NUM_BITS);
            fail("Expected IndexOutOfBoundsException for isFlag(NUM_BITS)");
        } catch (IndexOutOfBoundsException e) {
            // Expected
        }
    }

    // setFlag tests
    @Test
    public void testSetFlagSingleBit() {
        flags = new Flags(0);
        flags.setFlag(5);
        assertTrue(flags.isFlag(5));
        for (int i = 0; i < Flags.NUM_BITS; i++) {
            if (i == 5) assertTrue(flags.isFlag(i));
            else assertFalse(flags.isFlag(i));
        }
    }

    @Test
    public void testSetFlagAlreadySet() {
        flags = new Flags(0xFFFFFFFF);
        flags.setFlag(10);
        assertTrue(flags.isFlag(10));
        // Verify all flags still set
        for (int i = 0; i < Flags.NUM_BITS; i++) {
            assertTrue(flags.isFlag(i));
        }
    }

    @Test
    public void testSetFlagMultipleBits() {
        flags = new Flags(0);
        flags.setFlag(0).setFlag(15).setFlag(31);
        assertTrue(flags.isFlag(0));
        assertTrue(flags.isFlag(15));
        assertTrue(flags.isFlag(31));
        assertFalse(flags.isFlag(1));
    }

    @Test
    public void testSetFlagOutOfBoundsNegative() {
        try {
            flags.setFlag(-1);
            fail("Expected IndexOutOfBoundsException for setFlag(-1)");
        } catch (IndexOutOfBoundsException e) {
            // Expected
        }
    }

    @Test
    public void testSetFlagOutOfBoundsTooHigh() {
        try {
            flags.setFlag(Flags.NUM_BITS);
            fail("Expected IndexOutOfBoundsException for setFlag(NUM_BITS)");
        } catch (IndexOutOfBoundsException e) {
            // Expected
        }
    }

    // clearFlag tests
    @Test
    public void testClearFlagSingleBit() {
        flags = new Flags(0xFFFFFFFF);
        flags.clearFlag(5);
        assertFalse(flags.isFlag(5));
        for (int i = 0; i < Flags.NUM_BITS; i++) {
            if (i == 5) assertFalse(flags.isFlag(i));
            else assertTrue(flags.isFlag(i));
        }
    }

    @Test
    public void testClearFlagAlreadyClear() {
        flags = new Flags(0);
        flags.clearFlag(10);
        assertFalse(flags.isFlag(10));
        // Verify all flags still clear
        for (int i = 0; i < Flags.NUM_BITS; i++) {
            assertFalse(flags.isFlag(i));
        }
    }

    @Test
    public void testClearFlagMultipleBits() {
        flags = new Flags(0xFFFFFFFF);
        flags.clearFlag(0).clearFlag(15).clearFlag(31);
        assertFalse(flags.isFlag(0));
        assertFalse(flags.isFlag(15));
        assertFalse(flags.isFlag(31));
        assertTrue(flags.isFlag(1));
    }

    @Test
    public void testClearFlagOutOfBoundsNegative() {
        try {
            flags.clearFlag(-1);
            fail("Expected IndexOutOfBoundsException for clearFlag(-1)");
        } catch (IndexOutOfBoundsException e) {
            // Expected
        }
    }

    @Test
    public void testClearFlagOutOfBoundsTooHigh() {
        try {
            flags.clearFlag(Flags.NUM_BITS);
            fail("Expected IndexOutOfBoundsException for clearFlag(NUM_BITS)");
        } catch (IndexOutOfBoundsException e) {
            // Expected
        }
    }

    // flipFlag tests
    @Test
    public void testFlipFlagFromZeroToOne() {
        flags = new Flags(0);
        flags.flipFlag(7);
        assertTrue(flags.isFlag(7));
        for (int i = 0; i < Flags.NUM_BITS; i++) {
            if (i == 7) assertTrue(flags.isFlag(i));
            else assertFalse(flags.isFlag(i));
        }
    }

    @Test
    public void testFlipFlagFromOneToZero() {
        flags = new Flags(1 << 7);
        flags.flipFlag(7);
        assertFalse(flags.isFlag(7));
        // Verify all flags clear
        for (int i = 0; i < Flags.NUM_BITS; i++) {
            assertFalse(flags.isFlag(i));
        }
    }

    @Test
    public void testFlipFlagMultipleTimes() {
        flags = new Flags(0);
        flags.flipFlag(3).flipFlag(3);
        assertFalse(flags.isFlag(3));
    }

    @Test
    public void testFlipFlagOutOfBoundsNegative() {
        try {
            flags.flipFlag(-1);
            fail("Expected IndexOutOfBoundsException for flipFlag(-1)");
        } catch (IndexOutOfBoundsException e) {
            // Expected
        }
    }

    @Test
    public void testFlipFlagOutOfBoundsTooHigh() {
        try {
            flags.flipFlag(Flags.NUM_BITS);
            fail("Expected IndexOutOfBoundsException for flipFlag(NUM_BITS)");
        } catch (IndexOutOfBoundsException e) {
            // Expected
        }
    }

    // setAllFlags tests
    @Test
    public void testSetAllFlags() {
        flags = new Flags(0);
        flags.setAllFlags();
        assertEquals(32, flags.toString().replaceAll("0", "").length());
        for (int i = 0; i < Flags.NUM_BITS; i++) {
            assertTrue(flags.isFlag(i));
        }
    }

    @Test
    public void testSetAllFlagsWithInt() {
        flags = new Flags(0);
        flags.setAllFlags(0x12345678);
        // Verify specific bits are set correctly
        // 0x12345678 has bits 3,4,5,6 set in low byte
        assertTrue(flags.isFlag(3));
        assertTrue(flags.isFlag(4));
        assertTrue(flags.isFlag(5));
        assertFalse(flags.isFlag(0));
        assertFalse(flags.isFlag(1));

        final String text = flags.toString();
        for(int i = 0; i < Flags.NUM_BITS; i++) {
            final boolean bit = flags.isFlag(i);
            final String c = "" + text.charAt(text.length() - 1 - i);
            if (bit==false) {
                assertEquals("0", c);
            } else {
                assertEquals("1", c);
            }
        }
    }

    @Test
    public void testSetAllFlagsWithFlags() {
        flags = new Flags(0);
        Flags other = new Flags(0xABCDEF01);
        flags.setAllFlags(other);
        // Compare by checking the patterns match
        assertEquals(other.toString(), flags.toString());
    }

    // clearAllFlags tests
    @Test
    public void testClearAllFlags() {
        flags = new Flags(0xFFFFFFFF);
        flags.clearAllFlags();
        assertEquals(0, flags.toString().replaceAll("0", "").length());
        for (int i = 0; i < Flags.NUM_BITS; i++) {
            assertFalse(flags.isFlag(i));
        }
    }

    // flipAllFlags tests
    @Test
    public void testFlipAllFlags() {
        flags = new Flags(0xAAAAAAAA);
        flags.flipAllFlags();
        // After flip, should have alternating pattern: 0101...
        for (int i = 0; i < Flags.NUM_BITS; i++) {
            if (i % 2 == 0) assertTrue(flags.isFlag(i));
            else assertFalse(flags.isFlag(i));
        }
    }

    @Test
    public void testFlipAllFlagsZero() {
        flags = new Flags(0);
        flags.flipAllFlags();
        // All bits should be set after flip
        for (int i = 0; i < Flags.NUM_BITS; i++) {
            assertTrue(flags.isFlag(i));
        }
    }

    // numFlags test
    @Test
    public void testNumFlags() {
        assertEquals(32, flags.numFlags());
        assertEquals(Flags.NUM_BITS, flags.numFlags());
    }

    // Bitwise operation tests
    @Test
    public void testCopy() {
        flags = new Flags(0xDEADBEEF);
        Flags copy = flags.copy();
        assertEquals(flags.toString(), copy.toString());
        copy.flipFlag(0);
        assertNotEquals(flags.toString(), copy.toString());
    }

    @Test
    public void testAnd() {
        flags = new Flags(0xF0F0F0F0);
        Flags other = new Flags(0x0F0F0F0F);
        Flags result = flags.and(other);
        // Result should have no bits set
        for (int i = 0; i < Flags.NUM_BITS; i++) {
            assertFalse(result.isFlag(i));
        }
    }

    @Test
    public void testOr() {
        flags = new Flags(0xF0F0F0F0);
        Flags other = new Flags(0x0F0F0F0F);
        Flags result = flags.or(other);
        // Result should have all bits set
        for (int i = 0; i < Flags.NUM_BITS; i++) {
            assertTrue(result.isFlag(i));
        }
    }

    @Test
    public void testXor() {
        flags = new Flags(0xF0F0F0F0);
        Flags other = new Flags(0xF0F0F0F0);
        Flags result = flags.xor(other);
        // Result should have no bits set
        for (int i = 0; i < Flags.NUM_BITS; i++) {
            assertFalse(result.isFlag(i));
        }
    }

    @Test
    public void testNot() {
        flags = new Flags(0);
        Flags result = flags.not();
        assertEquals(0xFFFFFFFF, result.flags);
    }

    @Test
    public void testNand() {
        flags = new Flags(0xFFFFFFFF);
        Flags other = new Flags(0xFFFFFFFF);
        Flags result = flags.nand(other);
        assertEquals(0, result.flags);
    }

    @Test
    public void testNor() {
        flags = new Flags(0);
        Flags other = new Flags(0);
        Flags result = flags.nor(other);
        // All bits should be set (NOR of all 0s)
        for (int i = 0; i < Flags.NUM_BITS; i++) {
            assertTrue(result.isFlag(i));
        }
    }

    @Test
    public void testXnor() {
        flags = new Flags(0xF0F0F0F0);
        Flags other = new Flags(0xF0F0F0F0);
        Flags result = flags.xnor(other);
        // All bits should be set (same inputs -> all 1s)
        for (int i = 0; i < Flags.NUM_BITS; i++) {
            assertTrue(result.isFlag(i));
        }
    }

    @Test
    public void testSet() {
        flags = new Flags(0);
        Flags other = new Flags(0x12345678);
        flags.set(other);
        // Compare by checking individual bits match
        assertEquals(other.toString(), flags.toString());
    }

    // Interface method tests
    @Test
    public void testGetFlagIndex() {
        flags = new Flags(0);
        assertEquals(5, flags.getFlagIndex(5));
        assertEquals(0, flags.getFlagIndex(0));
        assertEquals(31, flags.getFlagIndex(31));
    }

    @Test
    public void testGetFlags() {
        flags = new Flags(0x12345678);
        Flags result = flags.getFlags();
        assertEquals(flags, result);
    }

    @Test
    public void testSetFlagsFromOther() {
        flags = new Flags(0);
        Flags other = new Flags(0xABCDEF01);
        flags.setFlags(other);
        assertEquals(other.toString(), flags.toString());
    }

    // toString test
    @Test
    public void testToStringAllZeros() {
        flags = new Flags(0);
        String result = flags.toString();
        assertEquals(32, result.length());
        assertTrue(result.matches("0{32}"));
    }

    @Test
    public void testToStringAllOnes() {
        flags = new Flags(0xFFFFFFFF);
        String result = flags.toString();
        assertEquals(32, result.length());
        assertTrue(result.matches("1{32}"));
    }

    @Test
    public void testToStringMixed() {
        flags = new Flags(0xAAAAAAAA);
        String result = flags.toString();
        assertEquals(32, result.length());
        assertEquals("10101010101010101010101010101010", result);
    }

    // Method chaining tests
    @Test
    public void testMethodChaining() {
        flags = new Flags(0);
        Flags result = flags.setFlag(0).setFlag(5).clearFlag(0).flipFlag(10);
        assertFalse(flags.isFlag(0));
        assertTrue(flags.isFlag(5));
        assertTrue(flags.isFlag(10));
        assertEquals(flags, result);
    }

}
