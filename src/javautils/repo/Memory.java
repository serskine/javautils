package javautils.repo;

import java.io.Serializable;
import java.util.Iterator;

public interface Memory extends Iterable<Memory.Node> {

    Repo getRepo();

    default long addrHeader() {
        return 0L;
    }

    default long addrFirstNode() {
        return addrHeader() + Header.BYTES;
    }

    default Header readHeader() {
        final Header header = new Header();
        header.capacity = getRepo().readLongAt(addrHeader());
        header.bytesUsed = getRepo().readLongAt(addrHeader() + Long.BYTES);
        return header;
    }

    default void writeHeader(final Header header) {
        getRepo().writeLongAt(addrHeader(), header.capacity);
        getRepo().writeLongAt(addrHeader() + Long.BYTES, header.bytesUsed);
    }

    class Header implements Serializable {
        public static final int BYTES = Long.BYTES * 4; // capacity, bytesUsed
        private static final long serialVersionUID = 1L;
        public long capacity, bytesUsed;

        public final long bytesFree() {
            return capacity - bytesUsed;
        }
    }
    class Node implements Serializable {
        public static final int BYTES = Long.BYTES * 4 + 1; // next, prev, size, isFree
        private static final long serialVersionUID = 1L;
        public long addr, next, prev, size;
        boolean isFree;

        public Node(long addr) {
            this.addr = addr;
        }
    }

    default Node readNode() {
        return readNodeAt(getRepo().position());
    }

    default Node readNodeAt(final long nodeAddress) {
        final Node node = new Node(nodeAddress);

        node.addr = getRepo().readLongAt(nodeAddress + Long.BYTES * 0);
        node.prev = getRepo().readLongAt(nodeAddress + Long.BYTES * 1);
        node.next = getRepo().readLongAt(nodeAddress + Long.BYTES * 2);
        node.size = getRepo().readLongAt(nodeAddress + Long.BYTES * 3);
        node.isFree = getRepo().readBooleanAt(nodeAddress + Long.BYTES * 4);

        return node;
    }

    default void writeNodeAt(final long nodeAddress, final Node node) {
        if (node.addr != nodeAddress) {
            throw new IllegalArgumentException("Node address mismatch: expected " + nodeAddress + ", but found " + node.addr);
        }
        getRepo().writeLongAt(nodeAddress + Long.BYTES * 0, node.addr);
        getRepo().writeLongAt(nodeAddress + Long.BYTES * 1, node.prev);
        getRepo().writeLongAt(nodeAddress + Long.BYTES * 2, node.next);
        getRepo().writeLongAt(nodeAddress + Long.BYTES * 3, node.size);
        getRepo().writeBooleanAt(nodeAddress + Long.BYTES * 4, node.isFree);
    }

