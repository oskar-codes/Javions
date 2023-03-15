package ch.epfl.javions;

/**
 * Non-instantiable utility class for preconditions
 * @author Eddy Rashed (360667)
 * @author Oskar Zanota (361595)
 */
public final class Preconditions {
    private Preconditions() {}

    /**
     * Checks if the given boolean is true, if not throws an IllegalArgumentException
     * @param shouldBeTrue the boolean to check
     * @throws IllegalArgumentException if the boolean is false
     */
    public static void checkArgument(boolean shouldBeTrue) {
        if (!shouldBeTrue) throw new IllegalArgumentException("Argument check failed");
    }
}
