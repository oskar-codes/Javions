package ch.epfl.javions;

import static ch.epfl.javions.Preconditions.checkArgument;

/**
 * Non instanciable utility class for mathematical operations
 * @author Eddy Rashed (360667)
 * @author Oskar Zanota (361595)
 */
public final class Math2 {
    private Math2() {}

    /**
     * Clamps a value between a minimum and a maximum
     * @param min minimum value
     * @param v value to clamp
     * @param max maximum value
     * @return min if v < min, max if v > max, v otherwise
     * @throws IllegalArgumentException if min > max
     */
    public static int clamp(int min, int v, int max) {
        checkArgument(min <= max);
        return Math.max(Math.min(max, v), min);
    }

    /**
     * Returns the reciprocal hyperbolic sine of a double value.
     * @param x the value whose asinh is to be returned
     * @return the asinh of x
     */
    public static double asinh(double x) {
        return Math.log(x + Math.sqrt(1 + x * x));
    }
}
