package ch.epfl.javions.adsb;

public class MessageParser {
    private MessageParser() {}

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
