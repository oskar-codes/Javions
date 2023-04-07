package ch.epfl.javions.adsb;

/**
 * A class that parses a {@link RawMessage} into a {@link Message}.
 *
 * @author Oskar Zanota (361595)
 * @author Eddy Rashed (360667)
 */
public class MessageParser {
    private MessageParser() {}

    /**
     * Parses a {@link RawMessage} into a {@link Message}.
     *
     * @param rawMessage the raw message to parse
     * @return the parsed message
     */
    public static Message parse(RawMessage rawMessage) {
        if (rawMessage.typeCode() >= 1 && rawMessage.typeCode() <= 4) {
            return AircraftIdentificationMessage.of(rawMessage);
        }
        if (rawMessage.typeCode() >= 9 && rawMessage.typeCode() <= 18 || rawMessage.typeCode() >= 20 && rawMessage.typeCode() <= 22) {
            return AirbornePositionMessage.of(rawMessage);
        }
        if (rawMessage.typeCode() == 19) {
            return AirborneVelocityMessage.of(rawMessage);
        }
        return null;
    }
}
