package ch.epfl.javions.adsb;

import ch.epfl.javions.Bits;
import ch.epfl.javions.Units;
import ch.epfl.javions.aircraft.IcaoAddress;

import static ch.epfl.javions.Bits.extractUInt;
import static ch.epfl.javions.Preconditions.checkArgument;

//check que code de type soit contenu dans (9,18) et (20,22)
public record AirbornePositionMessage(long timeStampNs, IcaoAddress icaoAddress, double altitude, int parity, double x, double y) implements Message {
    public AirbornePositionMessage {
        if (icaoAddress == null) {
            throw new NullPointerException("Arguments cannot be null");
        }
        checkArgument(timeStampNs >= 0 && (parity == 0 || parity == 1) && x >= 0 && x < 1 && y >= 0 && y < 1);
    }

    private static int bitValue(long data, int bit) {
        return Bits.testBit(data, bit) ? 1 : 0;
    }

    private static long gray(long v) {
        int n = (int) (Math.floor(Math.log(v) / Math.log(2)) + 1);
        long result = v;
        for (int i = 1; i < n; i++) {
            result ^= v >>> i;
        }
        return result;
    }

    public static AirbornePositionMessage of(RawMessage message) {

        long data = message.payload() & (long) Math.pow(2, 48) - 1;
        int parity = extractUInt(data, 34, 1);
        long altitudeData = extractUInt(data, 36, 12);
        double altitude;

        int Q = bitValue(altitudeData, 4);
        if (Q == 1) {
            long n = ((altitudeData & 0b111111100000) >>> 1) | (altitudeData & 0b1111);
            altitude = Units.convert(-1000 + 25 * n, Units.Length.FOOT, Units.Length.METER);
        } else {
            long untangled = (long) bitValue(altitudeData, 4) << 11
                           | (long) bitValue(altitudeData, 2) << 10
                           | (long) bitValue(altitudeData, 0) << 9
                           | (long) bitValue(altitudeData, 10) << 8
                           | (long) bitValue(altitudeData, 8) << 7
                           | (long) bitValue(altitudeData, 6) << 6
                           | (long) bitValue(altitudeData, 5) << 5
                           | (long) bitValue(altitudeData, 3) << 4
                           | (long) bitValue(altitudeData, 1) << 3
                           | (long) bitValue(altitudeData, 11) << 2
                           | (long) bitValue(altitudeData, 9) << 1
                           | (long) bitValue(altitudeData, 7);

            long least = gray(untangled & 0b111);
            long most = gray(untangled >>> 3);

            if (least == 0 || least == 5 || least == 6) return null;
            if (least == 7) least = 5;
            if (most % 2 == 1) least = 6 - least;

            altitude = Units.convert(-1300 + 100 * least + 500 * most, Units.Length.FOOT, Units.Length.METER);
        }

        long lonData = extractUInt(data, 0, 17);
        long latData = extractUInt(data, 17, 17);

        double x = lonData / Math.pow(2, 17);
        double y = latData / Math.pow(2, 17);

        return new AirbornePositionMessage(message.timeStampNs(), message.icaoAddress(), altitude, parity, x, y);
    }
}