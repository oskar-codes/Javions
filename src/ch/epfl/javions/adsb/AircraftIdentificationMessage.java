package ch.epfl.javions.adsb;

import ch.epfl.javions.aircraft.IcaoAddress;

import java.util.Objects;

import static ch.epfl.javions.Preconditions.checkArgument;

/**
 * Represents an Aircraft Identification Message.
 *
 * @param timeStampNs the time at which the message was received
 * @param icaoAddress the ICAO address of the aircraft
 * @param category    the category of the aircraft
 * @param callSign    the call sign of the aircraft
 * @author Oskar Zanota (361595)
 * @author Eddy Rashed (360667)
 */
public record AircraftIdentificationMessage(long timeStampNs,
                                            IcaoAddress icaoAddress,
                                            int category,
                                            CallSign callSign) implements Message {
    private static final char[] ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789 ".toCharArray();
    private static final int PAYLOAD_SIZE = 48;
    public AircraftIdentificationMessage {
        Objects.requireNonNull(icaoAddress);
        Objects.requireNonNull(callSign);
        checkArgument(timeStampNs >= 0);
    }

    /**
     * Creates an AircraftIdentificationMessage from a RawMessage.
     *
     * @param rawMessage the RawMessage to be converted
     * @return the AircraftIdentificationMessage
     */
    public static AircraftIdentificationMessage of(RawMessage rawMessage) {

        long payload = rawMessage.payload();

        int first = 14 - rawMessage.typeCode();
        int second = (int) (payload >>> PAYLOAD_SIZE & 0b111);

        int category = (first << 4) | second;

        long cs = payload & (1L << PAYLOAD_SIZE) - 1;

        StringBuilder string = new StringBuilder();

        // Constructs the CallSign from the 48 bits of the payload
        for (int i = PAYLOAD_SIZE; i > 0; i -= 6) {
            int c = (int) (cs >>> (i - 6)) & 0b111111;

            if (c >= 1 && c <= 26) {
                string.append(ALPHABET[c - 1]);
                continue;
            }

            if (c >= 48 && c <= 57) {
                string.append(ALPHABET[c - 22]);
                continue;
            }

            if (c == 32) {
                string.append(" ");
                continue;
            }

            return null;
        }

        String TRAILING_SPACES = "\\s+$";
        CallSign callSign = new CallSign(string.toString().replaceAll(TRAILING_SPACES, ""));
        return new AircraftIdentificationMessage(rawMessage.timeStampNs(), rawMessage.icaoAddress(), category, callSign);
    }

}
