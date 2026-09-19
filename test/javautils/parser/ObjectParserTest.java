package javautils.parser;

import javautils.Logger;
import javautils.parser.Parsable;
import javautils.parser.ObjectParser;
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

    @Before
    public void onSetup() {

        items = new ArrayList<>();
        parsers = new ArrayList<>();

        parsers.add(ObjectParser.create(null));
        parsers.add(ObjectParser.create((byte) 0x5F));
        parsers.add(ObjectParser.create((short) 0xA8));
        parsers.add(ObjectParser.create((char) 'a'));
        parsers.add(ObjectParser.create((int) 45));
        parsers.add(ObjectParser.create((long) 4534957435643L));
        parsers.add(ObjectParser.create((float) 3.12F));
        parsers.add(ObjectParser.create((double) 12345.6787654321234D));
        parsers.add(ObjectParser.create(Boolean.TRUE));
        parsers.add(ObjectParser.create("The quick brown fox jumped over the lazy dogs."));

        parsers.add(ObjectParser.create(Byte.class, null));
        parsers.add(ObjectParser.create(Short.class, null));
        parsers.add(ObjectParser.create(Character.class, null));
        parsers.add(ObjectParser.create(Integer.class, null));
        parsers.add(ObjectParser.create(Long.class, null));
        parsers.add(ObjectParser.create(Float.class, null));
        parsers.add(ObjectParser.create(Double.class, null));
        parsers.add(ObjectParser.create(Boolean.class, null));
        parsers.add(ObjectParser.create(String.class, null));
        parsers.add(ObjectParser.create(Object.class, null));

        for(Parser parser : parsers) {
            Logger.info(" - " + parser.toString());
        }

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
            final Parser<Object> parser = parsers.get(i);
            final Object item = parsers.get(i).getItem();
            try {
                final String expectedAsString = parser.describe(item);

                final Object parsedFromString = parser.parseFromText(expectedAsString);

                final String observedAsString = parser.describe(parsedFromString);

                assertEquals(expectedAsString, observedAsString);

                Logger.info("observed[" + i + "]: " + observedAsString);

            } catch (Exception e) {
                fail("Item[" + i + "]: " + items.toString(), e);
            }
        }
    }
}
