package ch.epfl.javions.demodulation;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import static ch.epfl.javions.Preconditions.checkArgument;

/**
 * A class that decodes the samples from the input stream.
 * @author Oskar Zanota (361595)
 * @author Eddy Rashed (360667)
 */
public final class SamplesDecoder {
    /**
     * The offset of the samples.
     */
    private static final int OFFSET = 2048;
    private final InputStream stream;
    private final int batchSize;
    private final byte[] bytes;

    /**
     * Constructs a new SamplesDecoder.
     * @param stream the input stream
     * @param batchSize the size of the batch
     */
    public SamplesDecoder(InputStream stream, int batchSize) {
        checkArgument(batchSize > 0);
        Objects.requireNonNull(stream);
        this.stream = stream;
        this.batchSize = batchSize;
        this.bytes = new byte[Short.BYTES * batchSize];
    }

    /**
     * Reads a batch of samples from the input stream and processes them.
     * @param batch the batch to be filled
     * @return the number of samples read
     * @throws IOException if an I/O error occurs
     */
    public int readBatch(short[] batch) throws IOException {
        checkArgument(batch.length == batchSize);

        int readBytes = stream.readNBytes(bytes, 0, batchSize * Short.BYTES) / 2;

        for (int i = 0; i < batch.length; i++ ) {
            short lsb = bytes[i * 2] ;
            short msb = (short)(bytes[i * 2 + 1] & 0xf);
            batch[i] = (short)((msb << Byte.SIZE)  + (lsb & 0xff) - OFFSET);
        }
        return readBytes;
    }
}
