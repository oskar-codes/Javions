package ch.epfl.javions.adsb;

import ch.epfl.javions.GeoPos;

/**
 * An interface for setting the state of an aircraft.
 *
 * @author Oskar Zanota (361595)
 * @author Eddy Rashed (360667)
 */
public interface AircraftStateSetter {
    void setLastMessageTimeStampNs(long timeStampNs);

    void setCategory(int category);

    void setCallSign(CallSign callSign);

    void setPosition(GeoPos position);

    void setAltitude(double altitude);

    void setVelocity(double velocity);

    void setTrackOrHeading(double trackOrHeading);
}
