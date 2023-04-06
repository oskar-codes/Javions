package ch.epfl.javions.adsb;

import ch.epfl.javions.GeoPos;

public class AircraftState implements AircraftStateSetter {
    private long lastMessageTimeStampNs;
    @Override
    public void setLastMessageTimeStampNs(long timeStampNs) {
        lastMessageTimeStampNs = timeStampNs;
    }

    private int category;
    @Override
    public void setCategory(int newCategory) {
        category = newCategory;
    }

    private CallSign callSign;
    @Override
    public void setCallSign(CallSign newCallSign) {
        callSign = newCallSign;
    }

    private GeoPos position;
    @Override
    public void setPosition(GeoPos newPosition) {
        position = newPosition;
    }

    private double altitude;
    @Override
    public void setAltitude(double newAltitude) {
        altitude = newAltitude;
    }

    private double velocity;
    @Override
    public void setVelocity(double newVelocity) {
        velocity = newVelocity;
    }

    private double trackOrHeading;
    @Override
    public void setTrackOrHeading(double newTrackOrHeading) {
        trackOrHeading = newTrackOrHeading;
    }
}
