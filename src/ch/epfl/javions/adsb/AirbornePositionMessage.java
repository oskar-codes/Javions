package ch.epfl.javions.adsb;

import ch.epfl.javions.Bits;
import ch.epfl.javions.Units;
import ch.epfl.javions.aircraft.IcaoAddress;

import java.util.Objects;

import static ch.epfl.javions.Bits.extractUInt;
import static ch.epfl.javions.Preconditions.checkArgument;

/**
 * A record that represents an ADS-B airborne position message.
 *
 * @param timeStampNs time of reception of the message
 * @param icaoAddress ICAO address of the aircraft
 * @param altitude    altitude of the aircraft
 * @param parity      parity of the message
 * @param x           longitude of the aircraft
 * @param y           latitude of the aircraft
 * @author Oskar Zanota (361595)
 * @author Eddy Rashed (360667)
 */
public record AirbornePositionMessage(long timeStampNs,
                                      IcaoAddress icaoAddress,
                                      double altitude,
                                      int parity,
                                      double x,
                                      double y) implements Message {
    /**
     * Constructs an airborne position message with the given parameters.
     * @param timeStampNs time of reception of the message in nanoseconds. Must be positive.
     * @param icaoAddress ICAO address of the aircraft. Must not be null.
     * @param altitude altitude of the aircraft.
     * @param parity parity of the message (0 or 1).
     * @param x longitude of the aircraft (between 0 and 1).
     * @param y latitude of the aircraft (between 0 and 1).
     */
    public AirbornePositionMessage {
        Objects.requireNonNull(icaoAddress);
        checkArgument(timeStampNs >= 0
                  && (parity == 0 || parity == 1)
                  && x >= 0 && x < 1
                  && y >= 0 && y < 1);
    }

    /**
     * Returns the value corresponding to the given gray code.
     * @param v the gray code
     * @return the value corresponding to the given gray code
     */
    private static long gray(long v) {
        int n = (int) (Math.floor(Math.log(v) / Math.log(2)) + 1);
        long result = v;
        for (int i = 1; i < n; i++) {
            result ^= v >>> i;
        }
        return result;
    }

    /**
     * Creates an airborne position message from the given raw message.
     * @param message the raw message
     * @return an airborne position message from the given raw message
     */
    public static AirbornePositionMessage of(RawMessage message) {

        long data = message.payload() & (long) Math.pow(2, 48) - 1;
        int parity = extractUInt(data, 34, 1);
        long altitudeData = extractUInt(data, 36, 12);
        double altitude;

        boolean Q = Bits.testBit(altitudeData, 4);
        if (Q) {
            Bits.extractUInt(data, 36, 12);
            long n = (((long)Bits.extractUInt(altitudeData, 5, 7) << 4 | (Bits.extractUInt(altitudeData, 0 , 4))));
            altitude = Units.convertFrom(-1000 + 25 * n, Units.Length.FOOT);
        } else {

            long untangled = 0;
            int bitIndex = 7;
            for (int i = 0; i < 12; i++) {
                // Extract the current bit from the input using bitwise operations and set it in the untangled value
                untangled += (Bits.testBit(altitudeData, bitIndex) ? 1 : 0) << i;

                // Update the bit index for the next iteration
                // The indices follow: [7, 9, 11, 1, 3, 5, 6, 8, 10, 0, 2, 4]
                if (bitIndex == 5) bitIndex -= 1;
                bitIndex = (bitIndex + 2) % 12;
            }

            long least = gray(untangled & 0b111);
            long most = gray(untangled >>> 3);

            if (least == 0 || least == 5 || least == 6) return null;
            if (least == 7) least = 5;
            if (most % 2 == 1) least = 6 - least;
            altitude = Units.convertFrom(-1300 + 100 * least + 500 * most, Units.Length.FOOT);
        }

        long lonData = extractUInt(data, 0, 17);
        long latData = extractUInt(data, 17, 17);

        double x = Math.scalb(lonData, -17);
        double y = Math.scalb(latData, -17);

        return new AirbornePositionMessage(message.timeStampNs(), message.icaoAddress(), altitude, parity, x, y);
    }
}