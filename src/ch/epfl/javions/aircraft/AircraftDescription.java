package ch.epfl.javions.aircraft;

import java.util.regex.Pattern;

public record AircraftDescription(String string) {
    static final Pattern pattern = Pattern.compile("[ABDGHLPRSTV-][0123468][EJPT-]");
    public AircraftDescription {
        if (!pattern.matcher(string).matches() && string.length() != 0) {
            throw new IllegalArgumentException("Invalid aircraft description");
        }
    }
}