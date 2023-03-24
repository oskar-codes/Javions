package ch.epfl.javions;

import ch.epfl.javions.adsb.AirbornePositionMessage;
import ch.epfl.javions.adsb.AirborneVelocityMessage;
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

        int identification = 0;
        int position = 0;
        int velocity = 0;
        int ignored = 0;

        double maxSpeed = -1;
        double minSpeed = Double.MAX_VALUE;

        String f = "resources/samples_20230304_1442.bin";
        try (InputStream s = new FileInputStream(f)) {
            AdsbDemodulator d = new AdsbDemodulator(s);
            RawMessage m;

            while ((m = d.nextMessage()) != null) {
                if (m.typeCode() >= 1 && m.typeCode() <= 4) {
                    AircraftIdentificationMessage a = AircraftIdentificationMessage.of(m);
                    System.out.println(a);
                    identification++;
                     continue;
                }
                if (m.typeCode() >= 9 && m.typeCode() <= 18 || m.typeCode() >= 20 && m.typeCode() <= 22) {
                    AirbornePositionMessage a = AirbornePositionMessage.of(m);
                    System.out.println(a);
                    position++;
                     continue;
                }
                if (m.typeCode() == 19) {
                    AirborneVelocityMessage a = AirborneVelocityMessage.of(m);
                    if (a != null && a.speed() > maxSpeed) {
                        maxSpeed = a.speed();
                    }
                     if (a != null && a.speed() < minSpeed) {
                           minSpeed = a.speed();
                     }
                    System.out.println(a);
                    velocity++;
                    continue;
                }
                ignored++;
            }
        }

        Date end = new Date();
        System.out.println();
        System.out.println("Executed in: " + (end.getTime() - start.getTime()) / 1000.0 + " seconds");
        System.out.println();
        System.out.println("Max speed: " + maxSpeed * 3.6 + " km/h");
        System.out.println("Min speed: " + minSpeed * 3.6 + " km/h");
        System.out.println();
        System.out.println("Identification messages: " + identification);
        System.out.println("Position messages: " + position);
        System.out.println("Velocity messages: " + velocity);
        System.out.println("Ignored messages: " + ignored);
        System.out.println("Number of messages: " + (identification + position + velocity + ignored));
    }
}