package ch.epfl.javions.adsb;

import ch.epfl.javions.aircraft.IcaoAddress;

import static ch.epfl.javions.Preconditions.checkArgument;

/**
 * Represents an Aircraft Identification Message.
 * @author Oskar Zanota (361595)
 * @author Eddy Rashed (360667)
 * @param timeStampNs - the time at which the message was received
 * @param icaoAddress - the ICAO address of the aircraft
 * @param category - the category of the aircraft
 * @param callSign - the call sign of the aircraft
 */
public record AircraftIdentificationMessage(long timeStampNs, IcaoAddress icaoAddress, int category, CallSign callSign) implements Message {

    public AircraftIdentificationMessage {
        if (icaoAddress == null || callSign == null) {
            throw new NullPointerException("Arguments cannot be null");
        }
        checkArgument(timeStampNs >= 0);
    }

    /**
     * Creates an AircraftIdentificationMessage from a RawMessage.
     * @param rawMessage - the RawMessage to be converted
     * @return the AircraftIdentificationMessage
     */
    public static AircraftIdentificationMessage of(RawMessage rawMessage) {

        int first = 14 - rawMessage.typeCode();
        int second = (int) (rawMessage.payload() >>> 48 & 0b111);

        int category = (first << 4) | second;

        long cs = rawMessage.payload() & (long) Math.pow(2, 48) - 1;

        StringBuilder string = new StringBuilder();

        // TODO: Do this with StringBuilder ?
        // Constructs the CallSign from the 48 bits of the payload
        char[] alphabet = new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
        for (int i = 48; i > 0; i -= 6) {
            int c = (int) (cs >>> (i - 6)) & 0b111111;

            if (c >= 1 && c <= 26) {
                string.append(alphabet[c - 1]);
                continue;
            }

            if (c >= 48 && c <= 57) {
                string.append(alphabet[c - 22]);
                continue;
            }

            if (c == 32) {
                string.append(" ");
                continue;
            }

            return null;
        }

        CallSign callSign = new CallSign(string.toString().trim());
        return new AircraftIdentificationMessage(rawMessage.timeStampNs(), rawMessage.icaoAddress(), category, callSign);
    }

}
