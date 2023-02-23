package ch.epfl.javions;

/**
 * Non-instanciable Web Mercator projection utility class
 * @author Eddy Rashed (360667)
 * @author Oskar Zanota (361595)
 */
public class WebMercator {
    private WebMercator() {}

    /**
     * Converts a longitude to a x coordinate following the Web Mercator projection
     * @param zoomLevel the zoom level of the map
     * @param longitude the longitude to convert
     * @return the x coordinate
     */
    public static double x(int zoomLevel, double longitude) {
        return Math.scalb(Units.convertTo(longitude, Units.Angle.TURN) + 0.5, 8 + zoomLevel);
    }

    /**
     * Converts a latitude to a y coordinate following the Web Mercator projection
     * @param zoomLevel the zoom level of the map
     * @param latitude the latitude to convert
     * @return the y coordinate
     */
    public static double y (int zoomLevel, double latitude) {
        return Math.scalb(-Units.convertTo(Math2.asinh(Math.tan(latitude)), Units.Angle.TURN) + 0.5, 8 + zoomLevel);
    }
}