package ch.epfl.javions.gui;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.adsb.AircraftStateSetter;
import ch.epfl.javions.adsb.CallSign;
import ch.epfl.javions.aircraft.AircraftData;
import ch.epfl.javions.aircraft.IcaoAddress;
import javafx.beans.property.*;
import javafx.collections.ObservableList;

import java.util.ArrayList;

import static java.lang.Double.NaN;
import static javafx.collections.FXCollections.observableArrayList;
import static javafx.collections.FXCollections.unmodifiableObservableList;

/**
 * A class representing an observable aircraft state.
 * @author Oskar Zanota (361595)
 * @author Eddy Rashed (360667)
 */
public final class ObservableAircraftState implements AircraftStateSetter {

    /**
     * A class representing an airborne position.
     * @param position the position
     * @param altitude the altitude
     */
    public record AirbornePos(GeoPos position, double altitude) {
    }

    private final IcaoAddress icaoAddress;
    private final AircraftData aircraftData;
    private final LongProperty lastMessageTimeStampNs = new SimpleLongProperty();
    private final IntegerProperty category = new SimpleIntegerProperty();
    private final ObjectProperty<CallSign> callSign = new SimpleObjectProperty<>();
    private final ObjectProperty<GeoPos> position = new SimpleObjectProperty<>();
    private final ObservableList<AirbornePos> trajectory = observableArrayList();
    private final ObservableList<AirbornePos> unmodifiableTrajectory = unmodifiableObservableList(trajectory);
    private long lastTrajectoryAdd = -1;
    private final DoubleProperty altitude = new SimpleDoubleProperty(NaN);
    private final DoubleProperty velocity = new SimpleDoubleProperty(NaN);
    private final DoubleProperty trackOrHeading = new SimpleDoubleProperty();

    /**
     * Creates a new observable aircraft state.
     * @param icaoAddress the ICAO address
     * @param data the aircraft data
     */
    public ObservableAircraftState(IcaoAddress icaoAddress, AircraftData data) {
        this.icaoAddress = icaoAddress;
        this.aircraftData = data;
    }

    /**
     * Getter for the ICACO address.
     * @return the ICAO address
     */
    public IcaoAddress getIcaoAddress() {
        return icaoAddress;
    }

    /**
     * Getter for the aircraft data.
     * @return the aircraft data
     */
    public AircraftData getAircraftData() {
        return aircraftData;
    }

    /**
     * Getter for the position property. The position property is read-only.
     * @return the position property
     */
    public ReadOnlyLongProperty lastMessageTimeStampNsProperty() {
        return lastMessageTimeStampNs;
    }

    /**
     * Getter for the last message time stamp.
     * @return the last message time stamp
     */
    public long getLastMessageTimeStampNs() {
        return lastMessageTimeStampNs.get();
    }

    /**
     * Setter for the last message time stamp.
     * @param lastMessageTimeStampNs - the last message time stamp in nanoseconds
     */
    public void setLastMessageTimeStampNs(long lastMessageTimeStampNs) {
        this.lastMessageTimeStampNs.set(lastMessageTimeStampNs);
    }

    /**
     * Getter for the category property. The category property is read-only.
     * @return the category property
     */
    public ReadOnlyIntegerProperty categoryProperty() {
        return category;
    }

    /**
     * Getter for the category.
     * @return the category
     */
    public int getCategory() {
        return category.get();
    }

    /**
     * Setter for the category.
     * @param category - the aircraft category
     */
    public void setCategory(int category) {
        this.category.set(category);
    }

    /**
     * Getter for the call sign property. The call sign property is read-only.
     * @return the call sign property
     */
    public ReadOnlyObjectProperty<CallSign> callSignProperty() {
        return callSign;
    }

    /**
     * Getter for the call sign.
     * @return the call sign
     */
    public CallSign getCallSign() {
        return callSign.get();
    }

    /**
     * Setter for the call sign.
     * @param callSign - the call sign
     */
    public void setCallSign(CallSign callSign) {
        this.callSign.set(callSign);
    }

