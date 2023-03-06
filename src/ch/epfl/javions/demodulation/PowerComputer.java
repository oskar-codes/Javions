package ch.epfl.javions.demodulation;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLOutput;
import java.util.Arrays;

import static ch.epfl.javions.Preconditions.checkArgument;

public class PowerComputer {
    private final InputStream stream;
    private final int batchSize;
    private int[] array = new int[8];
    public PowerComputer(InputStream stream, int batchSize) {
        checkArgument(batchSize % 8 == 0 && batchSize > 0);
        this.stream = stream;
        this.batchSize = batchSize;
    }

    private short get(short[] arr, int index) {
        return index < 0 || index >= arr.length ? 0 : arr[index];
    }

    public int readBatch(int[] batch) throws IOException {
        checkArgument(batch.length == batchSize);

        SamplesDecoder decoder = new SamplesDecoder(stream, batchSize);

        short[] result = new short[batchSize];
        int read = decoder.readBatch(result);

        System.out.println(Arrays.toString(result));

        for (int i = 1; i < read; i+=2) {

            System.out.println("####### " + i/2 + " #######");
            System.out.println(get(result, i - 6) + " - " + get(result, i - 4) + " + " + get(result, i - 2) + " - " + get(result, i));
            System.out.println(get(result, i - 7) + " - " + get(result, i - 5) + " + " + get(result, i - 3) + " - " + get(result, i-1));


            batch[(i - 1)/2] = (int) (Math.pow(get(result, i - 6) - get(result, i - 4) + get(result, i - 2) - get(result, i), 2) +
                                Math.pow(get(result, i - 7) - get(result, i - 5) + get(result, i - 3) - get(result, i - 1), 2));
        }

        return read;
    }
}
