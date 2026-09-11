package javautils.parsable;

import javautils.Logger;
import javautils.parser.Parsable;
import javautils.parser.ParsableCollection;
import javautils.parser.ParsableObj;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class ParsableObjTest {
    private List<Parsable> items;

    @Before
    public void onSetup() {
        items = new ArrayList<>();

        items.add(ParsableObj.create(null));
        items.add(ParsableObj.create((byte) 0x5F));
        items.add(ParsableObj.create((short) 0xA8));
        items.add(ParsableObj.create((char) 'a'));
        items.add(ParsableObj.create((int) 45));
        items.add(ParsableObj.create((long) 4534957435643L));
        items.add(ParsableObj.create((float) 3.12F));
        items.add(ParsableObj.create((double) 12345.6787654321234D));
        items.add(ParsableObj.create(Boolean.TRUE));
        items.add(ParsableObj.create("The quick brown fox jumped over the lazy dogs."));

        items.add(ParsableObj.create(Byte.class, null));
        items.add(ParsableObj.create(Short.class, null));
        items.add(ParsableObj.create(Character.class, null));
        items.add(ParsableObj.create(Integer.class, null));
        items.add(ParsableObj.create(Long.class, null));
        items.add(ParsableObj.create(Float.class, null));
        items.add(ParsableObj.create(Double.class, null));
        items.add(ParsableObj.create(Boolean.class, null));
        items.add(ParsableObj.create(String.class, null));
        items.add(ParsableObj.create(Object.class, null));

        for(Parsable item : items) {
            Logger.info(" - " + item.toString());
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
            final Parsable parsableItem = items.get(i);

            try {
                final String expectedAsString = parsableItem.describe();

                parsableItem.parseFromText(expectedAsString);

                final String observedAsString = parsableItem.describe();

                assertEquals(expectedAsString, observedAsString);

                Logger.info("observed[" + i + "]: " + observedAsString);

            } catch (Exception e) {
                fail("Item[" + i + "]: " + items.toString(), e);
            }
        }
    }
}
