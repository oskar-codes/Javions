package ch.epfl.javions;

import ch.epfl.javions.aircraft.AircraftData;
import ch.epfl.javions.aircraft.AircraftDatabase;
import ch.epfl.javions.aircraft.IcaoAddress;
import ch.epfl.javions.aircraft.WakeTurbulenceCategory;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class AircraftDatabaseTest {
    // TODO: write null test for constructor

    @Test
    void get() throws IOException {
        AircraftDatabase db = new AircraftDatabase("/aircraft.zip");

        assertThrows(NullPointerException.class, () -> db.get(null));

        AircraftData data = db.get(new IcaoAddress("009214"));

        assertEquals("ZS-GVJ", data.registration().str());
        assertEquals("SF25", data.typeDesignator().str());
        assertEquals("SCHEIBE SF-25 Falke", data.model());
        assertEquals("L1P", data.description().str());
        assertEquals(WakeTurbulenceCategory.LIGHT, data.wakeTurbulenceCategory());
    }
}