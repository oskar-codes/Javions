package ch.epfl.javions.demodulation;

import java.io.IOException;
import java.io.InputStream;

import static ch.epfl.javions.Preconditions.checkArgument;

public class PowerComputer {
    private final InputStream stream;
    private final int batchSize;
    private final short[] saved = new short[8];
    public PowerComputer(InputStream stream, int batchSize) {
        checkArgument(batchSize % 8 == 0 && batchSize > 0);
        this.stream = stream;
        this.batchSize = batchSize;
    }

    private short get(short[] arr, int index) {
        if (index < -8) return 0;
        if (index < 0) return saved[index + 8];
        return arr[index];
    }

    public int readBatch(int[] batch) throws IOException {
        checkArgument(batch.length == batchSize);

        SamplesDecoder decoder = new SamplesDecoder(stream, batchSize * 2);

        short[] result = new short[batchSize * 2];
        int read = decoder.readBatch(result);

        int affected = 0;
        for (int i = 1; i < read; i+=2) {

//            System.out.println("####### " + i/2 + " #######");
//            System.out.println(get(result, i - 7) + " - " + get(result, i - 5) + " + " + get(result, i - 3) + " - " + get(result, i-1));
//            System.out.println(get(result, i - 6) + " - " + get(result, i - 4) + " + " + get(result, i - 2) + " - " + get(result, i));

            batch[(i - 1) / 2] = (int) (Math.pow(get(result, i - 6) - get(result, i - 4) + get(result, i - 2) - get(result, i), 2) +
                                Math.pow(get(result, i - 7) - get(result, i - 5) + get(result, i - 3) - get(result, i - 1), 2));
            affected++;
        }

        int start = Math.max(read - 8, 0);
        int count = read - start;

        for (int i = 0; i < 8; i++) {
            if (i - count >= 0) saved[i - count] = saved[i];
        }
        if (read - start >= 0) System.arraycopy(result, start, saved, 8 - count + start - start, read - start);

        return affected;
    }
}
