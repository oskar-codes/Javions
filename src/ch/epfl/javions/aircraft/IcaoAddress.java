package ch.epfl.javions.aircraft;

import java.util.regex.Pattern;

import static ch.epfl.javions.Preconditions.checkArgument;

/**
 * Record representing an ICAO address.
 *
 * @author Eddy Rashed (360667)
 * @author Oskar Zanota (361595)
 */
public record IcaoAddress(String string) {
    // Pattern to check if the string is a valid ICAO address
    static final Pattern PATTERN = Pattern.compile("[0-9A-F]{6}");

    /**
     * Constructs an IcaoAddress from a string.
     *
     * @param string the string representing the ICAO address
     */
    public IcaoAddress {
        checkArgument(PATTERN.matcher(string).matches() && string.length() != 0);
    }
}
