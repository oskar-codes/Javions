package ch.epfl.javions.gui;

import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;

/**
 * Controller for the status line of the Javions application.
 * The status line displays the number of aircraft and messages received.
 * It is updated by the {@link Main} class.
 * @author Oskar Zanota (361595)
 * @author Eddy Rashed (360667)
 */
public final class StatusLineController {
    private final BorderPane pane;
    private final IntegerProperty aircraftCountProperty;
    private final LongProperty messageCountProperty;

    /**
     * Constructor of the status line controller.
     * Initializes the status line with bindings to the aircraft count and message count properties.
     */
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

    /**
     * Getter for the pane of the status line.
     * @return the pane of the status line.
     */
    public BorderPane pane() {
        return pane;
    }

    /**
     * Getter for the aircraft count property.
     * @return the aircraft count property.
     */
    public IntegerProperty aircraftCountProperty() {
        return aircraftCountProperty;
    }

    /**
     * Getter for the message count property.
     * @return the message count property.
     */
    public LongProperty messageCountProperty() {
        return messageCountProperty;
    }
}
