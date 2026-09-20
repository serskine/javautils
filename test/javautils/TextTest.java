package javautils;

import javautils.math.Matrix;
import javautils.math.MatrixImpl;
import javautils.parser.MatrixParser;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TextTest {

    private static final MatrixParser MATRIX_PARSER = new MatrixParser();

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
        final Matrix expected = new MatrixImpl(3, 4);
        expected.set(0, 0, 0);
        expected.set(0, 1, 0);
        expected.set(0, 2, 0);
        expected.set(0, 3, 0);
        expected.set(1, 0, 0);
        expected.set(1, 1, 1);
        expected.set(1, 2, 2);
        expected.set(1, 3, 3);
        expected.set(2, 0, 0);
        expected.set(2, 1, 2);
        expected.set(2, 2, 4);
        expected.set(2, 3, 6);

        final String expectedDescription = MATRIX_PARSER.describe(expected);
        Logger.info(expectedDescription);

        final Matrix observed = MATRIX_PARSER.parse(expectedDescription);

        Logger.info(observed.describe());

        final String observedDescription = MATRIX_PARSER.describe(observed);

        assertEquals(expectedDescription, observedDescription);


    }

    @Test
    public void asTable() {
        final String[][] t = new String[][] {
            {"aaaa\naaa\na", "bb", "c", "ddddddddddddd", "e\ne\ne\ne\n\ne"},
            {"bbb", "bb", "c", "ddddddddddddd", "xxx"},
            {"x", "x", "xx", "xx", "xxxxx", "wwww", "eee", "rrr", "tt"},
            {"ddd", "dd", "dd", "ddddddddddddd", null}
        };

        final String observed = Text.asTable(t);
        Logger.info(observed);
    }
}
