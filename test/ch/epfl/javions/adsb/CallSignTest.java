package ch.epfl.javions.adsb;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CallSignTest {
    @Test
    void testConstructor() {
        assertDoesNotThrow(() -> new CallSign(""));
        assertThrows(IllegalArgumentException.class, () -> new CallSign("abcdefghijklmnopqrstuvwxyz"));
    }
}