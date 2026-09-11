package javautils;

import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;

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
}
