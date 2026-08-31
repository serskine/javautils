package javautils.flags;

public interface HasFlags<Key> {
    int getFlagIndex(Key flag);
    int numFlags();
    Flags getFlags();
    HasFlags<Key> setFlags(HasFlags<Key> flags);

    default void verifyKey(Key key) {
        final int index = getFlagIndex(key);
        if (index < 0 || index >= numFlags()) {
            throw new IndexOutOfBoundsException("Flag index is not valid for " + key);
        }
    }

    default boolean isFlag(Key key) {
        verifyKey(key);
        return getFlags().isFlag(getFlagIndex(key));
    }

    default HasFlags<Key> clearFlag(Key key) {
        verifyKey(key);
        getFlags().clearFlag(getFlagIndex(key));
        return this;
    }

    default HasFlags<Key> setFlag(Key key) {
        verifyKey(key);
        getFlags().setFlag(getFlagIndex(key));
        return this;
    }

    default HasFlags<Key> flipFlag(Key key) {
        verifyKey(key);
        getFlags().flipFlag(getFlagIndex(key));
        return this;
    }

    default HasFlags<Key> clearAllFlags() {
        getFlags().clearAllFlags();
        return this;
    }

    default HasFlags<Key> setAllFlags() {
        getFlags().setAllFlags();
        return this;
    }

    default HasFlags<Key> flipAllFlags() {
        getFlags().flipAllFlags();
        return this;
    }

    default HasFlags<Key> setAllFlags(HasFlags<Key> other) {
        getFlags().setAllFlags(other.getFlags());
        return this;
    }

    default HasFlags<Key> flipAllFlags(HasFlags<Key> other) {
        getFlags().setAllFlags(other.getFlags());
        return this;
    }
}
