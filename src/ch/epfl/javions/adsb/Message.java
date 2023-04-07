package ch.epfl.javions.adsb;

import ch.epfl.javions.aircraft.IcaoAddress;

/**
 * An ADS-B message.
 *
 * @author Oskar Zanota (361595)
 * @author Eddy Rashed (360667)
 */
public interface Message {
    long timeStampNs();

    IcaoAddress icaoAddress();
}
