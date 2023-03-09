package ch.epfl.javions.demodulation;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

class PowerComputerTest {

    @Test
    void testPowerComputer() throws IOException {
        DataInputStream stream = new DataInputStream(
                new BufferedInputStream(
                        new FileInputStream("resources/samples.bin")));
        int size = 8;
        PowerComputer computer = new PowerComputer(stream, size);
        int[] batch = new int[size];

        computer.readBatch(batch);
        assertArrayEquals(new int[]{73, 292, 65, 745, 98, 4226, 12244, 25722}, batch);

        computer.readBatch(batch);
        assertArrayEquals(new int[]{36818, 23825, 10730, 1657, 1285, 1280, 394, 521}, batch);
    }
}