package ch.epfl.javions.adsb;

import ch.epfl.javions.GeoPos;

/**
 * An interface for setting the state of an aircraft.
 *
 * @author Oskar Zanota (361595)
 * @author Eddy Rashed (360667)
 */
public interface AircraftStateSetter {
    /**
     * Sets the last message time stamp in nanoseconds.
     * @param timeStampNs - the last message time stamp in nanoseconds
     */
    void setLastMessageTimeStampNs(long timeStampNs);

    /**
     * Sets the aircraft category.
     * @param category - the aircraft category
     */
    void setCategory(int category);

    /**
     * Sets the Call Sign.
     * @param callSign - the Call Sign
     */
    void setCallSign(CallSign callSign);

    /**
     * Sets the aircarft's position.
     * @param position - the aircarft's position
     */
    void setPosition(GeoPos position);

    /**
     * Sets the aircraft's altitude.
     * @param altitude - the aircraft's altitude
     */
    void setAltitude(double altitude);

    /**
     * Sets the aircraft's velocity.
     * @param velocity - the aircraft's velocity
     */
    void setVelocity(double velocity);

    /**
     * Sets the aircraft's track or heading property.
     * @param trackOrHeading - the aircraft's track or heading
     */
    void setTrackOrHeading(double trackOrHeading);
}
