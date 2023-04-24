package ch.epfl.javions.adsb;

import ch.epfl.javions.aircraft.AircraftDatabase;
import ch.epfl.javions.aircraft.IcaoAddress;
import ch.epfl.javions.gui.ObservableAircraftState;
import javafx.collections.ObservableSet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static javafx.collections.FXCollections.observableSet;
import static javafx.collections.FXCollections.unmodifiableObservableSet;

public final class AircraftStateManager {
    private final AircraftDatabase database;
    private final Map<IcaoAddress, AircraftStateAccumulator<ObservableAircraftState>> table = new HashMap<>();
    private final ObservableSet<ObservableAircraftState> states = observableSet();
    public AircraftStateManager(AircraftDatabase database) {
        this.database = database;
    }

    // The states() method that returns the state attribute still Observable but ReadOnly
    public ObservableSet<ObservableAircraftState> states() {
        return unmodifiableObservableSet(states);
    }

    public void updateWithMessage(Message message) throws IOException {
        IcaoAddress address = message.icaoAddress();
        if (table.containsKey(address)) {
            table.get(address).update(message);
            if (table.get(address).stateSetter().getPosition() != null) {
                states.add(table.get(address).stateSetter());
            }
        } else {
            AircraftStateAccumulator<ObservableAircraftState> obj = new AircraftStateAccumulator<>(new ObservableAircraftState(address, database.get(address)));
            obj.update(message);
            table.put(address, obj);
            if (obj.stateSetter().getPosition() != null) states.add(obj.stateSetter());
        }
        purge(message);
    }

    public void purge(Message message) {
        states.removeIf(state -> state.getLastMessageTimeStampNs() < message.timeStampNs() - 60 * 1e9);
    }
}
