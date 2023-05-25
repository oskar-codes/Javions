package ch.epfl.javions.adsb;

import java.util.regex.Pattern;

import static ch.epfl.javions.Preconditions.checkArgument;

/**
 * A class representing a call sign.
 *
 * @param string the call sign as a string
 * @author Oskar Zanota (361595)
 * @author Eddy Rashed (360667)
 */
public record CallSign(String string) {
    // Pattern to check if the string is a valid call sign
    private static final Pattern PATTERN = Pattern.compile("[A-Z0-9 ]{0,8}");

    /**
     * Constructs a call sign with the given string.
     * @param string the string. Must be a valid call sign or empty.
     */
    public CallSign {
        checkArgument(PATTERN.matcher(string).matches() || string.isEmpty());
    }
}