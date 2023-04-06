package ch.epfl.javions.aircraft;

import java.util.regex.Pattern;

import static ch.epfl.javions.Preconditions.checkArgument;

/**
 * Record representing the description of an aircraft.
 * @author Eddy Rashed (360667)
 * @author Oskar Zanota (361595)
 */
public record AircraftDescription(String string) {
    // The pattern that the string must match
    static final Pattern PATTERN = Pattern.compile("[ABDGHLPRSTV-][0123468][EJPT-]");

    /**
     * Constructs an AircraftDescription object with the given string.
     * @param string - the string representing the description of an aircraft
     */
    public AircraftDescription {
        checkArgument(PATTERN.matcher(string).matches() || string.length() == 0);
    }
}