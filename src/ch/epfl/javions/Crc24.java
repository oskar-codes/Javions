package ch.epfl.javions;

public final class Crc24 {
    public static final int GENERATOR = 0xFFF409; // 1111_1111_1111_0100_0000_1001
    private final int generator;
    public Crc24(int generator) {
        this.generator = generator;
    }
    public Crc24() {
        this(GENERATOR);
    }
    public int crc_a(long message) {
        int crc = 0;
        long augmented = message << 24;
        int size = (int) Math.floor(Math.log(augmented) / Math.log(2));
        for (int i = size; i >= 0; i--) {
            int b = Bits.testBit(augmented, i) ? 1 : 0;
            crc = (crc << 1) | b;
            if (Bits.testBit(crc, 24)) crc ^= generator;
        }
        return crc & 0xffffff;
    }
    public int crc_b(int b) {
        int[] table = new int[]{0, generator};
        int crc = 0;
        for (int i = 0; i < 8; i++) {
            crc = ((crc << 1) | ((b >> (7 - i)) & 1)) ^ table[crc >> 23];
        }
        return crc & 0xffffff;
    }
    int crc(byte[] bytes) {
        int[] table = new int[256];
        for (int i = 0; i < 256; i++) {
            table[i] = crc_b(i);
        }
        int crc = 0;
        for (byte o : bytes) {
            crc = ((crc << 8) | o) ^ table[(crc >> 16) & 0xff];
        }
        return crc & 0xffffff;
    }

}
