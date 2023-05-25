package ch.epfl.javions;

/**
 * Non-instanciable utility class containing constants and conversion methods for units
 *
 * @author Eddy Rashed (360667)
 * @author Oskar Zanota (361595)
 */
public final class Units {
    private Units() {}

    public static final double CENTI = 1e-2;
    public static final double KILO = 1e3;

    /**
     * Converts a value from one unit to another
     *
     * @param value    the value to convert
     * @param fromUnit the unit of the value
     * @param toUnit   the unit to convert to
     * @return the converted value
     */
    public static double convert(double value, double fromUnit, double toUnit) {
        return value * (fromUnit / toUnit);
    }

    /**
     * Converts a value from one unit to the basis unit
     *
     * @param value    the value to convert
     * @param fromUnit the unit of the value
     * @return the converted value
     */
    public static double convertFrom(double value, double fromUnit) {
        return value * fromUnit;
    }

    /**
     * Converts a value from the basis unit to another unit
     *
     * @param value  the value to convert
     * @param toUnit the unit to convert to
     * @return the converted value
     */
    public static double convertTo(double value, double toUnit) {
        return value * (1 / toUnit);
    }


    /**
     * Contains constants for units of length
     */
    public static class Length {
        private Length() {}

        public static final double METER = 1;
        public static final double CENTIMETER = CENTI * METER;
        public static final double KILOMETER = KILO * METER;

        public static final double NAUTICAL_MILE = 1852 * METER;
        public static final double INCH = 2.54 * CENTIMETER;
        public static final double FOOT = 12 * INCH;
    }

    /**
     * Contains constants for units of angle
     */
    public static class Angle {
        private Angle() {}

        public static final double RADIAN = 1;
        public static final double TURN = 2 * Math.PI * RADIAN;
        public static final double DEGREE = TURN / 360;
        public static final double T32 = Math.scalb(TURN, -32);
    }

    /**
     * Contains constants for units of time
     */
    public static class Time {
        private Time() {}

        public static final double SECOND = 1;
        public static final double MINUTE = 60 * SECOND;
        public static final double HOUR = 60 * MINUTE;
    }

    /**
     * Contains constants for units of speed
     */
    public static class Speed {
        private Speed() {}

        public static final double KILOMETER_PER_HOUR = Length.KILOMETER / Time.HOUR;
        public static final double KNOT = Length.NAUTICAL_MILE / Time.HOUR;
    }
}