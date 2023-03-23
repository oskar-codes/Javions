package ch.epfl.javions.adsb;

import ch.epfl.javions.aircraft.IcaoAddress;

import static ch.epfl.javions.Preconditions.checkArgument;

public record AirbornePositionMessage(long timeStampNs, IcaoAddress icaoAddress, double altitude, int parity, double x, double y) {
    public AirbornePositionMessage {
        if (icaoAddress == null) {
            throw new NullPointerException("Arguments cannot be null");
        }
        checkArgument(timeStampNs >= 0 && (parity == 0 || parity == 1) && x >= 0 && x <= 1 && y >= 0 && y <= 1);
    }


}