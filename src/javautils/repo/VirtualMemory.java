package javautils.repo;

import java.io.RandomAccessFile;

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
