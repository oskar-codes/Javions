package ch.epfl.javions.adsb;

import ch.epfl.javions.aircraft.AircraftDatabase;
import ch.epfl.javions.aircraft.IcaoAddress;
import ch.epfl.javions.gui.ObservableAircraftState;
import javafx.beans.property.ReadOnlySetProperty;
import javafx.beans.property.SetProperty;
import javafx.beans.property.SimpleSetProperty;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public final class AircraftStateManager {
    private final AircraftDatabase database;
    private final Map<IcaoAddress, AircraftStateAccumulator<ObservableAircraftState>> table = new HashMap<>();
    private final SetProperty<AircraftStateAccumulator<ObservableAircraftState>> states = new SimpleSetProperty<>();

    public AircraftStateManager(AircraftDatabase database) {
        this.database = database;
    }

    public ReadOnlySetProperty<AircraftStateAccumulator<ObservableAircraftState>> states() {
        return states;
    }

    public void updateWithMessage(Message message) throws IOException {
        IcaoAddress address = message.icaoAddress();
        if (table.containsKey(address)) {
            table.get(address).update(message);
        } else {
            AircraftStateAccumulator<ObservableAircraftState> obj = new AircraftStateAccumulator<>(new ObservableAircraftState(address, database.get(address)));
            table.put(address, obj);
        }
    }
}
