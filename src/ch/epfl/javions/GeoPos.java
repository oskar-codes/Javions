package ch.epfl.javions;

/**
 * Record representing a geographical position
 * @author Eddy Rashed (360667)
 * @author Oskar Zanota (361595)
 */
public record GeoPos(int longitudeT32, int latitudeT32) {
    /**
     * Creates a new GeoPos
     * @param longitudeT32 longitude in T32
     * @param latitudeT32 latitude in T32
     * @throws IllegalArgumentException if the latitude is not a valid latitude
     */
    public GeoPos {
        if (!isValidLatitudeT32(latitudeT32)) throw new IllegalArgumentException("Invalid latitude");
    }

    /**
     * Utility method to check if a latitude is valid
     * @param lat latitude in T32
     * @return true if the latitude is in the range [-2^30, 2^30]
     */
    public static boolean isValidLatitudeT32(int lat) {
        return lat >= -Math.pow(2,30) && lat <= Math.pow(2, 30);
    }

    /**
     * Returns the longitude in degrees
     * @return the longitude in degrees
     */
    public double longitude() {
        return Units.convertFrom(longitudeT32, Units.Angle.T32);
    }

    /**
     * Returns the latitude in degrees
     * @return the latitude in degrees
     */
    public double latitude() {
        return Units.convertFrom(latitudeT32, Units.Angle.T32);
    }

    /**
     * Overrides the toString method to return a string representation of the GeoPos in degrees
     * @return a string representation of the GeoPos in degrees
     */
    @Override
    public String toString() {
        return "(" + Units.convertTo(longitude(), Units.Angle.DEGREE) + "°, " + Units.convertTo(latitude(), Units.Angle.DEGREE) + "°)";
    }
}