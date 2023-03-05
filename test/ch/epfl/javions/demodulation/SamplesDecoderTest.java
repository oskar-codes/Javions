package ch.epfl.javions.demodulation;

import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HexFormat;

import static org.junit.jupiter.api.Assertions.*;

class SamplesDecoderTest {
    @Test
    void testSampleDecoder() throws IOException {
        DataInputStream stream = new DataInputStream(
                new BufferedInputStream(
                        new FileInputStream(new File("resources/samples.bin"))));
        SamplesDecoder samplesDecoder = new SamplesDecoder(stream, 20);
        short[] batch = new short[20];
        samplesDecoder.readBatch(batch);
        System.out.println(Arrays.toString(batch));
    }
}