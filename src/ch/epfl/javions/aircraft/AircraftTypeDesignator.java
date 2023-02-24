package ch.epfl.javions.aircraft;

import java.util.regex.Pattern;

public record AircraftTypeDesignator(String str) {
    static final Pattern pattern = Pattern.compile("[A-Z0-9]{2,4}");
    public AircraftTypeDesignator {
        if (!pattern.matcher(str).matches() && str.length() != 0) {
            throw new IllegalArgumentException("Invalid aircraft type designator");
        }
    }
}
