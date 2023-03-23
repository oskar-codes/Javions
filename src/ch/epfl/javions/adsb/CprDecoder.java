package ch.epfl.javions.adsb;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.Units;

import static ch.epfl.javions.Preconditions.checkArgument;

public class CprDecoder {
    private CprDecoder() {}

    public static double ZP0 = 60;
    public static double ZP1 = 59;

    public static GeoPos decodePosition(double x0, double y0, double x1, double y1, int mostRecent) {
        checkArgument(mostRecent == 0 || mostRecent == 1);

//        y0 /= Math.pow(2, 17);
//        y1 /= Math.pow(2, 17);

        double zLat = Math.rint(y0 * 59 - y1 * 60);

        double z0Lat = zLat < 0 ? zLat + ZP0 : zLat;
        double z1Lat = zLat < 0 ? zLat + ZP1 : zLat;

        // These are in TURN
        double p0 = 1D/ ZP0 * (z0Lat + y0);
        double p1 = 1D/ ZP1 * (z1Lat + y1);

        double convertedP0 = Units.convert(p0, Units.Angle.TURN, Units.Angle.DEGREE);
        double convertedP1 = Units.convert(p1, Units.Angle.TURN, Units.Angle.DEGREE);

        double A1 = Math.acos(
                1 - ((1 - Math.cos(2*Math.PI*(1 / ZP0))) / Math.pow(Math.cos(convertedP0), 2))
        );

        double A2 = Math.acos(
                1 - ((1 - Math.cos(2*Math.PI*(1 / ZP1))) / Math.pow(Math.cos(convertedP1), 2))
        );

        double ZL0_0 = Math.floor(2 * Math.PI / A1);
        double ZL1_0 = ZL0_0 - 1;

        double ZL0_1 = Math.floor(2 * Math.PI / A2);
        double ZL1_1 = ZL0_1 - 1;

        if (ZL0_0 == ZL0_1) return null;

//        x0 /= Math.pow(2, 17);
//        x1 /= Math.pow(2, 17);

        double zLon = Math.rint(x0 * ZL1_0 - x1 * ZL0_0);

        double z0Lon = zLon < 0 ? zLon + ZP0 : zLon;
        double z1Lon = zLon < 0 ? zLon + ZP1 : zLon;

        double lam0 = 1 / ZL0_0 * (z0Lon + x0);
        double lam1 = 1 / ZL1_0 * (z1Lon + x1);

        return mostRecent == 0 ? new GeoPos((int) p0, (int) lam0) : new GeoPos((int) p0, (int) lam1);
    }
}
