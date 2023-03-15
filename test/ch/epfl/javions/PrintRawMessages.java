package ch.epfl.javions;

import ch.epfl.javions.adsb.RawMessage;
import ch.epfl.javions.demodulation.AdsbDemodulator;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import static java.lang.Thread.sleep;

public final class PrintRawMessages {
    public static void main(String[] args) throws IOException, InterruptedException {

        sleep(10 * 1000);

        System.out.println("Started");
        Date start = new Date();

        String f = "resources/samples_20230304_1442.bin";
        try (InputStream s = new FileInputStream(f)) {
            AdsbDemodulator d = new AdsbDemodulator(s);
            RawMessage m;
            int n = 0;
            while ((m = d.nextMessage()) != null) {
                System.out.println(m);
                n++;
//                if (n >= 40) break;
            }
            System.out.println("Number of messages: " + n);
        }

        Date end = new Date();
        System.out.println("Time spent: " + (end.getTime() - start.getTime()) / 1000.0 + " seconds");
    }
}