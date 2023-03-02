package ch.epfl.javions;

public final class Crc24 {
    public static final int GENERATOR = 0xFFF409; // 1111_1111_1111_0100_0000_1001
    private final int generator;
    private final int[] table = new int[256];
    public Crc24(int generator) {
        this.generator = generator;
        buildTable();
    }

    private void buildTable() {
        for (int i = 0; i < 256; i++) {
            table[i] = crc_bitwise(new byte[]{(byte)i});
        }
    }

    private int crc_bitwise(byte[] message) {
        int[] table = new int[]{0, generator & 0xffffff};
        int crc = 0;
        byte[] augmented = new byte[message.length + 3];
        for (int i = 0; i < message.length; i++) {
            augmented[i] = message[i];
        }
        for (int i = 0; i < 3; i++) {
            augmented[message.length + i] = 0;
        }
        for (byte b : augmented) {
            for (int i = 7; i >= 0; i--) {
                int bit = (Byte.toUnsignedInt(b) >> i) & 1;
                crc = ((crc << 1) | bit) ^ table[(crc >>> 23) & 1];
            }
        }
        return crc & 0xffffff;
    }

    public int crc(byte[] message) {
        byte[] augmented = new byte[message.length + 3];
        for (int i = 0; i < message.length; i++) {
            augmented[i] = message[i];
        }
        for (int i = 0; i < 3; i++) {
            augmented[message.length + i] = 0;
        }
        int crc = 0;
        for (byte o : augmented) {
            crc = ((crc << 8) | Byte.toUnsignedInt(o)) ^ table[(crc >>> 16) & 0xff];
        }
        return crc & 0xffffff;
    }

}