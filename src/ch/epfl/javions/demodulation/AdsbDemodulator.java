package ch.epfl.javions.demodulation;

import ch.epfl.javions.adsb.RawMessage;

import java.io.IOException;
import java.io.InputStream;

public class AdsbDemodulator {
    private final PowerWindow window;
    private final byte[] byteMessage = new byte[14];
    private int previous = 0;
    private long time = 0;

    public AdsbDemodulator(InputStream samplesStream) throws IOException {
        this.window = new PowerWindow(samplesStream, 1200);
    }
    public RawMessage nextMessage() throws IOException {
        int high;
        int low;
        int next;
        while (window.isFull()) {
            high = window.get(0) + window.get(10) + window.get(35) + window.get(45);
            low = window.get(5) + window.get(15) + window.get(20) + window.get(25) + window.get(30) + window.get(40);

            next = window.get(1) + window.get(11) + window.get(36) + window.get(46);

            if (high > previous && high > next && high >= 2 * low) {
                byte temp = 0;
                boolean correctType = true;
                for (int i = 0; i < 112; i++) {
                    temp = (byte) ((temp << 1) + (window.get(80 + 10 * i) < window.get(85 + 10 * i) ? (byte) 0 : (byte) 1));
                    if (i == 4 && temp != 17) {
                        correctType = false;
                        break;
                    }
                    if (i % 8 == 7) {
                        byteMessage[i / 8] = temp;
                        temp = 0;
                    }
                }
                if (correctType) {
                    RawMessage message = RawMessage.of(time, byteMessage);
                    if (message != null) {
                        window.advanceBy(1200);
                        time += 120_000;
                        return message;
                    }
                }
            }
            previous = high;
            time += 100;
            window.advance();
        }
        return null;
    }
}
