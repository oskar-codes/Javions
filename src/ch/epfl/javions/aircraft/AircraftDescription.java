package ch.epfl.javions.aircraft;

import java.util.regex.Pattern;

public record AircraftDescription(String str) {
    static final Pattern pattern = Pattern.compile("[ABDGHLPRSTV-][0123468][EJPT-]");
    public AircraftDescription {
        if (!pattern.matcher(str).matches() && str.length() != 0) {
            throw new IllegalArgumentException("Invalid aircraft description");
        }
    }
}