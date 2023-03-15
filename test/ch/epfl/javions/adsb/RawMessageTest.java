package ch.epfl.javions.adsb;

import ch.epfl.javions.ByteString;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RawMessageTest {
    private static final byte[] bytes = new byte[]{
            (byte)0x8D,
            (byte)0x4B,
            (byte)0x17,
            (byte)0xE5,
            (byte)0xF8,
            (byte)0x21,
            (byte)0x00,
            (byte)0x02,
            (byte)0x00,
            (byte)0x4B,
            (byte)0xB8,
            (byte)0xB1,
            (byte)0xF1,
            (byte)0xAC
    };
    @Test
    void testRawMessage() {
        RawMessage message = new RawMessage(0, new ByteString(bytes));
        assertEquals(0, message.timeStampNs());
        assertEquals(14, message.bytes().size());

        assertEquals(17, message.downLinkFormat());
        assertEquals("4B17E5", message.icaoAddress().string());
        assertEquals(0xF8210002004BB8L, message.payload());
        assertEquals(31, message.typeCode());
    }

    @Test
    void testConstructor() {
        assertDoesNotThrow(() -> new RawMessage(0, new ByteString(new byte[14])));
        assertThrows(IllegalArgumentException.class, () -> new RawMessage(0, new ByteString(new byte[]{14})));
        assertThrows(IllegalArgumentException.class, () -> new RawMessage(-1, new ByteString(bytes)));
    }
}