package ch.epfl.javions.adsb;

import ch.epfl.javions.ByteString;
import ch.epfl.javions.Units;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AirborneVelocityMessageTest {
    @Test
    void testVelocityWithSubtype3Or4() {
         AirborneVelocityMessage message = AirborneVelocityMessage.of(new RawMessage(
                 0, ByteString.ofHexadecimalString("8DA05F219B06B6AF189400CBC33F")
         ));

         assertEquals(message.trackOrHeading(), 4.25833, 1e-5);
         assertEquals(Units.convert(message.speed() * 3.6, Units.Speed.KILOMETER_PER_HOUR, Units.Speed.KNOT), 375, 1e-5);
    }

    @Test
    void testVelocityWithNullIcaoAddress() {
        RawMessage rawMessage = new RawMessage(0, ByteString.ofHexadecimalString("8D000000F8210002004BB8B1F1AC"));
        AirborneVelocityMessage message = AirborneVelocityMessage.of(rawMessage);

        assertNull(message);
    }
}