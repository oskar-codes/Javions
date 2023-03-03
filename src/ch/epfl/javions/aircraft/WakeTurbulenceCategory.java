package ch.epfl.javions.aircraft;

/**
 * Enum representing the wake turbulence category of an aircraft.
 * @author Eddy Rashed (360667)
 * @author Oskar Zanota (361595)
 */
public enum WakeTurbulenceCategory {
    LIGHT, MEDIUM, HEAVY, UNKNOWN;

    /**
     * Returns the WakeTurbulenceCategory corresponding to the given String.
     * @param s - String to be converted
     * @return WakeTurbulenceCategory corresponding to the given String, or UNKNOWN if the String is not valid.
     */
    public static WakeTurbulenceCategory of(String s) {
        switch (s) {
            case "L" -> {
                return LIGHT;
            }
            case "M" -> {
                return MEDIUM;
            }
            case "H" -> {
                return HEAVY;
            }
            default -> {
                return UNKNOWN;
            }
        }
    }
}
