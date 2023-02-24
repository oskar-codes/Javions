package ch.epfl.javions.aircraft;

import java.util.regex.Pattern;

public record AircraftRegistration(String str) {
    static final Pattern pattern = Pattern.compile("[A-Z0-9 .?/_+-]+");
    public AircraftRegistration {
        if (!pattern.matcher(str).matches() || str.length() == 0) {
            throw new IllegalArgumentException("Invalid Aircraft Registration");
        }
    }
}
