package ch.epfl.javions;

import ch.epfl.javions.adsb.CallSign;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CallSignTest {
    @Test
    void testConstructor() {
        assertDoesNotThrow(() -> new CallSign(""));
        assertThrows(IllegalArgumentException.class, () -> new CallSign("abcdefghijklmnopqrstuvwxyz"));
    }
}