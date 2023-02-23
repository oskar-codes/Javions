package ch.epfl.javions;

import java.util.Arrays;
import java.util.HexFormat;
import java.util.Objects;

/**
 * Instanciable class for immutable byte strings
 * @author Eddy Rashed (360667)
 * @author Oskar Zanota (361595)
 */
public final class ByteString {
    private final byte[] bytes;

    /**
     * Constructs a ByteString from a byte array
     * @param bytes the byte array to be used
     */
    public ByteString(byte[] bytes) {
        this.bytes = bytes.clone();
    }

    /**
     * Returns the size of the ByteString
     * @return the size of the ByteString
     */
    public int size() {
        return bytes.length;
    }

    /**
     * Returns the byte at the given index
     * @param index the index of the byte to be returned
     * @return the byte at the given index
     */
    public int byteAt(int index) {
        Objects.checkIndex(index, size());
        return bytes[index] & 0xFF;
    }

    /**
     * Returns a new ByteString formed from the given hexadecimal string
     * @param hexString the hexadecimal string to be used
     * @return a new ByteString formed from the given hexadecimal string
     */
    public static ByteString ofHexadecimalString(String hexString) {
        if (hexString.length() % 2 != 0) {
            throw new NumberFormatException("Hexadecimal string must have an even number of characters");
        }
        if (!hexString.matches("[0-9a-fA-F]+")) {
            throw new NumberFormatException("Hexadecimal string must contain only hexadecimal characters");
        }
        return new ByteString(HexFormat.of().withUpperCase().parseHex(hexString));
    }

    /**
     * Returns a long representing the bytes in the given range
     * @param fromIndex the index of the first byte to be used
     * @param toIndex the index of the last byte to be used
     * @return a long representing the bytes in the given range
     */
    public long bytesInRange(int fromIndex, int toIndex) {
        Objects.checkFromToIndex(fromIndex, toIndex, size());
        if (toIndex - fromIndex > Long.BYTES) {
            throw new IllegalArgumentException("Range must not be longer than " + Long.BYTES + " bytes");
        }
        long result = 0;
        for (int i = fromIndex; i < toIndex; i++) {
            result = result << Byte.SIZE | byteAt(i);
        }
        return result;
    }

    /**
     * Overridden equals method
     * @param other the object to be compared to
     * @return true if the given object is a ByteString and has the same bytes as this ByteString
     */
    @Override
    public boolean equals(Object other) {
        if (!(other instanceof ByteString)) return false;
        return Arrays.equals(((ByteString) other).bytes, bytes);
    }

    /**
     * Overridden hashCode method
     * @return the hash code of this ByteString
     */
    @Override
    public int hashCode() {
        return Arrays.hashCode(bytes);
    }

    /**
     * Overridden toString method
     * @return the hexadecimal representation of this ByteString
     */
    @Override
    public String toString() {
        return HexFormat.of().withUpperCase().formatHex(bytes);
    }
}
