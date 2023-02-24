package ch.epfl.javions.aircraft;

import java.util.regex.Pattern;

public record IcaoAddress(String str) {
    static final Pattern pattern = Pattern.compile("[0-9A-F]{6}");
    public IcaoAddress {
        if (!pattern.matcher(str).matches() || str.length() == 0) {
            throw new IllegalArgumentException("Invalid ICAO str");
        }
    }
}
