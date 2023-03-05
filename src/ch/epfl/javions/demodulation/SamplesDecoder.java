package ch.epfl.javions.demodulation;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HexFormat;

import static ch.epfl.javions.Preconditions.checkArgument;

public final class SamplesDecoder {
    private final InputStream stream;
    private final int batchSize;
    private byte[] buffer;

    public SamplesDecoder(InputStream stream, int batchSize) {
        checkArgument(batchSize > 0);
        if (stream == null) {
            throw new NullPointerException("The stream cannot be null");
        }
        this.stream = stream;
        this.batchSize = batchSize;
        this.buffer = new byte[batchSize * 2];
    }

    int readBatch2(short[] batch) throws IOException {
        checkArgument(batch.length == batchSize);
        int read = 0;
        byte[] bytes = stream.readNBytes(batchSize);
        //  [7F,D3,45,AE,3F]
        //  [7FD, 345, AE3]

        boolean even = false;
        for (int i = 0; i < batchSize; i++) {
            even = !even;
            if (even) {
                short firstByte = (short) (bytes[i] << 4);
                short secondByte = (short) (bytes[i+1] >>> 4);
                short sum = (short) (firstByte + secondByte);
                batch[i] = (short) ((sum << 8 | sum >>> 8) - 2048);
            } else {
                short firstByte = (short) (((bytes[i] << 4) >>> 4) << 8);
                short secondByte = bytes[i+1];
                short sum = (short)(firstByte + secondByte);
                batch[i] = (short) ((sum << 8 | sum >>> 8) - 2048);
                i++;
            }
        }
        return read;
    }

    int readBatch(short[] batch) throws IOException {
        checkArgument(batch.length == batchSize);
        int read = 0;
        byte[] bytes = stream.readNBytes(Math.min(batchSize, stream.available()));
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
