package ch.epfl.javions.adsb;

import java.util.regex.Pattern;

import static ch.epfl.javions.Preconditions.checkArgument;

public record CallSign(String string) {
    static final Pattern pattern = Pattern.compile("[A-Z0-9 ]{0,8}");
    public CallSign {
        checkArgument(pattern.matcher(string).matches() && string.length() != 0);
    }
}
