package ch.epfl.javions.demodulation;

import java.io.IOException;
import java.io.InputStream;
import java.util.HexFormat;

import static ch.epfl.javions.Preconditions.checkArgument;

/**
 * A class that decodes the samples from the input stream.
 * @author Oskar Zanota (361595)
 * @author Eddy Rashed (360667)
 */
public final class SamplesDecoder {
    private final InputStream stream;
    private final int batchSize;
    private byte[] bytes;

    /**
     * Constructs a new SamplesDecoder.
     * @param stream - the input stream
     * @param batchSize - the size of the batch
     */
    public SamplesDecoder(InputStream stream, int batchSize) {
        checkArgument(batchSize > 0);
        if (stream == null) {
            throw new NullPointerException("The stream cannot be null");
        }
        this.stream = stream;
        this.batchSize = batchSize;
        this.bytes = new byte[batchSize * 2];
    }

    /**
     * Reads a batch of samples from the input stream and processes them.
     * @param batch - the batch to be filled
     * @return the number of samples read
     * @throws IOException if an I/O error occurs
     */
    public int readBatch(short[] batch) throws IOException {
        checkArgument(batch.length == batchSize);
        int read = 0;
        bytes = stream.readNBytes(Math.min(batchSize * 2, stream.available()));
        String str = HexFormat.of().formatHex(bytes);
        String[] split = str.split("(?<=\\G.{4})");
        int i = 0;
        for (String s : split) {
            int number = Integer.parseInt(s, 16);
            batch[i++] = (short) (((number & 0xff00) >>> 8) + ((number & 0x00ff) << 8) - 2048);
            ++read;
        }
        return read;
    }
}
