package ch.epfl.javions;

import ch.epfl.javions.adsb.RawMessage;
import ch.epfl.javions.demodulation.AdsbDemodulator;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

public final class PrintRawMessages {
    public static void main(String[] args) throws IOException {

        System.out.println("Started");
        Date start;

        int n = 0;

        String f = "resources/samples_20230304_1442.bin";
        try (InputStream s = new FileInputStream(f)) {
            start = new Date();

            AdsbDemodulator d = new AdsbDemodulator(s);
            RawMessage m;
            while ((m = d.nextMessage()) != null) {
                System.out.println(m);
                n++;
            }
        }

        Date end = new Date();

        System.out.println("Number of messages: " + n);
        System.out.println("Executed in: " + (end.getTime() - start.getTime()) / 1000.0 + " seconds");
    }
}