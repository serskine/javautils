package javautils.repo;

import java.io.*;

public interface Repo {


    void seek(long position);
    long position();

    byte readByte();
    short readShort();
    char readChar();
    int readInt();
    long readLong();
    float readFloat();
    double readDouble();
    boolean readBoolean();

    default boolean isNull(int value) {
        return value < 0;
    }


    default byte[] readBytes() {
        try {
            final int length = readInt();
            if (length<0) {
                return null;    // Negative bytes means null value
            }
            final byte[] bytes = new byte[length];
            for (int i = 0; i < length; i++) {
                bytes[i] = readByte();
            }
            return bytes;
        } catch (Exception e) {
            throw new RuntimeException("Failed to read byte array", e);
        }
    }

    default <T extends Serializable> T readObject(final Class<T> clazz) {
        try {
            final byte[] bytes = readBytes();
            if (bytes == null) {
                return null; // Null value
            } else {
                return fromBytes(bytes, clazz);
            }
        } catch (EOFException e) {
            throw new RuntimeException("Failed to read object of type " + clazz.getName() + " from a truncated serialized payload", e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to read object of type " + clazz.getName() + " using readBytes()", e);
        }
    }

    default byte readByteRelative(long offset) {    return readByteAt(position() + offset); }
    default short readShortRelative(long offset) {    return readShortAt(position() + offset); }
    default char readCharRelative(long offset) {    return readCharAt(position() + offset); }
    default int readIntRelative(long offset) {    return readIntAt(position() + offset); }
    default long readLongRelative(long offset) {    return readLongAt(position() + offset); }
    default float readFloatRelative(long offset) {    return readFloatAt(position() + offset); }
    default double readDoubleRelative(long offset) {    return readDoubleAt(position() + offset); }
    default boolean readBooleanRelative(long offset) {  return readBooleanAt(position() + offset); }
    default byte[] readBytesRelative(long offset) {    return readBytesAt(position() + offset); }
    default <T extends Serializable> T readObjectRelative(long offset, final Class<T> clazz) {
        return readObjectAt(position() + offset, clazz);
    }

    void writeByte(byte value);
    void writeShort(short value);
    void writeChar(char value);
    void writeInt(int value);
    void writeLong(long value);
    void writeFloat(float value);
    void writeDouble(double value);
    void writeBoolean(boolean value);

    default void writeNull() {
        writeInt(-1);
    }

    default void writeBytes(byte... bytes) {
        try {
            if (bytes==null) {
                writeNull();
                return;
            }
            writeInt(bytes.length);
            for (byte b : bytes) {
                writeByte(b);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to write byte array", e);
        }
    }

    default <T extends Serializable> void writeObject(T value) {
        try {
            if (value==null) {
                writeNull();
            } else {
                final byte[] bytes = toBytes(value);
                writeBytes(bytes);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to write object of type " + value.getClass().getName(), e);
        }
    }

    default void writeByteRelative(long offset, byte value) {    writeByteAt(position() + offset, value); }
    default void writeShortRelative(long offset, short value) {    writeShortAt(position() + offset, value); }
    default void writeCharRelative(long offset, char value) {    writeCharAt(position() + offset, value); }
    default void writeIntRelative(long offset, int value) {    writeIntAt(position() + offset, value); }
    default void writeLongRelative(long offset, long value) {    writeLongAt(position() + offset, value); }
    default void writeFloatRelative(long offset, float value) {    writeFloatAt(position() + offset, value); }
    default void writeDoubleRelative(long offset, double value) {    writeDoubleAt(position() + offset, value); }
    default void writeBytesRelative(long offset, byte... bytes) {    writeBytesAt(position() + offset, bytes); }
    default void writeBooleanRelative(long offset, boolean value) {    writeBooleanAt(position() + offset, value); }
    default void writeObjectRelative(long offset, Serializable value) {    writeObjectAt(position() + offset, value); }

    default byte readByteAt(long position) {
        try {
            seek(position);
            return readByte();
        } catch (Exception e) {
            throw new RuntimeException("Failed to read byte at position " + position, e);
        }
    }

    default void writeByteAt(long position, byte value) {
        try {
            seek(position);
            writeByte(value);
        } catch (Exception e) {
            throw new RuntimeException("Failed to write byte at position " + position, e);
        }
    }

    default short readShortAt(long position) {
        try {
            seek(position);
            return readShort();
        } catch (Exception e) {
            throw new RuntimeException("Failed to read short at position " + position, e);
        }
    }

    default void writeShortAt(long position, short value) {
        try {
            seek(position);
            writeShort(value);
        } catch (Exception e) {
            throw new RuntimeException("Failed to write short at position " + position, e);
        }
    }

    default char readCharAt(long position) {
        try {
            seek(position);
            return readChar();
        } catch (Exception e) {
            throw new RuntimeException("Failed to read char at position " + position, e);
        }
    }

    default void writeCharAt(long position, char value) {
        try {
            seek(position);
            writeChar(value);
        } catch (Exception e) {
            throw new RuntimeException("Failed to write char at position " + position, e);
        }
    }

    default int readIntAt(long position) {
        try {
            seek(position);
            return readInt();
        } catch (Exception e) {
            throw new RuntimeException("Failed to read int at position " + position, e);
        }
    }

    default void writeIntAt(long position, int value) {
        try {
            seek(position);
            writeInt(value);
        } catch (Exception e) {
            throw new RuntimeException("Failed to write int at position " + position, e);
        }
    }

    default long readLongAt(long position) {
        try {
            seek(position);
            return readLong();
        } catch (Exception e) {
            throw new RuntimeException("Failed to read long at position " + position, e);
        }
    }

    default void writeLongAt(long position, long value) {
        try {
            seek(position);
            writeLong(value);
        } catch (Exception e) {
            throw new RuntimeException("Failed to write long at position " + position, e);
        }
    }

    default float readFloatAt(long position) {
        try {
            seek(position);
            return readFloat();
        } catch (Exception e) {
            throw new RuntimeException("Failed to read float at position " + position, e);
        }
    }

    default void writeFloatAt(long position, float value) {
        try {
            seek(position);
            writeFloat(value);
        } catch (Exception e) {
            throw new RuntimeException("Failed to write float at position " + position, e);
        }
    }

    default double readDoubleAt(long position) {
        try {
            seek(position);
            return readDouble();
        } catch (Exception e) {
            throw new RuntimeException("Failed to read double at position " + position, e);
        }
    }

    default void writeDoubleAt(long position, double value) {
        try {
            seek(position);
            writeDouble(value);
        } catch (Exception e) {
            throw new RuntimeException("Failed to write double at position " + position, e);
        }
    }

    default boolean readBooleanAt(long position) {
        try {
            seek(position);
            return readBoolean();
        } catch (Exception e) {
            throw new RuntimeException("Failed to read boolean at position " + position, e);
        }
    }

    default void writeBooleanAt(long position, boolean value) {
        try {
            seek(position);
            writeBoolean(value);
        } catch (Exception e) {
            throw new RuntimeException("Failed to write boolean at position " + position, e);
        }
    }

    default byte[] readBytesAt(long position) {
        try {
            seek(position);
            return readBytes();
        } catch (Exception e) {
            throw new RuntimeException("Failed to read bytes at position " + position, e);
        }
    }

    default void writeBytesAt(long position, byte[] value) {
        try {
            seek(position);
            writeBytes(value);
        } catch (Exception e) {
            throw new RuntimeException("Failed to write bytes at position " + position, e);
        }
    }

    default <T extends Serializable> T readObjectAt(long position, final Class<T> clazz) {
        try {
            seek(position);
            return readObject(clazz);
        } catch (Exception e) {
            throw new RuntimeException("Failed to read object of type " + clazz.getName() + " at position " + position, e);
        }
    }

    default <T extends Serializable> void writeObjectAt(long position, T value) {
        try {
            seek(position);
            writeObject(value);
        } catch (Exception e) {
            throw new RuntimeException("Failed to write object of type " + value.getClass().getName() + " at position " + position, e);
        }
    }

    default <T extends Serializable> int bytesUsedForArray(int byteArrayLength) {
        return Integer.BYTES + byteArrayLength; // 4 bytes for length + actual bytes
    }

    default <T extends Serializable> byte[] toBytes(T value) {
        try {
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            if (value == null) {
                baos.write(-1); // Write -1 for null value
                return baos.toByteArray();
            } else {
                final ObjectOutputStream oos = new ObjectOutputStream(baos);
                oos.writeObject(value);
                oos.flush();
                final byte[] payload = baos.toByteArray();

                final ByteArrayOutputStream out = new ByteArrayOutputStream(Integer.BYTES + payload.length);
                final DataOutputStream dos = new DataOutputStream(out);
                dos.writeInt(payload.length);
                dos.write(payload);
                return out.toByteArray();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to serialize object of type " + value.getClass().getName(), e);
        }

    }

    default <T extends Serializable> T fromBytes(byte[] repoBytes, Class<T> clazz) throws IOException, ClassNotFoundException {
        try {
            if (repoBytes == null) {
                return null;
            }

            final DataInputStream dis = new DataInputStream(new ByteArrayInputStream(repoBytes));
            int length = dis.readInt();
            if (length < 0) {
                return null;
            }

            byte[] payload = new byte[length];
            dis.readFully(payload);

            final ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(payload));
            Object obj = ois.readObject();
            if (obj == null) {
                return null;
            }
            if (!clazz.isInstance(obj)) {
                throw new ClassCastException("Expected " + clazz.getName() + " but got " + obj.getClass().getName());
            }
            return clazz.cast(obj);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Failed to deserialize object of type " + clazz.getName(), e);
        }
    }
}
