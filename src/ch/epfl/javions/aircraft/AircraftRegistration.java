package ch.epfl.javions.aircraft;

import java.util.regex.Pattern;

import static ch.epfl.javions.Preconditions.checkArgument;

/**
 * Record representing the registration of an aircraft.
 *
 * @author Eddy Rashed (360667)
 * @author Oskar Zanota (361595)
 */
public record AircraftRegistration(String string) {
    // The pattern for the registration of an aircraft
    private static final Pattern PATTERN = Pattern.compile("[A-Z0-9 .?/_+-]+");

    /**
     * Constructs an AircraftRegistration object with the given string.
     *
     * @param string the string representing the registration of an aircraft
     */
    public AircraftRegistration {
        checkArgument(PATTERN.matcher(string).matches());
    }
}
