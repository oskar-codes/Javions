package ch.epfl.javions.demodulation;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

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