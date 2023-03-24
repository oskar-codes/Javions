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

        double zLat = Math.rint(y0 * ZP1 - y1 * ZP0);

        double z0Lat = zLat < 0 ? zLat + ZP0 : zLat;
        double z1Lat = zLat < 0 ? zLat + ZP1 : zLat;

        double p0 = 1D / ZP0 * (z0Lat + y0);
        double p1 = 1D / ZP1 * (z1Lat + y1);

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

        double ZL0_0 = Double.isNaN(A1) ? 1 : Math.floor((2 * Math.PI) / A1);
        double ZL1_0 = ZL0_0 - 1;

        double ZL0_1 = Double.isNaN(A2) ? 1 : Math.floor((2 * Math.PI) / A2);
        double ZL1_1 = ZL0_1 - 1;

//        if (ZL0_0 != ZL0_1) return null;

        if (ZL0_0 == 1) {
            double x0T32 = Units.convert(x0, Units.Angle.TURN, Units.Angle.T32);
            double x1T32 = Units.convert(x1, Units.Angle.TURN, Units.Angle.T32);
            return mostRecent == 0 ?
                    new GeoPos((int) Math.round(x0T32), (int) Math.round(p0T32)) :
                    new GeoPos((int) Math.round(x1T32), (int) Math.round(p1T32));
        }

        double zLon = Math.rint(x0 * ZL1_0 - x1 * ZL0_0);

        double z0Lon = zLon < 0 ? zLon + ZP0 : zLon;
        double z1Lon = zLon < 0 ? zLon + ZP1 : zLon;
        double lam0 = 1 / ZL0_0 * (z0Lon + x0);
        double lam1 = 1 / ZL1_0 * (z1Lon + x1);

        if (lam0 > .25) lam0 -= 0.25;
        if (lam1 > .25) lam1 -= 0.25;

        double lam0T32 = Units.convert(lam0, Units.Angle.TURN, Units.Angle.T32);
        double lam1T32 = Units.convert(lam1, Units.Angle.TURN, Units.Angle.T32);


        return mostRecent == 0 ?
                new GeoPos((int) Math.round(lam0T32), (int) Math.round(p0T32)) :
                new GeoPos((int) Math.round(lam1T32), (int) Math.round(p1T32));
    }
}
