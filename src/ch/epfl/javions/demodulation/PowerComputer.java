package ch.epfl.javions.demodulation;

import java.io.IOException;
import java.io.InputStream;

import static ch.epfl.javions.Preconditions.checkArgument;

/**
 * Class that given a stream of bytes and a batch size, computes the power of the samples.
 *
 * @author Oskar Zanota (361595)
 * @author Eddy Rashed (360667)
 */
public class PowerComputer {
    private final int batchSize;
    private final short[] saved;
    private final short[] result;
    private final SamplesDecoder decoder;
    private final static int SAVED_SIZE = 8;

    /**
     * Constructor for the PowerComputer class
     *
     * @param stream    the stream of bytes to read from
     * @param batchSize the number of samples to read at once
     */
    public PowerComputer(InputStream stream, int batchSize) {
        checkArgument(batchSize % 8 == 0 && batchSize > 0);
        this.batchSize = batchSize;
        this.result = new short[batchSize * Short.BYTES];
        this.decoder = new SamplesDecoder(stream, batchSize * Short.BYTES);
        this.saved = new short[SAVED_SIZE];
    }

    /**
     * Reads a batch of samples from the stream and computes the power of each sample.
     *
     * @param batch the array to store the power of each sample in
     * @return the number of samples read
     * @throws IOException if an I/O error occurs
     */
    public int readBatch(int[] batch) throws IOException {
        checkArgument(batch.length == batchSize);

        int read = decoder.readBatch(result);

        // Compute the power of each sample
        for (int i = 1; i < read; i += 2) {
            int a = get(i - 6) - get(i - 4) + get(i - 2) - get(i);
            int b = get(i - 7) - get(i - 5) + get(i - 3) - get(i - 1);
            batch[(i - 1) / 2] = a * a + b * b;
        }

        int start = Math.max(read - 8, 0);
        int count = read - start;

        // TODO: avoid using System.arraycopy
        // Save the last 8 samples for the next batch
        if (read - start >= 0) System.arraycopy(result, start, saved, SAVED_SIZE - count, count);

        return read / 2;
    }

    /**
     * Returns the value at the given index of the result array. If the index is negative, returns values from the saved array. If the index is out of bounds, returns 0.
     *
     * @param index the index of the value to get
     * @return the value at the given index
     */
    private short get(int index) {
        if (index < -SAVED_SIZE) return 0;
        if (index < 0) return saved[index + SAVED_SIZE];
        return result[index];
    }
}