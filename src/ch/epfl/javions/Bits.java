package ch.epfl.javions;

import java.util.Objects;

/**
 * Utility class for bit operations
 * @author Eddy Rashed (360667)
 * @author Oskar Zanota (361595)
 */
public class Bits {
    private Bits() {}

    /**
     * Extracts a bit field from a long value
     * @param value the long value to extract from
     * @param start the index of the first bit to extract
     * @param size the number of bits to extract
     * @return the extracted bit field
     * @throws IllegalArgumentException if size is not in the range [1, 31]
     * @throws IndexOutOfBoundsException if the range [start, size] is not included in [0, 63]
     */
    public static int extractUInt(long value, int start, int size) {
        // checkIndex throws IndexOutOfBoundsException, but we want an IllegalArgumentException so we catch and immediately throw
        try {
            Objects.checkIndex(size, Integer.SIZE);
        } catch (IndexOutOfBoundsException e) {
            throw new IllegalArgumentException("size must be strictly less than " + Integer.SIZE);
        }
        Objects.checkFromIndexSize(start, size, Long.SIZE);
        if (size == 0) throw new IllegalArgumentException("size must be greater than 0");

        return (int)((value << (Long.SIZE - size - start)) >>> (Long.SIZE - size));
    }

    /**
     * Checks if a bit is set to 1 in a long value at a given index
     * @param value the long value to check
     * @param index the index of the bit in the long value
     * @return true if the bit is set to 1, false otherwise
     * @throws IndexOutOfBoundsException if the index is not in the range [0, 63]
     */
    public static boolean testBit(long value, int index) {
        Objects.checkIndex(index, Long.SIZE);
        return extractUInt(value, index, 1) == 1;
    }
}