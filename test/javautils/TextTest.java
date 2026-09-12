package javautils;

import javautils.math.Matrix;
import javautils.math.MatrixImpl;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TextTest {

    @Test
    public void extractTokens_and_placeTokens() {
        final String format = "The {speed} {color} {animal} {action} over the {who}";
        final String input = "The quick brown fox jumped over the lazy dogs";

        final Map<String, String> observed = Text.extractTokens(format, input);

        assertEquals("quick", observed.get("speed"));
        assertEquals("brown", observed.get("color"));
        assertEquals("fox", observed.get("animal"));
        assertEquals("jumped", observed.get("action"));
        assertEquals("lazy dogs", observed.get("who"));

        final String output = Text.substituteTokens(observed, format);

        assertEquals(input, output);
    }

    @Test
    public void extractTokens_and_placeTokens_matrix() {
        final String format = "{numRows} x {numCols}\n" +
                "{cells}";

        final Matrix m = new MatrixImpl(3, 4);
        m.set(0, 0, 0);
        m.set(0, 1, 0);
        m.set(0, 2, 0);
        m.set(0, 3, 0);
        m.set(1, 0, 0);
        m.set(1, 1, 1);
        m.set(1, 2, 2);
        m.set(1, 3, 3);
        m.set(2, 0, 0);
        m.set(2, 1, 2);
        m.set(2, 2, 4);
        m.set(2, 3, 6);

        final String input = m.describe();
        Logger.info(input);

        final Map<String, String> tokenMap = Text.extractTokens(format, input);

        assertNotNull(tokenMap);
        assertEquals(3, tokenMap.size());

        assertEquals("3", tokenMap.get("numRows"));
        assertEquals("4", tokenMap.get("numCols"));

        final String tokenCells = tokenMap.get("cells");
        assertEquals("[ 0.00][ 0.00][ 0.00][ 0.00]\n" +
                "[ 0.00][ 1.00][ 2.00][ 3.00]\n" +
                "[ 0.00][ 2.00][ 4.00][ 6.00]\n" +
                "\n", tokenCells);

        final Matrix observed = new MatrixImpl(10, 10);
        observed.parseFromText(input);


    }
}
