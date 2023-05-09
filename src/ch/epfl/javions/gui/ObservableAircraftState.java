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

public final class ObservableAircraftState implements AircraftStateSetter {

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

    public ObservableAircraftState(IcaoAddress icaoAddress, AircraftData data) {
        this.icaoAddress = icaoAddress;
        this.aircraftData = data;
    }

    public IcaoAddress getIcaoAddress() {
        return icaoAddress;
    }
    public AircraftData getAircraftData() {
        return aircraftData;
    }

    public ReadOnlyLongProperty lastMessageTimeStampNsProperty() {
        return lastMessageTimeStampNs;
    }

    public long getLastMessageTimeStampNs() {
        return lastMessageTimeStampNs.get();
    }

    public void setLastMessageTimeStampNs(long lastMessageTimeStampNs) {
        this.lastMessageTimeStampNs.set(lastMessageTimeStampNs);
    }

    public ReadOnlyIntegerProperty categoryProperty() {
        return category;
    }

    public int getCategory() {
        return category.get();
    }

    public void setCategory(int category) {
        this.category.set(category);
    }

    public ReadOnlyObjectProperty<CallSign> callSignProperty() {
        return callSign;
    }

    public CallSign getCallSign() {
        return callSign.get();
    }

    public void setCallSign(CallSign callSign) {
        this.callSign.set(callSign);
    }

    public ReadOnlyObjectProperty<GeoPos> positionProperty() {
        return position;
    }

    public GeoPos getPosition() {
        return position.get();
    }

    public void setPosition(GeoPos position) {
        boolean different = !position.equals(getPosition());
        this.position.set(position);
        if (different) updateTrajectory();
    }

    public ObservableList<AirbornePos> trajectoryProperty() {
        return unmodifiableTrajectory;
    }

    public ArrayList<AirbornePos> getTrajectory() {
        return new ArrayList<>(trajectory);
    }

    public ReadOnlyDoubleProperty altitudeProperty() {
        return altitude;
    }

    public double getAltitude() {
        return altitude.get();
    }

    public void setAltitude(double altitude) {
        boolean different = altitude != getAltitude();
        this.altitude.set(altitude);
        if (different) updateTrajectory();
    }


    public ReadOnlyDoubleProperty velocityProperty() {
        return velocity;
    }

    public double getVelocity() {
        return velocity.get();
    }

    public void setVelocity(double velocity) {
        this.velocity.set(velocity);
    }

    public ReadOnlyDoubleProperty trackOrHeadingProperty() {
        return trackOrHeading;
    }

    public double getTrackOrHeading() {
        return trackOrHeading.get();
    }

    public void setTrackOrHeading(double trackOrHeading) {
        this.trackOrHeading.set(trackOrHeading);
    }

    private void updateTrajectory() {
        if ((trajectory.size() == 0 || !trajectory.get(trajectory.size() - 1).position.equals(getPosition())
            ) && getPosition() != null) {
            trajectory.add(new AirbornePos(getPosition(), getAltitude()));
            lastTrajectoryAdd = getLastMessageTimeStampNs();
        } else if (getLastMessageTimeStampNs() == lastTrajectoryAdd) {
            trajectory.set(trajectory.size() - 1, new AirbornePos(getPosition(), getAltitude()));
        }
    }
}
