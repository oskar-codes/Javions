package ch.epfl.javions.demodulation;

import java.io.IOException;
import java.io.InputStream;

import static ch.epfl.javions.Preconditions.checkArgument;

public class PowerWindow {
    private static final int BATCH_SIZE = (int)Math.pow(2, 16);
    private final int windowSize;
    private long position = 0;
    private final PowerComputer computer;
    private final int[] windowA;
    private final int[] windowB;

    public PowerWindow(InputStream stream, int windowSize) throws IOException {
        checkArgument(windowSize > 0 && windowSize <= BATCH_SIZE);

        this.windowSize = windowSize;
        this.windowA = new int[windowSize];
        this.windowB = new int[windowSize];

        this.computer = new PowerComputer(stream, BATCH_SIZE);

        computer.readBatch(windowA);
    }

    public int size() {
        return windowSize;
    }
    public long position() {
        return position;
    }
    public boolean isFull() {
        return true; // TODO
    }

    //                                 windowSize
    //                      <---------------------------->
    // 0                position    2^16        i                 2^17
    // |                    |        |          |                  |
    //                      [----------------------------]
    // [----------------------------][----------------------------]
    //            windowA                       windowB
    public int get(int i) {
        if (i < 0 || i >= windowSize) throw new IndexOutOfBoundsException();

        if (position + i > BATCH_SIZE) return windowB[(int) (i - (BATCH_SIZE - position))];
        return windowA[(int) (position + i)];
    }
    //                                             windowSize
    //                                      <---------------------->
    // 0                            2^16 position                 2^17
    // |                             |      |                      |
    //                                     [----------------------]
    // [----------------------------][----------------------------]
    //            windowA                       windowB
    public void advance() throws IOException {
        position++;
        if (position + windowSize >= 2L * BATCH_SIZE) {
            position -= BATCH_SIZE;
            System.arraycopy(windowB, 0, windowA, 0, BATCH_SIZE);
            computer.readBatch(windowB);
        }
    }
    public void advanceBy(int offset) throws IOException {
        checkArgument(offset >= 0);

        for (int i = 0; i < offset; i++) {
            advance();
        }
    }
}
