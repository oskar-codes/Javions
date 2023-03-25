package ch.epfl.javions.adsb;

import ch.epfl.javions.GeoPos;

public class AircraftState implements AircraftStateSetter {

    @Override
    public void setLastMessageTimeStampNs(long timeStampNs) {
//        System.out.println("timeStampNs: " + timeStampNs);
    }

    @Override
    public void setCategory(int category) {
//        System.out.println("category: " + category);
    }

    @Override
    public void setCallSign(CallSign callSign) {
        System.out.println("callSign: " + callSign);
    }

    @Override
    public void setPosition(GeoPos position) {
        System.out.println("position: " + position);
    }

    @Override
    public void setAltitude(double altitude) {
//        System.out.println("altitude: " + altitude);
    }

    @Override
    public void setVelocity(double velocity) {
//        System.out.println("velocity: " + velocity);
    }

    @Override
    public void setTrackOrHeading(double trackOrHeading) {
//        System.out.println("trackOrHeading: " + trackOrHeading);
    }
}
