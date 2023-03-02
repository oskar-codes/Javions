package ch.epfl.javions;

import org.junit.jupiter.api.Test;
import java.util.HexFormat;
import static org.junit.jupiter.api.Assertions.*;

public class Crc24Test {
    @Test
    public void testCrc24() {
        Crc24 crc24 = new Crc24(Crc24.GENERATOR);

        String[] tests = new String[]{
                "8D392AE499107FB5C00439|035DB8",
                "8D4D2286EA428867291C08|EE2EC6",
                "8D3950C69914B232880436|BC63D3",
                "8D4B17E399893E15C09C21|9FC014",
                "8D4B18F4231445F2DB63A0|DEEB82",
                "8D495293F82300020049B8|111203"
        };
        for (String test : tests) {
            String[] split = test.split("\\|");
            String message = split[0];
            String expected = split[1];
            int crc = crc24.crc(HexFormat.of().parseHex(message));
            assertEquals(Integer.parseInt(expected, 16), crc);

            int crcZero = crc24.crc(HexFormat.of().parseHex(message + expected));
            assertEquals(0, crcZero);
        }
    }
}
