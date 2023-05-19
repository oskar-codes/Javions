package ch.epfl.javions.adsb;

import ch.epfl.javions.Bits;
import ch.epfl.javions.Units;
import ch.epfl.javions.aircraft.IcaoAddress;

import static ch.epfl.javions.Preconditions.checkArgument;

/**
 * A record that represents an airborne velocity message.
 * @param timeStampNs    the time at which the message was received.
 * @param icaoAddress    the ICAO address of the aircraft.
 * @param speed          the speed of the aircraft.
 * @param trackOrHeading the track or heading of the aircraft.
 * @author Oskar Zanota (361595)
 * @author Eddy Rashed (360667)
 */
public record AirborneVelocityMessage(long timeStampNs, IcaoAddress icaoAddress, double speed,
                                      double trackOrHeading) implements Message {
    private static final int DNS_POSITION = 10;
    private static final int DEW_POSITION = 21;
    private static final int VNS_POSITION = 0;
    private static final int VNS_SIZE = 10;
    private static final int VEW_POSITION = 11;
    private static final int VEW_SIZE = 10;
    private static final int AS_POSITION = 0;
    private static final int AS_SIZE = 10;
    private static final int HDG_POSITION = 11;
    private static final int HDG_SIZE = 10;
    public AirborneVelocityMessage {
        if (icaoAddress == null) throw new NullPointerException("ICAO address is null");
        checkArgument(timeStampNs >= 0 && speed >= 0 && trackOrHeading >= 0);
    }

    /**
     * Creates an AirborneVelocityMessage from a RawMessage.
     *
     * @param rawMessage the RawMessage to decode.
     * @return the decoded AirborneVelocityMessage.
     */
    public static AirborneVelocityMessage of(RawMessage rawMessage) {
        int subtype = Bits.extractUInt(rawMessage.payload(), 48, 3);
        int data = Bits.extractUInt(rawMessage.payload(), 21, 22);

        if (subtype == 1 || subtype == 2) {
            boolean dns = Bits.testBit(data, DNS_POSITION);
            int vns = Bits.extractUInt(data, VNS_POSITION, VNS_SIZE) - 1;

            boolean dew = Bits.testBit(data, DEW_POSITION);
            int vew = Bits.extractUInt(data, VEW_POSITION, VEW_SIZE) - 1;

            if (vns == -1 || vew == -1) return null;

            double speed = Math.hypot(vew, vns) * (subtype == 2 ? 4 : 1);

            // Converts the result to m/s
            double convertedSpeed = Units.convertFrom(speed, Units.Speed.KNOT);

            if (dns) vns = -vns;
            if (dew) vew = -vew;

            double heading = Math.atan2(vew, vns);
            if (heading < 0) heading += 2 * Math.PI;

            return new AirborneVelocityMessage(rawMessage.timeStampNs(), rawMessage.icaoAddress(), convertedSpeed, heading);
        }

        if (subtype == 3 || subtype == 4) {
            boolean sh = Bits.testBit(data, 21);
            if (sh) {
                long hdg = Integer.toUnsignedLong(Bits.extractUInt(data, HDG_POSITION, HDG_SIZE));
                double result = Units.convertFrom(Math.scalb(hdg, -10), Units.Angle.TURN);

                long as = Bits.extractUInt(data, AS_POSITION, AS_SIZE) - 1;
                if (as == -1) return null;
                double speed = as * (subtype == 4 ? 4 : 1);

                // Converts the result to m/s
                double convertedSpeed = Units.convertFrom(speed, Units.Speed.KNOT);

                return new AirborneVelocityMessage(rawMessage.timeStampNs(), rawMessage.icaoAddress(), convertedSpeed, result);
            }
        }

        return null;
    }
}