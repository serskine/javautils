package javautils.repo;

import java.nio.ByteBuffer;

public class MemoryRepo implements Repo {

    private byte[] memory;

    public MemoryRepo(final int capacity) {
        this.memory = new byte[capacity];
    }


    @Override
    public void seek(long position) {
        ByteBuffer.wrap(this.memory, 0, Long.BYTES).putLong(position);
    }

    @Override
    public long position() {
        return ByteBuffer.wrap(this.memory, 0, Long.BYTES).getLong();
    }
    
    public int positionPlusPlus() {
        int currentPosition = (int) position();
        seek(currentPosition + 1);
        return currentPosition;
    }

    @Override
    public byte readByte() {
        return this.memory[positionPlusPlus()];
    }

    @Override
    public short readShort() {
        return (short) ((this.memory[positionPlusPlus()] & 0xFF) << 8 | this.memory[positionPlusPlus()] & 0xFF);
    }

    @Override
    public char readChar() {
        return (char) ((this.memory[positionPlusPlus()] & 0xFF) << 8 | this.memory[positionPlusPlus()] & 0xFF);
    }

    @Override
    public int readInt() {
        return (this.memory[positionPlusPlus()] & 0xFF) << 24 | (this.memory[positionPlusPlus()] & 0xFF) << 16 | (this.memory[positionPlusPlus()] & 0xFF) << 8 | this.memory[positionPlusPlus()] & 0xFF;
    }

    @Override
    public long readLong() {
        return (long) this.readInt() << 32 | this.readInt() & 0xFFFFFFFFL;
    }

    @Override
    public float readFloat() {
        return Float.intBitsToFloat(this.readInt());
    }

    @Override
    public double readDouble() {
        return Double.longBitsToDouble(this.readLong());
    }

    @Override
    public boolean readBoolean() {
        return this.memory[positionPlusPlus()] != 0;
    }

    @Override
    public void writeByte(byte value) {
        this.memory[positionPlusPlus()] = value;
    }

    @Override
    public void writeShort(short value) {
        this.memory[positionPlusPlus()] = (byte) (value >> 8);
        this.memory[positionPlusPlus()] = (byte) value;
    }

    @Override
    public void writeChar(char value) {
        this.memory[positionPlusPlus()] = (byte) (value >> 8);
        this.memory[positionPlusPlus()] = (byte) value;
    }

    @Override
    public void writeInt(int value) {
        this.memory[positionPlusPlus()] = (byte) (value >> 24);
        this.memory[positionPlusPlus()] = (byte) (value >> 16);
        this.memory[positionPlusPlus()] = (byte) (value >> 8);
        this.memory[positionPlusPlus()] = (byte) value;
    }

    @Override
    public void writeLong(long value) {
        this.writeInt((int) (value >> 32));
        this.writeInt((int) value);
    }

    @Override
    public void writeFloat(float value) {
        this.writeInt(Float.floatToIntBits(value));
    }

    @Override
    public void writeDouble(double value) {
        this.writeLong(Double.doubleToLongBits(value));
    }

    @Override
    public void writeBoolean(boolean value) {
        this.memory[positionPlusPlus()] = (byte) (value ? 1 : 0);
    }


}
