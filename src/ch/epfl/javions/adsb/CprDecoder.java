package ch.epfl.javions.adsb;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.Units;

import static ch.epfl.javions.Preconditions.checkArgument;

/**
 * A class that decodes the CPR encoded position of an aircraft.
 * @author Oskar Zanota (361595)
 * @author Eddy Rashed (360667)
 */
public class CprDecoder {
    private CprDecoder() {}

    public final static double ZP0 = 60;
    public final static double ZP1 = 59;

    /**
     * Decodes the position of an aircraft.
     * @param x0 the longitude of the first position.
     * @param y0 the latitude of the first position.
     * @param x1 the longitude of the second position.
     * @param y1 the latitude of the second position.
     * @param mostRecent the index of the most recent position.
     * @return the decoded position as a GeoPos
     */
    public static GeoPos decodePosition(double x0, double y0, double x1, double y1, int mostRecent) {
        checkArgument(mostRecent == 0 || mostRecent == 1);

        double zLat = Math.rint(y0 * ZP1 - y1 * ZP0);

        double z0Lat = zLat < 0 ? zLat + ZP0 : zLat;
        double z1Lat = zLat < 0 ? zLat + ZP1 : zLat;

        double p0 = (1D / ZP0) * (z0Lat + y0);
        double p1 = (1D / ZP1) * (z1Lat + y1);

        double p0Rad = Units.convert(p0, Units.Angle.TURN, Units.Angle.RADIAN);
        double p1Rad = Units.convert(p1, Units.Angle.TURN, Units.Angle.RADIAN);

        double p0T32 = Units.convert(p0, Units.Angle.TURN, Units.Angle.T32);
        double p1T32 = Units.convert(p1, Units.Angle.TURN, Units.Angle.T32);

        double A1 = Math.acos(
                1 - ((1 - Math.cos(2*Math.PI*(1 / ZP0))) / Math.pow(Math.cos(p0Rad), 2))
        );
        double A2 = Math.acos(
                1 - ((1 - Math.cos(2*Math.PI*(1 / ZP1))) / Math.pow(Math.cos(p1Rad), 2))
        );

        int ZL0 = Double.isNaN(A1) ? 1 : (int) Math.floor((2 * Math.PI) / A1);
        int ZL1 = ZL0 - 1;

        int ZL1_2 = Double.isNaN(A2) ? 1 : (int) Math.floor((2 * Math.PI) / A2);

        if (ZL0 != ZL1_2 + 1) return null;

        if (ZL0 == 1) {
            double x0T32 = Units.convert(x0, Units.Angle.TURN, Units.Angle.T32);
            double x1T32 = Units.convert(x1, Units.Angle.TURN, Units.Angle.T32);
            return mostRecent == 0 ?
                    new GeoPos((int) Math.round(x0T32), (int) Math.round(p0T32)) :
                    new GeoPos((int) Math.round(x1T32), (int) Math.round(p1T32));
        }

        double zLon = Math.rint(x0 * ZL1 - x1 * ZL0);

        double z0Lon = zLon < 0 ? zLon + ZL0 : zLon;
        double z1Lon = zLon < 0 ? zLon + ZL1 : zLon;
        double lam0 = (1D / ZL0) * (z0Lon + x0);
        double lam1 = (1D / ZL1) * (z1Lon + x1);

        if (lam1 > 0.5) lam1 -= 1;
        if (lam0 > 0.5) lam0 -= 1;

        double lam0T32 = Units.convert(lam0, Units.Angle.TURN, Units.Angle.T32);
        double lam1T32 = Units.convert(lam1, Units.Angle.TURN, Units.Angle.T32);

        return mostRecent == 0 ?
                new GeoPos((int) Math.round(lam0T32), (int) Math.round(p0T32)) :
                new GeoPos((int) Math.round(lam1T32), (int) Math.round(p1T32));
    }
}
