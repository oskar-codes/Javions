package ch.epfl.javions;

import ch.epfl.javions.adsb.*;
import ch.epfl.javions.demodulation.AdsbDemodulator;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

public final class PrintMessages {
    public static void main(String[] args) throws IOException {

        System.out.println("Started");

        Date start;

        int identification = 0;
        int position = 0;
        int velocity = 0;
        int ignored = 0;

        String f = "resources/samples_20230304_1442.bin";
        try (InputStream s = new FileInputStream(f)) {

            start = new Date();

            AdsbDemodulator d = new AdsbDemodulator(s);
            RawMessage m;

            while ((m = d.nextMessage()) != null) {

                Message parsed = MessageParser.parse(m);
                if (parsed != null) System.out.println(parsed);

                switch (parsed) {
                    case AircraftIdentificationMessage aim -> identification++;
                    case AirbornePositionMessage apm -> position++;
                    case AirborneVelocityMessage avm -> velocity++;
                    case null, default -> ignored++;
                }
            }
        }

        Date end = new Date();
        System.out.println();
        System.out.println("Executed in: " + (end.getTime() - start.getTime()) / 1000.0 + " seconds");
        System.out.println();
        System.out.println("Identification messages: " + identification);
        System.out.println("Position messages: " + position);
        System.out.println("Velocity messages: " + velocity);
        System.out.println("Ignored messages: " + ignored);
        System.out.println("Number of messages: " + (identification + position + velocity + ignored));
    }
}