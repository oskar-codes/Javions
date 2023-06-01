package ch.epfl.javions.adsb;

import ch.epfl.javions.ByteString;
import ch.epfl.javions.aircraft.AircraftDatabase;
import ch.epfl.javions.gui.AircraftStateManager;
import org.junit.jupiter.api.Test;

import java.io.IOException;

class AircraftStateManagerTest {
    @Test
    void testAircraftStateManager() throws IOException {
        AircraftStateManager manager = new AircraftStateManager(new AircraftDatabase("resources/aircraft.zip"));

        Message m1 = MessageParser.parse(new RawMessage(0, ByteString.ofHexadecimalString("8D49529958B302E6E15FA352306B")));
        Message m2 = MessageParser.parse(new RawMessage(0, ByteString.ofHexadecimalString("8D49529958B302E6B95F836AEF91")));
        Message m3 = MessageParser.parse(new RawMessage(0, ByteString.ofHexadecimalString("8D49529958B30662B356CC3FFCA0")));
        Message m4 = MessageParser.parse(new RawMessage(0, ByteString.ofHexadecimalString("8D49529958B302E6715F4A9FB0A3")));
        Message m5 = MessageParser.parse(new RawMessage(0, ByteString.ofHexadecimalString("8D49529958B302E65F5F3B65C105")));

        manager.updateWithMessage(m1);
        System.out.println(manager.states());

        manager.updateWithMessage(m2);
        System.out.println(manager.states());

        manager.updateWithMessage(m3);
        System.out.println(manager.states());

        manager.updateWithMessage(m4);
        manager.updateWithMessage(m5);

    }

}