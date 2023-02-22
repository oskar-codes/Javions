package ch.epfl.javions;

public final class Math2 {
    private Math2() {}
    public static int clamp(int min, int v, int max) {
        if (min > max) throw new IllegalArgumentException();
        return Math.max(Math.min(max, v), min);
    }

    public static double asinh(double x) {
        return Math.log(x + Math.sqrt(1 + x * x));
    }
}
