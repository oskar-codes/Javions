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

/**
 * A class that manages the aircraft states.
 * @author Oskar Zanota (361595)
 * @author Eddy Rashed (360667)
 */
public final class AircraftStateManager {
    private final AircraftDatabase database;
    private final Map<IcaoAddress, AircraftStateAccumulator<ObservableAircraftState>> table = new HashMap<>();
    private final ObservableSet<ObservableAircraftState> states = observableSet();
    private final ObservableSet<ObservableAircraftState> unmodifiableStates = unmodifiableObservableSet(states);
    public AircraftStateManager(AircraftDatabase database) {
        this.database = database;
    }

    /**
     * Returns the set of aircraft states. The returned set is unmodifiable.
     * @return the set of aircraft states
     */
    public ObservableSet<ObservableAircraftState> states() {
        return unmodifiableStates;
    }

    /**
     * Updates the aircraft state with the given message.
     * @param message the message
     * @throws IOException if an I/O error occurs
     */
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
            if (obj.stateSetter().getPosition() != null) {
                states.add(obj.stateSetter());
            }
        }
    }

    /**
     * Purges the aircraft states that are older than 60 seconds.
     * @param message the message.
     */
    public void purge(Message message) {
        states.removeIf(state -> state.getLastMessageTimeStampNs() < message.timeStampNs() - 60 * 1e9);
        table.entrySet().removeIf(entry -> entry.getValue().stateSetter().getLastMessageTimeStampNs() < message.timeStampNs() - 60 * 1e9);
    }
}
