package ch.epfl.javions.demodulation;

import java.io.IOException;
import java.io.InputStream;

import static ch.epfl.javions.Preconditions.checkArgument;

/**
 * Class that given a stream of bytes and a batch size, computes the power of the samples.
 * @author Oskar Zanota (361595)
 * @author Eddy Rashed (360667)
 */
public class PowerComputer {
    private final int batchSize;
    private final short[] saved = new short[8];
    private final short[] result;
    private final SamplesDecoder decoder;

    /**
     * Constructor for the PowerComputer class
     * @param stream - the stream of bytes to read from
     * @param batchSize - the number of samples to read at once
     */
    public PowerComputer(InputStream stream, int batchSize) {
        checkArgument(batchSize % 8 == 0 && batchSize > 0);
        this.batchSize = batchSize;
        this.result = new short[batchSize * 2];
        this.decoder = new SamplesDecoder(stream, batchSize * 2);
    }

    /**
     * Returns the value at the given index of the result array. If the index is negative, returns values from the saved array. If the index is out of bounds, returns 0.
     * @param index - the index of the value to get
     * @return the value at the given index
     */
    private short get(int index) {
        if (index < -8) return 0;
        if (index < 0) return saved[index + 8];
        return result[index];
    }

    /**
     * Reads a batch of samples from the stream and computes the power of each sample.
     * @param batch - the array to store the power of each sample in
     * @return the number of samples read
     * @throws IOException if an I/O error occurs
     */
    public int readBatch(int[] batch) throws IOException {
        checkArgument(batch.length == batchSize);

        int read = decoder.readBatch(result);

        int affected = read / 2;

        // Compute the power of each sample
        for (int i = 1; i < read; i+=2) {
            batch[(i - 1) / 2] = (int) (Math.pow(get(i - 6) - get(i - 4) + get(i - 2) - get(i), 2) +
                                Math.pow(get(i - 7) - get(i - 5) + get(i - 3) - get(i - 1), 2));
        }

        // Fill the rest of the batch with -1
        for (int i = affected; i < batchSize; i++) {
            batch[i] = -1;
        }

        int start = Math.max(read - 8, 0);
        int count = read - start;

        // Save the last 8 samples for the next batch
        if (read - start >= 0) System.arraycopy(result, start, saved, 8 - count, count);

        return affected;
    }
}