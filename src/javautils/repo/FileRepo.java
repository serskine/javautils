package javautils.repo;

import java.io.IOException;
import java.io.RandomAccessFile;

public class FileRepo implements Repo {

    protected final RandomAccessFile file;

    public FileRepo(RandomAccessFile f) {
        this.file = f;

    }

    @Override
    public final void seek(long position) {
        try {
            file.seek(position);
        } catch (IOException e) {
            throw new RuntimeException("Failed to seek to position " + position, e);
        }
    }

    @Override
    public final long position() {
        try {
            return file.getFilePointer();
        } catch (IOException e) {
            throw new RuntimeException("Failed to get current position", e);
        }
    }

    @Override
    public final byte readByte() {
        try {
            return file.readByte();
        } catch (IOException e) {
            throw new RuntimeException("Failed to read byte", e);
        }
    }

    @Override
    public final short readShort() {
        try {
            return file.readShort();
        } catch (IOException e) {
            throw new RuntimeException("Failed to read short", e);
        }
    }

    @Override
    public final char readChar() {
        try {
            return file.readChar();
        } catch (IOException e) {
            throw new RuntimeException("Failed to read char", e);
        }
    }

    @Override
    public final int readInt() {
        try {
            return file.readInt();
        } catch (IOException e) {
            throw new RuntimeException("Failed to read int", e);
        }
    }

    @Override
    public final long readLong() {
        try {
            return file.readLong();
        } catch (IOException e) {
            throw new RuntimeException("Failed to read long", e);
        }
    }

    @Override
    public final float readFloat() {
        try {
            return file.readFloat();
        } catch (IOException e) {
            throw new RuntimeException("Failed to read float", e);
        }
    }

    @Override
    public final double readDouble() {
        try {
            return file.readDouble();
        } catch (IOException e) {
            throw new RuntimeException("Failed to read double", e);
        }
    }

    @Override
    public final boolean readBoolean() {
        try {
            return file.readBoolean();
        } catch (IOException e) {
            throw new RuntimeException("Failed to read boolean", e);
        }
    }

    @Override
    public final void writeByte(byte value) {
        try {
            file.writeByte(value);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write byte", e);
        }
    }

    @Override
    public final void writeShort(short value) {
        try {
            file.writeShort(value);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write short", e);
        }
    }

    @Override
    public final void writeChar(char value) {
        try {
            file.writeChar(value);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write char", e);
        }

    }

    @Override
    public final void writeInt(int value) {
        try {
            file.writeInt(value);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write int", e);
        }
    }

    @Override
    public final void writeLong(long value) {
        try {
            file.writeLong(value);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write long", e);
        }
    }

    @Override
    public final void writeFloat(float value) {
        try {
            file.writeFloat(value);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write float", e);
        }
    }

    @Override
    public final void writeDouble(double value) {
        try {
            file.writeDouble(value);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write double", e);
        }
    }

    @Override
    public final void writeBoolean(boolean value) {
        try {
            file.writeBoolean(value);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write boolean", e);
        }
    }
}
