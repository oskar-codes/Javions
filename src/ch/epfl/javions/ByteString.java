package ch.epfl.javions;

import java.util.Objects;

public final class ByteString {
    private final byte[] bytes;
    public ByteString(byte[] bytes) {
        this.bytes = bytes;
    }

    public int size() {
        return bytes.length;
    }

    public int byteAt(int index) {
        Objects.checkIndex(index, size());
        return bytes[index];
    }

    public long bytesInRange(int fromIndex, int toIndex) {
        return 1L;
    }
}
