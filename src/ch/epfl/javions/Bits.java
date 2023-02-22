package ch.epfl.javions;

import java.util.Objects;

public class Bits {
    private Bits() {};

    // TODO: illegalargument
    public static int extractUInt(long value, int start, int size) {
        Objects.checkIndex(size, Integer.SIZE);
        Objects.checkFromIndexSize(start, size, Long.SIZE);

        return (int)((value << (Long.SIZE - size - start)) >>> (Long.SIZE - size));
    }
x
    public static boolean testBit(long value, int index) {
        Objects.checkIndex(index, Long.SIZE);
        return extractUInt(value, start, 1) == 1;
    }
}