package ch.epfl.javions.adsb;

import ch.epfl.javions.ByteString;
import ch.epfl.javions.Crc24;
import ch.epfl.javions.aircraft.IcaoAddress;

import static ch.epfl.javions.Preconditions.checkArgument;
public record RawMessage(long timeStampNs, ByteString bytes) {
    public static final int LENGTH = 14;
    private static final Crc24 crc = new Crc24(Crc24.GENERATOR);
    public RawMessage {
        checkArgument(timeStampNs >= 0 && bytes.size() == LENGTH);
    }

    public static RawMessage of(long timeStampNs, byte[] bytes) {
        if (crc.crc(bytes) != 0) return null;
        return new RawMessage(timeStampNs, new ByteString(bytes));
    }

    public static int size(byte byte0) {
        return (Byte.toUnsignedInt(byte0) >>> 3) == 17 ? LENGTH : 0;
    }

    public static int typeCode(long payload) {
        return (int) (payload >>> 51);
    }

    public int downLinkFormat() {
        return bytes.byteAt(0) >>> 3;
    }

    public IcaoAddress icaoAddress() {
        return new IcaoAddress(Long.toString(bytes.bytesInRange(1, 4), 16).toUpperCase());
    }
    public long payload() {
        return bytes.bytesInRange(4, 11);
    }
    public int typeCode() {
        return typeCode(payload());
    }
}
