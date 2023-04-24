package ch.epfl.javions;

import ch.epfl.javions.adsb.AircraftStateManager;
import ch.epfl.javions.adsb.Message;
import ch.epfl.javions.adsb.MessageParser;
import ch.epfl.javions.adsb.RawMessage;
import ch.epfl.javions.aircraft.AircraftDatabase;
import ch.epfl.javions.gui.ObservableAircraftState;
import org.junit.jupiter.api.Test;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class ReadSamples {
    public static void main(String[] args) {
        AircraftStateManager stateManager = new AircraftStateManager(new AircraftDatabase("resources/aircraft.zip"));
        int i = 0;
        try (DataInputStream s = new DataInputStream(
                new BufferedInputStream(
                        new FileInputStream("resources/messages_20230318_0915.bin")))){
            byte[] bytes = new byte[RawMessage.LENGTH];

            while (true) {
                long timeStampNs = s.readLong();
                int bytesRead = s.readNBytes(bytes, 0, bytes.length);
                assert bytesRead == RawMessage.LENGTH;
                ByteString message = new ByteString(bytes);

                RawMessage raw = RawMessage.of(timeStampNs, bytes);
                if (raw == null) continue;
                Message m = MessageParser.parse(raw);
                if (m == null) continue;
                stateManager.updateWithMessage(m);

                StringBuilder output = new StringBuilder();
                try {
                    String header = padRight("OACI", 10) + padRight("Indicatif", 10) + padRight("Immat.", 10) + padRight("Modèle", 40) + padRight("Longitude", 14) + padRight("Latitude", 14) + padRight("Altitude", 10) + padRight("Vitesse", 10);
                    output.append(header).append("\n");
                    output.append("-".repeat(header.length())).append("\n");
                    for (ObservableAircraftState state : stateManager.states()) {
                        output.append(padRight(state.getIcaoAddress().string(), 10));
                        output.append(padRight(state.getCallSign().string(), 10));
                        output.append(padRight(state.getAircraftData().registration().string(), 10));
                        output.append(padRight(state.getAircraftData().model(), 40));
                        String longitude = String.valueOf(Units.convertTo(state.getPosition().longitude(), Units.Angle.DEGREE)).substring(0, 13);
                        String latitude = String.valueOf(Units.convertTo(state.getPosition().latitude(), Units.Angle.DEGREE)).substring(0, 13);
                        output.append(padRight(longitude, 14));
                        output.append(padRight(latitude, 14));
                        output.append(padRight(String.valueOf(Math.floor(state.getAltitude())), 10));
                        output.append(padRight(String.valueOf(Math.floor(state.getVelocity())), 10));
                        output.append("\n");
                    }
                } catch (Exception ignored) {}
                System.out.println(output);
//                System.out.printf("%13d: %s\n", timeStampNs, message);
                i++;
            }
        } catch (IOException e) {
            System.out.println(e);
        }
        System.out.println(i);
    }

    public static String padRight(String s, int n) {
        return String.format("%-" + n + "s", s);
    }

    @Test
    public void testIterator() {
        List<Integer> l = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5));
        Iterator<Integer> it = l.iterator();

        System.out.println(it.next());
        it.remove();
        System.out.println(it.next());

        System.out.println(l);
    }
}
