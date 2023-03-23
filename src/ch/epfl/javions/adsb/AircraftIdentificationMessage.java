package ch.epfl.javions.adsb;

import ch.epfl.javions.aircraft.IcaoAddress;

import static ch.epfl.javions.Preconditions.checkArgument;

public record AircraftIdentificationMessage(long timeStampNs, IcaoAddress icaoAddress, int category, CallSign callSign) implements Message {

    public AircraftIdentificationMessage {
        if (icaoAddress == null || callSign == null) {
            throw new NullPointerException("Arguments cannot be null");
        }
        checkArgument(timeStampNs >= 0);
    }

    //check if TypeCode is valid in tests
    public static AircraftIdentificationMessage of(RawMessage rawMessage) {

        int first = 14 - rawMessage.typeCode();
        int second = (int) (rawMessage.payload() >>> 48 & 0b111);

        int category = (first << 4) | second;

        long cs = rawMessage.payload()  & (long) Math.pow(2, 48) - 1;

        String string = "";

        char[] alphabet = new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
        for (int i = 48; i >= 0; i -= 6) {
            int c = (int) (cs >>> (i - 6)) & 0b111111;

            if (c >= 1 && c <= 26) {
                string += alphabet[c - 1];
                continue;
            }

            if (c >= 48 && c <= 57) {
                string += alphabet[c - 22];
                continue;
            }

            if (c == 32) {
                string += " ";
            }
        }

        CallSign callSign = new CallSign(string);
        return new AircraftIdentificationMessage(rawMessage.timeStampNs(), rawMessage.icaoAddress(), category, callSign);
    }

}
