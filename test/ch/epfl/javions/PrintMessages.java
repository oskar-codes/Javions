package ch.epfl.javions;

import ch.epfl.javions.adsb.AircraftIdentificationMessage;
import ch.epfl.javions.adsb.RawMessage;
import ch.epfl.javions.demodulation.AdsbDemodulator;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

public final class PrintMessages {
    public static void main(String[] args) throws IOException {

//        System.in.read();

        System.out.println("Started");
        Date start = new Date();

        String f = "resources/samples_20230304_1442.bin";
        try (InputStream s = new FileInputStream(f)) {
            AdsbDemodulator d = new AdsbDemodulator(s);
            RawMessage m;
            int n = 0;
            while ((m = d.nextMessage()) != null) {

                AircraftIdentificationMessage a = AircraftIdentificationMessage.of(m);
                System.out.println(a);

                n++;
            }
            System.out.println("Number of messages: " + n);
        }

        Date end = new Date();
        System.out.println("Time spent: " + (end.getTime() - start.getTime()) / 1000.0 + " seconds");
    }
}