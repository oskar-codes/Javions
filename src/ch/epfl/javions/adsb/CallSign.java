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
    static final Pattern PATTERN = Pattern.compile("[A-Z0-9 ]{0,8}");

    public CallSign {
        checkArgument(PATTERN.matcher(string).matches() || string.length() == 0);
    }
}