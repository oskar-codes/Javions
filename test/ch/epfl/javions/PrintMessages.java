package ch.epfl.javions;

import ch.epfl.javions.adsb.AirbornePositionMessage;
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

        System.out.println("### Q = 0 ###");
        RawMessage messageA = new RawMessage(0, ByteString.ofHexadecimalString("8D39203559B225F07550ADBE328F"));
        AirbornePositionMessage airbornePositionMessageA = AirbornePositionMessage.of(messageA);
        System.out.println(airbornePositionMessageA);

        RawMessage messageB = new RawMessage(0, ByteString.ofHexadecimalString("8DAE02C85864A5F5DD4975A1A3F5"));
        AirbornePositionMessage airbornePositionMessageB = AirbornePositionMessage.of(messageB);
        System.out.println(airbornePositionMessageB);

        System.out.println("### Q = 1 ###");

        String f = "resources/samples_20230304_1442.bin";
        try (InputStream s = new FileInputStream(f)) {
            AdsbDemodulator d = new AdsbDemodulator(s);
            RawMessage m;
            int n = 0;

            while ((m = d.nextMessage()) != null) {
                if (m.typeCode() >= 1 && m.typeCode() <= 4) {
                    AircraftIdentificationMessage a = AircraftIdentificationMessage.of(m);
                    System.out.println(a);
                    n++;
                }

//                if (m.typeCode() >= 9 && m.typeCode() <= 18 || m.typeCode() >= 20 && m.typeCode() <= 22) {
//                    AirbornePositionMessage a = AirbornePositionMessage.of(m);
//                    System.out.println(a);
//                    n++;
//                }
            }

            System.out.println("Number of messages: " + n);
        }

        Date end = new Date();
        System.out.println("Time spent: " + (end.getTime() - start.getTime()) / 1000.0 + " seconds");
    }
}