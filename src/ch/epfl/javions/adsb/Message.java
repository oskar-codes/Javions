package ch.epfl.javions.adsb;

import ch.epfl.javions.aircraft.IcaoAddress;

/**
 * An ADS-B message.
 *
 * @author Oskar Zanota (361595)
 * @author Eddy Rashed (360667)
 */
public interface Message {
    /**
     * Returns the time stamp of the message in nanoseconds.
     * @return the time stamp of the message in nanoseconds
     */
    long timeStampNs();

    /**
     * Returns the ICAO address of the message.
     * @return the ICAO address of the message
     */
    IcaoAddress icaoAddress();
}