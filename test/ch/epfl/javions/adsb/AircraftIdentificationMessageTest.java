package ch.epfl.javions.adsb;

import ch.epfl.javions.ByteString;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AircraftIdentificationMessageTest {
    @Test
    void testInvalidCallSign() {
        assertNull(AircraftIdentificationMessage.of(
                new RawMessage(0, ByteString.ofHexadecimalString("8D4D222823499000284820323B81"))
        ));
        assertNotNull(AircraftIdentificationMessage.of(
                new RawMessage(0, ByteString.ofHexadecimalString("8D4D2228234994B7284820323B81"))
        ));
    }

}