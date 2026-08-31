package javautils.flags;

public class Flags implements HasFlags<Integer> {
    private static final int NONE = 0x00000000;
    private static final int ALL = 0xFFFFFFFF;
    public static final int NUM_BITS = 32;

    protected int flags = 0;

    public Flags() {
        this(NONE);
    }

    public Flags(final int seed) {
        this.flags = seed;
    }

    public final boolean isFlag(int flag) {
        verifyIndex(flag);
        return (flags & (1 << flag)) != 0;
    }

    public final Flags setFlag(int flag) {
        verifyIndex(flag);
        flags |= (1 << flag);
        return this;
    }

    public final Flags clearFlag(int flag) {
        verifyIndex(flag);
        flags &= ~(1 << flag);
        return this;
    }

    public final Flags flipFlag(int flag) {
        verifyIndex(flag);
        flags ^= (1 << flag);
        return this;
    }

    public final Flags clearAllFlags() {
        flags = NONE;
        return this;
    }

    public final Flags setAllFlags() {
        flags = ALL;
        return this;
    }

    public final Flags flipAllFlags() {
        flags = ~flags;
        return this;
    }

    public final Flags setAllFlags(int allFlags) {
        this.flags = allFlags;
        return this;
    }

    public final Flags setAllFlags(Flags allFlags) {
        setAllFlags(allFlags.flags);
        return this;
    }

    @Override
    public int getFlagIndex(Integer flag) {
        return flag;
    }

    public final int numFlags() {
        return NUM_BITS;
    }

    @Override
    public Flags getFlags() {
        return this;
    }

    @Override
    public HasFlags<Integer> setFlags(HasFlags<Integer> other) {
        this.getFlags().setAllFlags(other.getFlags().flags);
        return this;
    }

    @Override
    public String toString() {
        return String.format("%32s", Integer.toBinaryString(flags)).replace(' ', '0');
    }

    private void verifyIndex(final int index) {
        if (index < 0 || index >= numFlags()) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + NUM_BITS);
        }
    }

    public final Flags copy() {
        return new Flags(flags);
    }

    public final Flags and(Flags other) {
        return new Flags(flags & other.flags);
    }

    public final Flags or(Flags other) {
        return new Flags(flags | other.flags);
    }

    public final Flags xor(Flags other) {
        return new Flags(flags ^ other.flags);
    }

    public final Flags not() {
        return new Flags(~flags);
    }

    public final Flags nand(Flags other) {
        return new Flags(~(flags & other.flags));
    }

    public final Flags nor(Flags other) {
        return new Flags(~(flags | other.flags));
    }

    public final Flags xnor(Flags other) {
        return new Flags(~(flags ^ other.flags));
    }

    public final Flags set(final Flags other) {
        setAllFlags(other.flags);
        return this;
    }

}
