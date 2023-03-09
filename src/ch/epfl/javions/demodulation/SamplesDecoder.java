package ch.epfl.javions.demodulation;

import java.io.IOException;
import java.io.InputStream;
import java.util.HexFormat;

import static ch.epfl.javions.Preconditions.checkArgument;

public final class SamplesDecoder {
    private final InputStream stream;
    private final int batchSize;

    public SamplesDecoder(InputStream stream, int batchSize) {
        checkArgument(batchSize > 0);
        if (stream == null) {
            throw new NullPointerException("The stream cannot be null");
        }
        this.stream = stream;
        this.batchSize = batchSize;
    }

    public int readBatch(short[] batch) throws IOException {
        checkArgument(batch.length == batchSize);
        int read = 0;
        byte[] bytes = stream.readNBytes(Math.min(batchSize * 2, stream.available()));
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
