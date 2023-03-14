package ch.epfl.javions.demodulation;

import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PowerComputerTest {

    @Test
    void testPowerComputer() throws IOException {
        DataInputStream stream = new DataInputStream(
                new BufferedInputStream(
                        new FileInputStream("resources/samples.bin")));
        int size = 8;
        PowerComputer computer = new PowerComputer(stream, size);
        int[] batch = new int[size];
        int read;

        read = computer.readBatch(batch);
        assertArrayEquals(new int[]{73, 292, 65, 745, 98, 4226, 12244, 25722}, batch);
        assertEquals(size, read);

        read = computer.readBatch(batch);
        assertArrayEquals(new int[]{36818, 23825, 10730, 1657, 1285, 1280, 394, 521}, batch);
        assertEquals(size, read);
    }

    @Test
    void testPowerComputerRead() throws IOException {
        InputStream stream = new ByteArrayInputStream(new byte[]{0,1,2,3, 4,5,6,7,   0,1,2,3, 4,5,6,7,    0,1,2,3, 4,5,6,7,   0,1,2,3, 4,5,6,7,
                                                                 0,1,2,3, 4,5,6,7,   0,1,2,3, 4,5,6,7,    0,1,2,3, 4,5,6,7,   0,1,2,3, 4,5,6,7,
                                                                 1,2,3,4});
        int size = 8;
        PowerComputer computer = new PowerComputer(stream, size);
        int[] batch = new int[size];
        int read;
        read = computer.readBatch(batch);
        assertEquals(8, read);

        read = computer.readBatch(batch);
        assertEquals(8, read);

        read = computer.readBatch(batch);
        assertEquals(1, read);
    }

    static void print120() throws IOException {
        DataInputStream stream = new DataInputStream(
                new BufferedInputStream(
                        new FileInputStream("resources/samples.bin")));
        int size = 160;
        PowerComputer computer = new PowerComputer(stream, size);
        int[] batch = new int[size];

        computer.readBatch(batch);

        System.out.println(Arrays.toString(batch));
    }
}