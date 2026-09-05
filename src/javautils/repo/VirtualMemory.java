package javautils.repo;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.Iterator;

public class VirtualMemory implements Memory {

    VirtualMemory(RandomAccessFile f) {
        this(f, Long.MAX_VALUE);
    }

    private FileRepo fileRepo;
    VirtualMemory(RandomAccessFile f, long capacity) {
        this.fileRepo = new FileRepo(f);
        if (!isInitialized()) {
            init(capacity);
        }
    }


    @Override
    public Repo getRepo() {
        return fileRepo;
    }
}
