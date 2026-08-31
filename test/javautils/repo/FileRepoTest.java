package javautils.repo;

import javautils.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Serializable;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileRepoTest {

    private String path = "tmp/" + getClass().getName() + "_testfile.bin";
    private RandomAccessFile file;
    private Repo repo;


    private TestNode theNode;


    @Before
    public void onSetup() throws FileNotFoundException {
        this.file = new RandomAccessFile(path, "rw");
        this.repo = new FileRepo(file);

        theNode = new TestNode(new TestNode(new TestNode(null)));
        Logger.info("theNode: " + theNode);
    }

    public class TestObject implements Serializable {
        public byte theByte = (byte) 1;
        public char theChar = 'x';
        public boolean theBoolean = true;
        public short theShort = (short) 2;
        public int theInt = 3;
        public long theLong = 4L;
        public float theFloat = 5.5f;
        public double theDouble = 6.6d;
        public String theString = "theString";

    }

    @Test
    public void testCrud() {

        final TestObject obj = new TestObject();
        final long addr = repo.position();
        repo.writeObject(obj);

        final long newAddr = repo.position();

        // Read back the object
        repo.seek(addr);
        final TestObject readObj = repo.readObject(TestObject.class);

        assert readObj != null;
        assert readObj.theByte == obj.theByte;
        assert readObj.theChar == obj.theChar;
        assert readObj.theBoolean == obj.theBoolean;
        assert readObj.theShort == obj.theShort;
        assert readObj.theInt == obj.theInt;
        assert readObj.theLong == obj.theLong;
        assert readObj.theFloat == obj.theFloat;
        assert readObj.theDouble == obj.theDouble;
        assert readObj.theString.equals(obj.theString);
    }

    @Test
    public void testSetup() {
    }

    @Test
    public void toAndFromBytes() throws IOException, ClassNotFoundException {
        byte[] bytes = repo.toBytes(theNode);
        final TestNode observed = repo.fromBytes(bytes, TestNode.class);

        Logger.info("observed: " + ((observed==null) ? null : observed.toString()));

        assertEquals(theNode, theNode);
        assertEquals(theNode, observed);
    }
}
