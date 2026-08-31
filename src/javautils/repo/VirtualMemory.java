package javautils.repo;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.Iterator;

public class VirtualMemory extends FileRepo implements Memory {

    VirtualMemory(RandomAccessFile f) {
        this(f, Long.MAX_VALUE);
    }

    VirtualMemory(RandomAccessFile f, long capacity) {
        super(f);
        if (!isInitialized()) {
            init(capacity);
        }
    }


}
