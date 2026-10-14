package javautils.common;

import java.util.Collection;
import java.util.HashSet;

public class HashSetTest extends SetTest<String> {

    @Override
    protected Collection<String> createCollection() {
        return new HashSet<>();
    }

    @Override
    public String createValueA() {
        return "A";
    }

    @Override
    public String createValueB() {
        return "B";
    }

    @Override
    public String createValueC() {
        return "C";
    }
}
