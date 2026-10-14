package javautils.common;

import javautils.Logger;
import javautils.Text;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.util.Collection;
import java.util.Set;

public abstract class CollectionTest<T> {

    protected abstract Collection<T> createCollection();

    public abstract T createValueA();
    public abstract T createValueB();
    public abstract T createValueC();

    protected T a, b, c;
    protected Collection<T> collection;

    @BeforeEach
    public void onSetup() {

        collection = createCollection();
        Logger.info(Text.describeIterable(collection));

        a = createValueA();
        b = createValueB();
        c = createValueC();
    }

    @AfterEach
    public void onTearDown() {
        Logger.info(Text.describeIterable(collection));
    }

}