    default boolean isInitialized() {
        try {
            readHeader();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    default void init(final long capacity) {
        final Header header = new Header();
        header.capacity = capacity;
        header.bytesUsed = Header.BYTES + Node.BYTES;

        writeHeader(header);

        final Node firstNode = new Node(addrFirstNode());
        firstNode.next = addrFirstNode();
        firstNode.prev = addrFirstNode();
        firstNode.isFree = true;
        firstNode.size = header.bytesFree();
        writeNodeAt(addrFirstNode(), firstNode);
    }

    default long addrOfNode(final long infoAddress) {
        return infoAddress - Node.BYTES;
    }

    default long addrOfInfo(final long nodeAddress) {
        return nodeAddress + Node.BYTES;
    }

    default long allocate(final long size) {
        final Header header = readHeader();
        if (size > header.bytesFree()) {
            throw new OutOfMemoryError("Not enough memory to allocate " + size + " bytes");
        }

        getRepo().seek(addrFirstNode());
        do {
            long oldNodePos = getRepo().position();
            final Node oldNode = readNode();

            final long newBytesRequired = Node.BYTES + size;
            if (oldNode.isFree && oldNode.size >= newBytesRequired) {
                final long newNodePos = oldNodePos + Node.BYTES + size;

                final Node newNode = new Node(newNodePos);
                newNode.isFree = true;
                newNode.prev = oldNodePos;
                newNode.next = oldNode.next;
                newNode.size = oldNode.size - Node.BYTES;

                oldNode.size = size;
                oldNode.isFree = false;
                oldNode.next = newNodePos;

                writeNodeAt(oldNodePos, oldNode);
                writeNodeAt(newNodePos, newNode);

                header.bytesUsed += size + newBytesRequired;
                writeHeader(header);

                return addrOfInfo(oldNodePos);
            }
            getRepo().seek(oldNode.next);
        } while(getRepo().position() != addrFirstNode());
        throw new OutOfMemoryError("There is no free chunk available to allocate " + size + " bytes. Defragmentation is required.");
    }

    default void verifyInfoAddress(final long infoAddress) {
        try {
            final long expectedAddress = addrOfNode(infoAddress);
            final Node node = readNodeAt(expectedAddress);
            assert node.addr == expectedAddress;
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid info address: " + infoAddress, e);
        }
    }

    default void verifyInfoAddressHasCapacity(final long infoAddress, final long requiredCapacity) {
        try {
            final long expectedAddress = addrOfNode(infoAddress);
            final Node node = readNodeAt(expectedAddress);
            assert node.addr == expectedAddress;
            assert node.size >= requiredCapacity;
        } catch (Exception e) {
            throw new IllegalArgumentException("Allocated Memory does not meet Criteria: " + infoAddress, e);
        }
    }

    default void free(final long infoAddress) {
        verifyInfoAddress(infoAddress);
        long nodeAddress = addrOfNode(infoAddress);
        final Node node = readNodeAt(nodeAddress);
        if (node.isFree) {
            return; // Already free, nothing to do
        }
        node.isFree = true;
        writeNodeAt(nodeAddress, node);

        final Header header = readHeader();
        header.bytesUsed -= (node.size + Node.BYTES);
        writeHeader(header);

        mergeWithPreviousWhileFree(nodeAddress);
    }

    default void mergeWithPreviousWhileFree(final long infoAddress) {
        final long nodeAddress = addrOfNode(infoAddress);
        Node node = readNodeAt(nodeAddress);
        boolean done = false;
        while(!done) {
            if (node.prev < nodeAddress) {
                final Node prevNode = readNodeAt(node.prev);
                if (prevNode.isFree) {
                    prevNode.size += node.size + Node.BYTES;
                    prevNode.next = node.next;

                    writeNodeAt(node.prev, prevNode);

                    final Node nextNode = readNodeAt(node.next);
                    nextNode.prev = node.prev;
                    writeNodeAt(node.next, nextNode);

                    node = prevNode;
                } else {
                    done = true;
                }
            } else {
                done = true;
            }
        }
    }

    default Iterator<Node> iterator() {

        return new Iterator<Node>() {
            private Node currentNode = readNodeAt(addrFirstNode());
            private boolean done = false;

            @Override
            public boolean hasNext() {
                return !done;
            }

            @Override
            public Node next() {
                final Node prevNode = currentNode;
                currentNode = readNodeAt(prevNode.next);
                done = prevNode.next == addrFirstNode();
                return prevNode;
            }
        };
    }

    default String describe() {
        final StringBuilder sb = new StringBuilder();
        final Memory.Header header = readHeader();
        sb.append("Used memory of type: " + getClass().getSimpleName() + "\n");
        sb.append("capacity  : " + header.capacity + "\n");
        sb.append("bytesUsed : " + header.bytesUsed + "\n");
        sb.append("bytesFree : " + header.bytesFree() + "\n");

        sb.append("==========================================================================\n");
        int i=0;
        long addr = addrFirstNode();
        for(Iterator<Memory.Node> it = iterator(); it.hasNext(); ) {
            final Memory.Node node = it.next();
            sb.append("[" + i + "]: {\n");
            sb.append("   isFree: " + node.isFree + "\n");
            sb.append("   addr:   " + String.format("0x%08X", addr) + "\n");
            sb.append("   next:   " + String.format("0x%08X", node.next) + "\n");
            sb.append("   prev:   " + String.format("0x%08X", node.prev) + "\n");
            sb.append("   size:   " + node.size + "\n");
            sb.append("}\n");
            addr = node.next;
        }
        return sb.toString();
    }

    default boolean isFree(final long infoAddress) {
        verifyInfoAddress(infoAddress);
        final long nodeAddress = addrOfNode(infoAddress);
        final Node node = readNodeAt(nodeAddress);
        return node.isFree;
    }

    default void verifyWriteAllowed(final long infoAddress) {
        try {
            final long expectedAddress = addrOfNode(infoAddress);
            final Node node = readNodeAt(expectedAddress);
            assert node.addr == expectedAddress;
            assert !node.isFree;    // It is allocated so we are allowed to write to the address
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid info address: " + infoAddress, e);
        }
    }


    default byte[] readDataAt(final long infoAddress) {
        verifyInfoAddress(infoAddress);
        getRepo().seek(infoAddress);
        return getRepo().readBytesAt(infoAddress);
    }

    default void writeDataAt(final long infoAddress, final byte[] data) {
        verifyWriteAllowed(infoAddress);
        getRepo().seek(infoAddress);
        getRepo().writeBytesAt(infoAddress, data);
    }

    default <T extends Serializable> T readFrom(final long infoAddress, final Class<T> clazz) {
        return getRepo().readObjectAt(infoAddress, clazz);
    }

    default <T extends Serializable> void writeTo(final long infoAddress, final T object) {
        getRepo().writeObjectAt(infoAddress, object);
    }
}
