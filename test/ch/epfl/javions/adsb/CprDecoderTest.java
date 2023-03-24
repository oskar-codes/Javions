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

        GeoPos pos = CprDecoder.decodePosition(0.62,0.42,0.62,0.42,0);
        System.out.println(pos);

    }
}
