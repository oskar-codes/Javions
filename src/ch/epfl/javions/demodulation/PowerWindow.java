package ch.epfl.javions.demodulation;

import java.io.IOException;
import java.io.InputStream;

import static ch.epfl.javions.Preconditions.checkArgument;

/**
 * A window of a given size on a sequence of power samples calculated by PowerComputer, given a stream of bytes.
 *
 * @author Oskar Zanota (361595)
 * @author Eddy Rashed (360667)
 */
public class PowerWindow {
    public static final int BATCH_SIZE = (int) Math.pow(2, 16);
    private final int windowSize;
    private long position = 0;
    private long actualPosition = 0;
    private final PowerComputer computer;
    private final int[] windowA;
    private final int[] windowB;
    private boolean shouldFillB = true;
    private int lastAmountRead;

    /**
     * Creates a new PowerWindow of the given size, given a stream of bytes.
     *
     * @param stream     the stream of bytes
     * @param windowSize the size of the window
     * @throws IOException if an I/O error occurs
     */
    public PowerWindow(InputStream stream, int windowSize) throws IOException {
        checkArgument(windowSize > 0 && windowSize <= BATCH_SIZE);

        this.windowSize = windowSize;
        this.windowA = new int[BATCH_SIZE];
        this.windowB = new int[BATCH_SIZE];

        this.computer = new PowerComputer(stream, BATCH_SIZE);

        lastAmountRead = computer.readBatch(windowA);
    }

    /**
     * Returns the size of the window.
     *
     * @return the size of the window
     */
    public int size() {
        return windowSize;
    }

    /**
     * Returns the position of the window.
     *
     * @return the position of the window
     */
    public long position() {
        return actualPosition;
    }

    /**
     * Returns true if the window is full, false otherwise.
     *
     * @return true if the window is full, false otherwise
     */
    public boolean isFull() {
        return position + windowSize <= lastAmountRead + Math.scalb(1, 16);
    }

    //                              windowSize
    //                      <------------------------->
    // 0                position    2^16        i                 2^17
    // |                    |        |          |                  |
    //                      [------------------------]
    // [----------------------------][----------------------------]
    //            windowA                       windowB

    /**
     * Returns the power at the given index in the window.
     *
     * @param i the index
     * @return the power at the given index in the window
     */
    public int get(int i) {
        if (i < 0 || i >= windowSize) throw new IndexOutOfBoundsException();
        if (position + i >= BATCH_SIZE) return windowB[(int) (i - (BATCH_SIZE - position))];
        return windowA[(int) (position + i)];
    }

    /**
     * Advances the window by one.
     *
     * @throws IOException if an I/O error occurs
     */
    public void advance() throws IOException {
        position++;
        actualPosition++;

        // If the first window is full and the second one has never been filled, fill it.
        if (position + windowSize >= BATCH_SIZE && shouldFillB) {
            lastAmountRead = computer.readBatch(windowB);
            shouldFillB = false;
        }

        /// If the first window is full and the second one is full, shift the windows.
        if (position + windowSize >= 2L * BATCH_SIZE) {
            position -= BATCH_SIZE;
            System.arraycopy(windowB, 0, windowA, 0, BATCH_SIZE);
            lastAmountRead = computer.readBatch(windowB);
        }
    }

    /**
     * Advances the window by the given offset.
     *
     * @param offset the offset
     * @throws IOException if an I/O error occurs
     */
    public void advanceBy(int offset) throws IOException {
        checkArgument(offset > 0);
        for (int i = 0; i < offset; i++) {
            advance();
        }
    }
}