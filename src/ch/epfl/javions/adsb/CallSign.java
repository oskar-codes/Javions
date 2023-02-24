package ch.epfl.javions.adsb;

import java.util.regex.Pattern;

public record CallSign(String str) {
    static final Pattern pattern = Pattern.compile("[A-Z0-9 ]{0,8}");
    public CallSign {
        if (!pattern.matcher(str).matches() || str.length() == 0) {
            throw new IllegalArgumentException("Invalid Call Sign");
        }
    }
}
