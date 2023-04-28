package ch.epfl.javions;

import java.util.Arrays;
import java.util.HexFormat;
import java.util.Objects;

import static ch.epfl.javions.Preconditions.checkArgument;

/**
 * Instanciable class for immutable byte strings
 *
 * @author Eddy Rashed (360667)
 * @author Oskar Zanota (361595)
 */
public final class ByteString {
    private final byte[] bytes;
    private static final HexFormat parser = HexFormat.of().withUpperCase();

    /**
     * Constructs a ByteString from a byte array
     *
     * @param bytes the byte array to be used
     */
    public ByteString(byte[] bytes) {
        this.bytes = bytes.clone();
    }

    /**
     * Returns the size of the ByteString
     *
     * @return the size of the ByteString
     */
    public int size() {
        return bytes.length;
    }

    /**
     * Returns the byte at the given index
     *
     * @param index the index of the byte to be returned
     * @return the byte at the given index
     */
    public int byteAt(int index) {
        return Byte.toUnsignedInt(bytes[index]);
    }

    /**
     * Returns a new ByteString formed from the given hexadecimal string
     *
     * @param hexString the hexadecimal string to be used
     * @return a new ByteString formed from the given hexadecimal string
     * @throws NumberFormatException if the hexadecimal string is invalid
     */
    public static ByteString ofHexadecimalString(String hexString) {
        return new ByteString(parser.parseHex(hexString));
    }

    /**
     * Returns a long representing the bytes in the given range
     *
     * @param fromIndex the index of the first byte to be used
     * @param toIndex   the index of the last byte to be used
     * @return a long representing the bytes in the given range
     * @throws IndexOutOfBoundsException if the range [fromIndex, toIndex] is not included in [0, size()) or if the range is larger than 8 bytes
     */
    public long bytesInRange(int fromIndex, int toIndex) {
        Objects.checkFromToIndex(fromIndex, toIndex, size());
        checkArgument(toIndex - fromIndex < Long.BYTES);
        long result = 0;
        for (int i = fromIndex; i < toIndex; i++) {
            result = result << Byte.SIZE | byteAt(i);
        }
        return result;
    }

    /**
     * Overridden equals method
     *
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
     *
     * @return the hash code of this ByteString
     */
    @Override
    public int hashCode() {
        return Arrays.hashCode(bytes);
    }

    /**
     * Overridden toString method
     *
     * @return the hexadecimal representation of this ByteString
     */
    @Override
    public String toString() {
        return parser.formatHex(bytes);
    }
}