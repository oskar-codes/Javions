package ch.epfl.javions.aircraft;

import java.util.regex.Pattern;

import static ch.epfl.javions.Preconditions.checkArgument;

/**
 * Record representing the aircraft type designator.
 * @author Eddy Rashed (360667)
 * @author Oskar Zanota (361595)
 */
public record AircraftTypeDesignator(String string) {
    // The pattern for the aircraft type designator
    static final Pattern PATTERN = Pattern.compile("[A-Z0-9]{2,4}");

    /**
     * Constructs an AircraftTypeDesignator from a string.
     * @param string - the string to construct the AircraftTypeDesignator from
     */
    public AircraftTypeDesignator {
        checkArgument(PATTERN.matcher(string).matches() || string.length() == 0);
    }
}