package ch.epfl.javions.adsb;

import ch.epfl.javions.GeoPos;

/**
 * An accumulator for aircraft state.
 *
 * @param <T> - the type of the state setter
 * @author Oskar Zanota (361595)
 * @author Eddy Rashed (360667)
 */
public class AircraftStateAccumulator<T extends AircraftStateSetter> {
    private final T stateSetter;
    private AirbornePositionMessage lastEvenMessage;
    private AirbornePositionMessage lastOddMessage;

    /**
     * Constructs an accumulator for aircraft state.
     *
     * @param stateSetter the state setter
     */
    public AircraftStateAccumulator(T stateSetter) {
        if (stateSetter == null) throw new NullPointerException("Setter is null");
        this.stateSetter = stateSetter;
    }

    /**
     * Returns the state setter.
     *
     * @return the state setter
     */
    public T stateSetter() {
        return stateSetter;
    }

    /**
     * Updates the state of the aircraft with the information given by the message.
     *
     * @param message the given message: an {@link AircraftIdentificationMessage}, an {@link AirbornePositionMessage} or an {@link AirborneVelocityMessage}
     */
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
                if (lastEvenMessage != null && lastOddMessage != null && Math.abs(lastEvenMessage.timeStampNs() - lastOddMessage.timeStampNs()) <= 1e10) {
                    GeoPos position = CprDecoder.decodePosition(lastEvenMessage.x(), lastEvenMessage.y(), lastOddMessage.x(), lastOddMessage.y(), apm.parity());
                    if (position != null) stateSetter.setPosition(position);
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
