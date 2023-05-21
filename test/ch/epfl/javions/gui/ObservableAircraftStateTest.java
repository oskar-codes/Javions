package ch.epfl.javions.gui;

import org.junit.jupiter.api.Test;

class ObservableAircraftStateTest {

    @Test
    void getPosition() {
        ObservableAircraftState state = new ObservableAircraftState(null, null);
        System.out.println(state.getPosition());
    }
}