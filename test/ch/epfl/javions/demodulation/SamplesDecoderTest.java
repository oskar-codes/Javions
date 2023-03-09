package ch.epfl.javions.demodulation;

import org.junit.jupiter.api.Test;

import java.io.*;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SamplesDecoderTest {
    @Test
    void testSampleDecoder() throws IOException {
        DataInputStream stream = new DataInputStream(
                new BufferedInputStream(
                        new FileInputStream("resources/samples.bin")));
        int size = 8;
        SamplesDecoder samplesDecoder = new SamplesDecoder(stream, size);
        short[] batch = new short[size];

        int read = samplesDecoder.readBatch(batch);
        assertArrayEquals(new short[]{-3, 8, -9, -8, -5, -8, -12, -16}, batch);
        assertEquals(size, read);
    }
}