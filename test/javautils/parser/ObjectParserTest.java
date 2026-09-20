package javautils.parser;

import javautils.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class ObjectParserTest {
    private List<Object> items;
    private List<ObjectParser> parsers;

    private ObjectParser parser;

    @Before
    public void onSetup() {

        items = new ArrayList<>();
        parsers = new ArrayList<>();

        items.add(null);
        items.add((byte) 0x5F);
        items.add((short) 0xA8);
        items.add((char) 'a');
        items.add((int) 45);
        items.add((long) 4534957435643L);
        items.add((float) 3.12F);
        items.add((double) 12345.6787654321234D);
        items.add(Boolean.TRUE);
        items.add("The quick brown fox jumped over the lazy dogs.");

        parser = new ObjectParser();

    }

    @After
    public void onTearDown() {

    }

    @Test
    public void testSetup() {

    }

    @Test
    public void parsableObj() {


        for(int i=0; i<items.size(); i++) {
            final Object item = items.get(i);
            try {
                final String expectedAsString = parser.describe(item);

                final Object parsedFromString = parser.parse(expectedAsString);

                final String observedAsString = parser.describe(parsedFromString);

                assertEquals(expectedAsString, observedAsString);

                Logger.info("observed[" + i + "]: " + observedAsString);

            } catch (Exception e) {
                fail("Item[" + i + "]: " + items.toString(), e);
            }
        }
    }
}
