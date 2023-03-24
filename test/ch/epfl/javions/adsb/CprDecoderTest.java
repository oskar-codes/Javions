package ch.epfl.javions.adsb;

import ch.epfl.javions.GeoPos;
import org.junit.jupiter.api.Test;

public class CprDecoderTest {
    @Test
    public void testCpr() {

        double x0 = 111600 / Math.pow(2, 17);
        double y0 = 94445 / Math.pow(2, 17);
        double x1 = 108865 / Math.pow(2, 17);
        double y1 = 77558 / Math.pow(2, 17);

        GeoPos result = CprDecoder.decodePosition(x0, y0, x1, y1, 0);

        System.out.println(result);

//        assertEquals(7.47606, Units.convert(result.latitude(), Units.Angle.DEGREE, Units.Angle.DEGREE), 0.00001);

    }
}
