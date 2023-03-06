package ch.epfl.javions.demodulation;

import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class PowerComputerTest {

    @Test
    void testPowerComputer() throws IOException {
        DataInputStream stream = new DataInputStream(
                new BufferedInputStream(
                        new FileInputStream(new File("resources/samples.bin"))));
        int size = 120;
        PowerComputer computer = new PowerComputer(stream, size);
        int[] batch = new int[size];
        computer.readBatch(batch);

        System.out.println(Arrays.toString(batch));
    }
}