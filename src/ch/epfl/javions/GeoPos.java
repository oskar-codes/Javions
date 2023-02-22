package ch.epfl.javions;

public record GeoPos(int longitudeT32, int latitudeT32) {
    public static boolean isValidLatitudet32(int latitudeT32) {
        return latitudeT32 >= Math.scalb(-1,30) && latitudeT32 <= Math.scalb(1,30);
    }
    public double longitude() {
        return Units.convertFrom(longitudeT32, Units.Angle.T32);
    }
    public double latitude() {
        return Units.convertFrom(longitudeT32, Units.Angle.T32);
    }
    @Override
    public String toString() {
        return "(" + Units.convertTo(longitude(), Units.Angle.DEGREE) + "°, " + Units.convertTo(latitude(), Units.Angle.DEGREE) + "°)";
    }
}