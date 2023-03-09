package ch.epfl.javions.aircraft;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AircraftDataTest {
    @Test
    void requireNonNullWorks() {
        assertThrows(NullPointerException.class, () -> new AircraftData(null, null, null, null, null));
        assertThrows(IllegalArgumentException.class, () -> new IcaoAddress(""));
        assertThrows(IllegalArgumentException.class, () -> new AircraftData(
                new AircraftRegistration(""),
                new AircraftTypeDesignator("SF25"),
                "SCHEIBE SF-25 Falke",
                new AircraftDescription("L1P"),
                WakeTurbulenceCategory.of("L")
        ));
        assertDoesNotThrow(() -> new AircraftData(
                new AircraftRegistration("ZS-GVJ"),
                new AircraftTypeDesignator(""),
                "SCHEIBE SF-25 Falke",
                new AircraftDescription("L1P"),
                WakeTurbulenceCategory.of("L")
        ));
        assertDoesNotThrow(() -> new AircraftData(
                new AircraftRegistration("ZS-GVJ"),
                new AircraftTypeDesignator("SF25"),
                "SCHEIBE SF-25 Falke",
                new AircraftDescription(""),
                WakeTurbulenceCategory.of("L")
        ));
    }
}