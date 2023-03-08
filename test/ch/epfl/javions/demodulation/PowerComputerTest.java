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
        int size = 16;
        PowerComputer computer = new PowerComputer(stream, size);
        int[] batch = new int[size];

        System.out.println("#### ITERATION 1 ####");
        computer.readBatch(batch);
        System.out.println(Arrays.toString(batch));

        System.out.println("\n#### ITERATION 2 ####");
        computer.readBatch(batch);
        System.out.println(Arrays.toString(batch));

        System.out.println("\n#### ITERATION 3 ####");
        computer.readBatch(batch);
        System.out.println(Arrays.toString(batch));
    }
}