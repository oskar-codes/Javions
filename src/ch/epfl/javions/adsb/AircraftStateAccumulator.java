package ch.epfl.javions.adsb;

public class AircraftStateAccumulator<T extends AircraftStateSetter> {
    private final T stateSetter;
    private AirbornePositionMessage lastEvenMessage;
    private AirbornePositionMessage lastOddMessage;

    public AircraftStateAccumulator(T stateSetter) {
        if (stateSetter == null) throw new NullPointerException("Setter is null");
        this.stateSetter = stateSetter;
    }

    public T stateSetter() {
        return stateSetter;
    }

    public void update(Message message) {
        stateSetter.setLastMessageTimeStampNs(message.timeStampNs());

        switch (message) {
            case AircraftIdentificationMessage aim -> {
                stateSetter.setCategory(aim.category());
                stateSetter.setCallSign(aim.callSign());
            }
            case AirbornePositionMessage apm -> {

                if (apm.parity() == 0) {
                    lastEvenMessage = apm;
                } else {
                    lastOddMessage = apm;
                }
                if (lastEvenMessage != null && lastOddMessage != null && Math.abs(lastEvenMessage.timeStampNs() - lastOddMessage.timeStampNs()) < 1e10) {
                    stateSetter.setPosition(CprDecoder.decodePosition(lastEvenMessage.x(), lastEvenMessage.y(), lastOddMessage.x(), lastOddMessage.y(), apm.parity()));
                }

                stateSetter.setAltitude(apm.altitude());
            }
            case AirborneVelocityMessage avm -> {
                stateSetter.setVelocity(avm.speed());
                stateSetter.setTrackOrHeading(avm.trackOrHeading());
            }
            default -> throw new IllegalStateException("Unexpected value: " + message);
        }
    }
}
