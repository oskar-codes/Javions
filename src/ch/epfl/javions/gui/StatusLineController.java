package ch.epfl.javions.gui;

import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;

public final class StatusLineController {
    private final BorderPane pane;
    private final IntegerProperty aircraftCountProperty;
    private final LongProperty messageCountProperty;
    public StatusLineController() {
        this.pane = new BorderPane();
        aircraftCountProperty = new SimpleIntegerProperty();
        messageCountProperty = new SimpleLongProperty();

        this.pane.getStylesheets().add("status.css");

        Text left = new Text();
        Text right = new Text();
        pane.setLeft(left);
        pane.setRight(right);

        left.textProperty().bind(Bindings.format("Aéronefs visibles : %d", aircraftCountProperty));
        right.textProperty().bind(Bindings.format("Messages reçus : %d", messageCountProperty));
    }

    public BorderPane pane() {
        return pane;
    }
    public IntegerProperty aircraftCountProperty() {
        return aircraftCountProperty;
    }
    public LongProperty messageCountProperty() {
        return messageCountProperty;
    }
}
