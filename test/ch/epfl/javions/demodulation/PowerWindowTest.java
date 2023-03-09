package ch.epfl.javions.demodulation;

import org.junit.jupiter.api.Test;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

class PowerWindowTest {
    @Test
    void mainTest() throws IOException {
        DataInputStream stream = new DataInputStream(
                new BufferedInputStream(
                        new FileInputStream("resources/samples.bin")));
        PowerWindow window = new PowerWindow(stream, 8);

        assertEquals(73, window.get(0));
        assertEquals(292, window.get(1));

        window.advance();
        assertEquals(292, window.get(0));

        window.advanceBy(3);
        assertEquals(4226, window.get(1));
    }
}