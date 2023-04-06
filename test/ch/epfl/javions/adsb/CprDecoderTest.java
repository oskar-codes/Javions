package ch.epfl.javions.adsb;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.Units;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CprDecoderTest {
    @Test
    public void testCpr() {

        double x0 = 111600 / Math.pow(2, 17);
        double y0 = 94445 / Math.pow(2, 17);
        double x1 = 108865 / Math.pow(2, 17);
        double y1 = 77558 / Math.pow(2, 17);

        GeoPos result = CprDecoder.decodePosition(x0, y0, x1, y1, 0);
        assertEquals(7.47606, Units.convertTo(result.longitude(), Units.Angle.DEGREE), 1e-4);
        assertEquals(46.3233, Units.convertTo(result.latitude(), Units.Angle.DEGREE), 1e-4);


        GeoPos pos = CprDecoder.decodePosition(0.62,0.42,0.62,0.42,0);
        assertEquals(-2.3186, Units.convertTo(pos.longitude(), Units.Angle.DEGREE), 1e-4);
        assertEquals(2.51999, Units.convertTo(pos.latitude(), Units.Angle.DEGREE), 1e-4);

    }
}
