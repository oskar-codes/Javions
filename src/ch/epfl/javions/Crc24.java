package ch.epfl.javions;

/**
 * Instantiable class for CRC-24 calculations
 *
 * @author Eddy Rashed (360667)
 * @author Oskar Zanota (361595)
 */
public final class Crc24 {
    /**
     * The default generator used for the CRC-24 calculation
     */
    public static final int GENERATOR = 0xFFF409; // 1111_1111_1111_0100_0000_1001
    /**
     * The size of the lookup table
     */
    private static final int SIZE = 256;

    // Table for the crc method, generated with the crc_bitwise method in buildTable
    private final int[] table = new int[SIZE];

    /**
     * Constructor for the Crc24 class, generates the table for the crc method
     *
     * @param generator the generator to use for the CRC-24 calculation
     */
    public Crc24(int generator) {
        buildTable(table, generator);
    }

    /**
     * Builds the CRC lookup table for the crc method
     */
    private static void buildTable(int[] table, int generator) {
        for (int i = 0; i < SIZE; i++) {
            table[i] = crc_bitwise(new byte[]{(byte) i}, generator);
        }
    }

    /**
     * Calculates the CRC-24 of a byte array using a bitwise algorithm
     *
     * @param message the byte array to calculate the CRC-24 of
     * @return the CRC-24 of the byte array as an int
     */
    private static int crc_bitwise(byte[] message, int generator) {
        int crc = 0;

        int[] bitwiseTable = new int[]{0, Bits.extractUInt(generator, 0, 24)};
        
        // Calculates the augmented message, with three 0 bytes at the end
        byte[] augmented = new byte[message.length + 3];
        System.arraycopy(message, 0, augmented, 0, message.length);

        // Calculates the CRC-24 of the augmented message
        for (byte b : augmented) {
            for (int i = 7; i >= 0; i--) {
                int bit = (Byte.toUnsignedInt(b) >> i) & 1;
                crc = ((crc << 1) | bit) ^ bitwiseTable[(crc >>> 23) & 1];
            }
        }

        // Returns the 6 least significant bytes of the CRC-24
        return Bits.extractUInt(crc, 0, 24);
    }

    /**
     * Calculates the CRC-24 of a byte array using a lookup table
     *
     * @param message the byte array to calculate the CRC-24 of
     * @return the CRC-24 of the byte array as an int
     */
    public int crc(byte[] message) {
        // Calculates the augmented message, with three 0 bytes at the end
        byte[] augmented = new byte[message.length + 3];
        System.arraycopy(message, 0, augmented, 0, message.length);

        // Calculates the CRC-24 of the augmented message
        int crc = 0;
        for (byte o : augmented) {
            crc = ((crc << 8) | Byte.toUnsignedInt(o)) ^ table[(crc >>> 16) & 0xff];
        }

        // Returns the 6 least significant bytes of the CRC-24
        return Bits.extractUInt(crc, 0, 24);
    }

}