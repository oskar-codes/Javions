package ch.epfl.javions;

import ch.epfl.javions.adsb.RawMessage;
import ch.epfl.javions.demodulation.AdsbDemodulator;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public final class PrintRawMessages {
    public static void main(String[] args) throws IOException {
        String f = "resources/samples_20230304_1442.bin";
        try (InputStream s = new FileInputStream(f)) {
            AdsbDemodulator d = new AdsbDemodulator(s);
            RawMessage m;
            int n = 0;
            while ((m = d.nextMessage()) != null) {
                System.out.println(m);
                n++;
            }
            System.out.println("Number of messages: " + n);
        }
    }
}