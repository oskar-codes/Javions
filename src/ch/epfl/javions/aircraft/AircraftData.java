package ch.epfl.javions.aircraft;

import java.util.Objects;

/**
 * Record representing the data of an aircraft.
 *
 * @author Eddy Rashed (360667)
 * @author Oskar Zanota (361595)
 */
public record AircraftData(AircraftRegistration registration,
                           AircraftTypeDesignator typeDesignator,
                           String model,
                           AircraftDescription description,
                           WakeTurbulenceCategory wakeTurbulenceCategory) {
    /**
     * Constructs an AircraftData object.
     *
     * @param registration           must not be null
     * @param typeDesignator         must not be null
     * @param model                  must not be null
     * @param description            must not be null
     * @param wakeTurbulenceCategory must not be null
     */
    public AircraftData {
        Objects.requireNonNull(registration);
        Objects.requireNonNull(typeDesignator);
        Objects.requireNonNull(model);
        Objects.requireNonNull(description);
        Objects.requireNonNull(wakeTurbulenceCategory);
    }
}
