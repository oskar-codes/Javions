package ch.epfl.javions.adsb;

import ch.epfl.javions.Bits;
import ch.epfl.javions.ByteString;
import ch.epfl.javions.Crc24;
import ch.epfl.javions.aircraft.IcaoAddress;

import static ch.epfl.javions.Preconditions.checkArgument;

/**
 * A raw ADS-B message.
 * @param timeStampNs - the time stamp of the message in nanoseconds
 * @param bytes - the bytes of the message
 * @author Oskar Zanota (361595)
 * @author Eddy Rashed (360667)
 */
public record RawMessage(long timeStampNs, ByteString bytes) {
    public static final int LENGTH = 14;
    private static final Crc24 crc = new Crc24(Crc24.GENERATOR);

    /**
     * Creates a new RawMessage. Checks that the time stamp is non-negative and that the bytes are of the correct length.
     * @param timeStampNs - the time stamp of the message in nanoseconds
     * @param bytes - the bytes of the message
     */
    public RawMessage {
        checkArgument(timeStampNs >= 0 && bytes.size() == LENGTH);
    }

    /**
     * Returns a RawMessage from the given bytes, if the CRC is valid. Otherwise, returns null.
     * @param timeStampNs - the time stamp of the message in nanoseconds
     * @param bytes - the bytes of the message
     * @return a RawMessage from the given bytes, if the CRC is valid. Otherwise, returns null
     */
    public static RawMessage of(long timeStampNs, byte[] bytes) {
        if (crc.crc(bytes) != 0) return null;
        return new RawMessage(timeStampNs, new ByteString(bytes));
    }

    /**
     * Returns the size of the message in bytes, if the message is a valid downlink format 17 message. Otherwise, returns 0.
     * @param byte0 - the first byte of the message
     * @return the size of the message in bytes, if the message is a valid downlink format 17 message. Otherwise, returns 0
     */
    public static int size(byte byte0) {
        return (Byte.toUnsignedInt(byte0) >>> 3) == 17 ? LENGTH : 0;
    }

    /**
     * Returns the type code of the message.
     * @param payload - the payload of the message
     * @return the type code of the message
     */
    public static int typeCode(long payload) {
        return Bits.extractUInt(payload, 51, 5);
    }

    /**
     * Returns the DF attribute.
     * @return the DF attribute
     */
    public int downLinkFormat() {
        return bytes.byteAt(0) >>> 3;
    }

    /**
     * Returns the ICAO address of the message.
     * @return the ICAO address of the message
     */
    public IcaoAddress icaoAddress() {
        return new IcaoAddress("%06X".formatted(bytes.bytesInRange(1, 4)));
    }

    /**
     * Returns the payload of the message.
     * @return the payload of the message
     */
    public long payload() {
        return bytes.bytesInRange(4, 11);
    }

    /**
     * Returns the type code of the message.
     * @return the type code of the message
     */
    public int typeCode() {
        return typeCode(payload());
    }
}
