package ch.epfl.javions.adsb;

import ch.epfl.javions.aircraft.IcaoAddress;

import static ch.epfl.javions.Bits.extractUInt;
import static ch.epfl.javions.Bits.testBit;
import static ch.epfl.javions.Preconditions.checkArgument;

//check que code de type soit contenu dans (9,18) et (20,22)
public record AirbornePositionMessage(long timeStampNs, IcaoAddress icaoAddress, double altitude, int parity, double x, double y) implements Message {
    public AirbornePositionMessage {
        if (icaoAddress == null) {
            throw new NullPointerException("Arguments cannot be null");
        }
        checkArgument(timeStampNs >= 0 && (parity == 0 || parity == 1) && x >= 0 && x <= 1 && y >= 0 && y <= 1);
    }

    public AirbornePositionMessage of(RawMessage rawMessage) {
        long cs = rawMessage.payload() & (long) Math.pow(2, 48) - 1;
        int parity = extractUInt(cs, 34, 1); // check if it correctly takes the bit
        int codedAlt = extractUInt(cs, 36, 12);
        if (testBit(codedAlt, 4)) {
            int tempAlt = ((codedAlt >>> 5) << 4) + codedAlt & 0b1111;
            int altitude = -1000 + 25 * tempAlt;
        } else {
            int tempAlt = 0;
            //detangles the bits in case of q=0
            for (int i = 7; i == 4 ; i = (i + 2) % 12) {
                if (testBit(codedAlt, i)) tempAlt +=1;
                tempAlt = tempAlt << 1;
                if (i == 5) i--;
                tempAlt++;
            }
            int LMBAlt = tempAlt & 0b1_1111_1111;
            if ((LMBAlt) == 0 || (LMBAlt) == 5 || (LMBAlt) == 6) {
                int altitude = -1; //Invalid altitude
            }
            if ((LMBAlt) == 7) LMBAlt = 5;
            int RMBAlt = extractUInt(tempAlt, 9,3);
            if (RMBAlt % 2 == 1) RMBAlt = 6 - (RMBAlt - 1);
        }
        // TODO : interpret the rightmost and leftmost bits (RMBAlt and LFMAlt) as gray code to decode altitude
        // TODO : add normalized lat and long to the AirbornePositionMessage Return
        return null;
//        return new AirbornePositionMessage(rawMessage.timeStampNs(), rawMessage.icaoAddress(), , parity)
    }
}