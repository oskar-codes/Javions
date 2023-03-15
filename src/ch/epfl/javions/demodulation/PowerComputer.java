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
    private final InputStream stream;
    private final int batchSize;
    private final short[] saved = new short[8];
    private final short[] result;

    /**
     * Constructor for the PowerComputer class
     * @param stream - the stream of bytes to read from
     * @param batchSize - the number of samples to read at once
     */
    public PowerComputer(InputStream stream, int batchSize) {
        checkArgument(batchSize % 8 == 0 && batchSize > 0);
        this.stream = stream;
        this.batchSize = batchSize;
        this.result = new short[batchSize * 2];
    }

    /**
     * Returns the value at the given index of the given array, or 0 if the index is out of bounds. If the index is negative, returns values from the saved array.
     * @param arr - the array to get the value from
     * @param index - the index of the value to get
     * @return the value at the given index of the given array, or 0 if the index is out of bounds
     */
    private short get(short[] arr, int index) {
        if (index < -8) return 0;
        if (index < 0) return saved[index + 8];
        return arr[index];
    }

    /**
     * Reads a batch of samples from the stream and computes the power of each sample.
     * @param batch - the array to store the power of each sample in
     * @return the number of samples read
     * @throws IOException if an I/O error occurs
     */
    public int readBatch(int[] batch) throws IOException {
        checkArgument(batch.length == batchSize);

        SamplesDecoder decoder = new SamplesDecoder(stream, batchSize * 2);

        int read = decoder.readBatch(result);

        int affected = read / 2;

        for (int i = 1; i < read; i+=2) {
            batch[(i - 1) / 2] = (int) (Math.pow(get(result, i - 6) - get(result, i - 4) + get(result, i - 2) - get(result, i), 2) +
                                Math.pow(get(result, i - 7) - get(result, i - 5) + get(result, i - 3) - get(result, i - 1), 2));
        }

        for (int i = affected; i < batchSize; i++) {
            batch[i] = -1;
        }

        int start = Math.max(read - 8, 0);
        int count = read - start;

        for (int i = 0; i < 8; i++) {
            if (i - count >= 0) saved[i - count] = saved[i];
        }
        if (read - start >= 0) System.arraycopy(result, start, saved, 8 - count, count);

        return affected;
    }
}