    /**
     * Getter for the position property. The position property is read-only.
     * @return the position property
     */
    public ReadOnlyObjectProperty<GeoPos> positionProperty() {
        return position;
    }

    /**
     * Getter for the position.
     * @return the position
     */
    public GeoPos getPosition() {
        return position.get();
    }

    /**
     * Setter for the position.
     * @param position - the aircraft's position
     */
    public void setPosition(GeoPos position) {
        boolean different = !position.equals(getPosition());
        this.position.set(position);
        if (different) updateTrajectory();
    }

    /**
     * Getter for the trajectory. The trajectory is an unmodifiable observable list.
     * @return the trajectory
     */
    public ObservableList<AirbornePos> trajectoryProperty() {
        return unmodifiableTrajectory;
    }

    /**
     * Getter for the trajectory as an array list.
     * @return the trajectory as an array list
     */
    public ArrayList<AirbornePos> getTrajectory() {
        return new ArrayList<>(trajectory);
    }

    /**
     * Getter for the altitude property. The altitude property is read-only.
     * @return the altitude property
     */
    public ReadOnlyDoubleProperty altitudeProperty() {
        return altitude;
    }

    /**
     * Getter for the altitude.
     * @return the altitude
     */
    public double getAltitude() {
        return altitude.get();
    }

    /**
     * Setter for the altitude.
     * @param altitude - the aircraft's altitude
     */
    public void setAltitude(double altitude) {
        boolean different = altitude != getAltitude();
        this.altitude.set(altitude);
        if (different) updateTrajectory();
    }

    /**
     * Getter for the velocity property. The velocity property is read-only.
     * @return the velocity property
     */
    public ReadOnlyDoubleProperty velocityProperty() {
        return velocity;
    }

    /**
     * Getter for the velocity.
     * @return the velocity
     */
    public double getVelocity() {
        return velocity.get();
    }

    /**
     * Setter for the velocity.
     * @param velocity - the aircraft's velocity
     */
    public void setVelocity(double velocity) {
        this.velocity.set(velocity);
    }

    /**
     * Getter for the track or heading property. The track or heading property is read-only.
     * @return the track or heading property
     */
    public ReadOnlyDoubleProperty trackOrHeadingProperty() {
        return trackOrHeading;
    }

    /**
     * Getter for the track or heading.
     * @return the track or heading
     */
    public double getTrackOrHeading() {
        return trackOrHeading.get();
    }

    /**
     * Setter for the track or heading.
     * @param trackOrHeading - the aircraft's track or heading
     */
    public void setTrackOrHeading(double trackOrHeading) {
        this.trackOrHeading.set(trackOrHeading);
    }

    // TODO: write this with new method
    /**
     * Updates the trajectory. This method is called whenever the aircraft's position or altitude changes.
     */
    private void updateTrajectory() {
//        if (!isNaN(getAltitude())) {
//            trajectory.add(new AirbornePos(getPosition(), getAltitude()));
//            lastTrajectoryAdd = getLastMessageTimeStampNs();
//            return;
//        }
//        if (getPosition() == null) return;
//        if (trajectory.size() == 0) {
//            trajectory.add(new AirbornePos(getPosition(), getAltitude()));
//            lastTrajectoryAdd = getLastMessageTimeStampNs();
//            return;
//        }
//        if (getLastMessageTimeStampNs() == lastTrajectoryAdd) {
//            trajectory.set(trajectory.size() - 1, new AirbornePos(getPosition(), getAltitude()));
//        }

        if ((trajectory.size() == 0 || !trajectory.get(trajectory.size() - 1).position.equals(getPosition())
            ) && getPosition() != null) {
            trajectory.add(new AirbornePos(getPosition(), getAltitude()));
            lastTrajectoryAdd = getLastMessageTimeStampNs();
        } else if (getLastMessageTimeStampNs() == lastTrajectoryAdd) {
            trajectory.set(trajectory.size() - 1, new AirbornePos(getPosition(), getAltitude()));
        }
    }
}
