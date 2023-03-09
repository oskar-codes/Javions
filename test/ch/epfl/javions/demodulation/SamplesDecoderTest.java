package ch.epfl.javions.demodulation;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.util.Arrays;

class SamplesDecoderTest {
    @Test
    void testSampleDecoder() throws IOException {
        DataInputStream stream = new DataInputStream(
                new BufferedInputStream(
                        new FileInputStream(new File("resources/samples.bin"))));
        int size = 8;
        SamplesDecoder samplesDecoder = new SamplesDecoder(stream, size);
        short[] batch = new short[size];

        samplesDecoder.readBatch(batch);
        assertArrayEquals(new short[]{-3, 8, -9, -8, -5, -8, -12, -16}, batch);
    }
}