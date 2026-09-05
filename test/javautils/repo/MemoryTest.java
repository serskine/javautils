package javautils.repo;

import javautils.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;

public class MemoryTest {

    public static final long MEM_CAPACITY = 1024 * 1024; // 1MB
    private String path = "tmp/testfile.bin";
    private RandomAccessFile file;
    private VirtualMemory vm;
    private ActualMemory am;

    private Memory memory;

    @Before
    public void onSetup() throws FileNotFoundException {
        this.file = new RandomAccessFile(path, "rw");
        this.vm = new VirtualMemory(file); // Initialize with 1MB capacity
        this.vm.init(MEM_CAPACITY);

        this.am = new ActualMemory(MEM_CAPACITY);

        this.memory = this.vm; // or am, depending on what you want to test

        Logger.info("Created VM with file: " + path + " and capacity: " + MEM_CAPACITY + " bytes");
        Logger.info("Created AM of size: " + MEM_CAPACITY + " bytes");
    }

    @After
    public void onTearDown() {

        final Memory.Header header = memory.readHeader();

        assertNotNull(header);

        logMemoryState(memory);

        try {
            file.close();
        } catch (Exception e) {
            Logger.err("Failed to close file", e);
        }
    }

    private void logMemoryState(Memory memory) {
        Logger.info(memory.describe());

    }

    @Test
    public void testSetup() {
        memory.init(1024 * 1024); // Initialize with 1MB capacity
        Logger.info("Initialized VM with 1MB capacity");
    }

    @Test
    public void testCrud() {

        final long addr1 = memory.allocate(1024);

        final long addr2 = memory.allocate(2048);

        final long addr3 = memory.allocate(1024);

        final TestNode expectedNode = new TestNode(new TestNode(new TestNode(null)));

        memory.writeTo(addr1, expectedNode);
        memory.writeTo(addr2, expectedNode);
        memory.writeTo(addr3, expectedNode);

        final TestNode observed1 = memory.readFrom(addr1, TestNode.class);
        final TestNode observed2 = memory.readFrom(addr2, TestNode.class);
        final TestNode observed3 = memory.readFrom(addr3, TestNode.class);

        assertEquals(expectedNode, observed1);
        assertEquals(expectedNode, observed2);
        assertEquals(expectedNode, observed3);

        logMemoryState(memory);

        assertFalse(memory.isFree(addr1));
        assertFalse(memory.isFree(addr2));
        assertFalse(memory.isFree(addr3));

        memory.free(addr2);

        assertFalse(memory.isFree(addr1));
        assertTrue(memory.isFree(addr2));
        assertFalse(memory.isFree(addr3));

        logMemoryState(memory);

        memory.writeTo(addr2, expectedNode);

    }

}
