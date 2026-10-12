package javautils.repo;

public class ActualMemory implements Memory {
    private MemoryRepo repo;

    public ActualMemory(final long capacity) {
        if (capacity > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Capacity exceeds maximum array size");
        }
        this.repo = new MemoryRepo((int) capacity);
    }

    @Override
    public Repo getRepo() {
        return this.repo;
    }

    @Override
    public void init(final long capacity) {
        this.repo = new MemoryRepo((int) capacity);

        final Memory.Header header = new Memory.Header();
        header.capacity = capacity;
        header.bytesUsed = getRepo().bytesUsedForArray(Memory.Header.BYTES) + getRepo().bytesUsedForArray(Memory.Node.BYTES);

        getRepo().writeObjectAt(addrHeader(), header);

        final Memory.Node firstNode = new Memory.Node(addrFirstNode());
        firstNode.next = addrFirstNode();
        firstNode.prev = addrFirstNode();
        firstNode.isFree = true;
        firstNode.size = header.bytesFree();
        getRepo().writeObjectAt(addrFirstNode(), firstNode);
    }
}
