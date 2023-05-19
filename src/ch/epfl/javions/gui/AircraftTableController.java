package ch.epfl.javions.gui;

import ch.epfl.javions.Units;
import ch.epfl.javions.adsb.CallSign;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Comparator;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Controller for the aircraft table.
 * @author Oskar Zanota (361595)
 * @author Eddy Rashed (360667)
 */
public final class AircraftTableController {
    private final TableView<ObservableAircraftState> table;
    // Determines whether the table should scroll to the selected item or not
    // (used to prevent scrolling when the user clicks on the table)
    private boolean shouldScroll = true;
    private Consumer<ObservableAircraftState> consumer;
    private final static NumberFormat formatter = NumberFormat.getInstance(new Locale("fr", "CH"));

    /**
     * Creates a new aircraft table controller.
     * @param states the set of aircraft states. Cannot be null.
     * @param state the selected aircraft state
     */
    public AircraftTableController(ObservableSet<ObservableAircraftState> states,
                                   ObjectProperty<ObservableAircraftState> state) {
        Objects.requireNonNull(states);

        this.table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_SUBSEQUENT_COLUMNS);
        table.setTableMenuButtonVisible(true);
        table.getStylesheets().add("table.css");

        createColumns();

        // Update the table when the set of aircraft states changes
        states.addListener((SetChangeListener<ObservableAircraftState>) change -> {
            if (change.wasAdded()) {
                table.getItems().add(change.getElementAdded());
                table.sort();
            } else if (change.wasRemoved()) {
                table.getItems().remove(change.getElementRemoved());
            }
        });

        // Update the table when the selected aircraft state changes
        state.addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                table.getSelectionModel().select(newValue);
                if (!newValue.equals(oldValue) && shouldScroll) {
                    table.scrollTo(newValue);
                }
                shouldScroll = true;
                table.requestFocus();
            }
        });

        // Handle table clicks
        table.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                switch (event.getClickCount()) {
                    case 1 -> {
                        ObservableAircraftState e = table.getSelectionModel().getSelectedItem();
                        if (e != null) {
                            shouldScroll = false;
                            state.set(e);
                        }
                    }
                    case 2 -> {
                        ObservableAircraftState e = table.getSelectionModel().getSelectedItem();
                        if (consumer != null && e != null) {
                            consumer.accept(e);
                        }
                    }
                }
            }
        });
    }

    /**
     * Sets the consumer for double clicks.
     * @param consumer the consumer
     */
    public void setOnDoubleClick(Consumer<ObservableAircraftState> consumer) {
        this.consumer = consumer;
    }

    /**
     * Creates the columns of the table.
     */
    private void createColumns() {

        /* ### TEXT COLUMNS ### */
        // ICAO ADDRESS COLUMN
        TableColumn<ObservableAircraftState, String> icaoAddressColumn = new TableColumn<>("OACI");
        icaoAddressColumn.setCellValueFactory(param ->
                new ReadOnlyObjectWrapper<>(param.getValue().getIcaoAddress().string()));
        icaoAddressColumn.setPrefWidth(60);

        // CALL SIGN COLUMN
        TableColumn<ObservableAircraftState, String> callSignColumn = new TableColumn<>("Indicatif");
        callSignColumn.setCellValueFactory(param -> param.getValue().callSignProperty().map(CallSign::string));
        callSignColumn.setPrefWidth(70);

        // REGISTRATION COLUMN
        TableColumn<ObservableAircraftState, String> registrationColumn = new TableColumn<>("Immatriculation");
        registrationColumn.setCellValueFactory(param -> {

            return param.getValue().getAircraftData() == null ?
                    new ReadOnlyObjectWrapper<>("") :
                    new ReadOnlyObjectWrapper<>(param.getValue().getAircraftData().registration().string());

//            if (param.getValue().getAircraftData() == null) {
//                return new ReadOnlyObjectWrapper<>("");
//            }
//            return new ReadOnlyObjectWrapper<>(param.getValue().getAircraftData().registration().string());
        });
        registrationColumn.setPrefWidth(90);

        // MODEL COLUMN
        TableColumn<ObservableAircraftState, String> modelColumn = new TableColumn<>("Modèle");
        modelColumn.setCellValueFactory(param -> {
            if (param.getValue().getAircraftData() == null) {
                return new ReadOnlyObjectWrapper<>("");
            }
            return new ReadOnlyObjectWrapper<>(param.getValue().getAircraftData().model());
        });
        modelColumn.setPrefWidth(230);

        // TYPE COLUMN
        TableColumn<ObservableAircraftState, String> typeColumn = new TableColumn<>("Type");
        typeColumn.setCellValueFactory(param -> {
            if (param.getValue().getAircraftData() == null) {
                return new ReadOnlyObjectWrapper<>("");
            }
            return new ReadOnlyObjectWrapper<>(param.getValue().getAircraftData().typeDesignator().string());
        });
        typeColumn.setPrefWidth(50);

        // DESCRIPTION COLUMN
        TableColumn<ObservableAircraftState, String> descriptionColumn = new TableColumn<>("Description");
        descriptionColumn.setCellValueFactory(param -> {
            if (param.getValue().getAircraftData() == null) {
                return new ReadOnlyObjectWrapper<>("");
            }
            return new ReadOnlyObjectWrapper<>(param.getValue().getAircraftData().description().string());
        });
        descriptionColumn.setPrefWidth(70);

        // TODO: constants, ternary, throws

        /* ### NUMBER COLUMNS ### */
        final int NUM_COLUMN_WIDTH = 85;
        // LONGITUDE COLUMN
        TableColumn<ObservableAircraftState, String> longitudeColumn = new TableColumn<>("Longitude (°)");
        longitudeColumn.setCellValueFactory(param -> param.getValue().positionProperty().map(e -> {
            formatter.setMinimumFractionDigits(4);
            formatter.setMaximumFractionDigits(4);
            double lon = Units.convertTo(param.getValue().positionProperty().get().longitude(), Units.Angle.DEGREE);
            return formatter.format(lon);
        }));
        longitudeColumn.setComparator(numberComparator());
        longitudeColumn.setPrefWidth(NUM_COLUMN_WIDTH);

        // LATITUDE COLUMN
        TableColumn<ObservableAircraftState, String> latitudeColumn = new TableColumn<>("Latitude (°)");
        latitudeColumn.setCellValueFactory(param -> param.getValue().positionProperty().map(e -> {
            formatter.setMinimumFractionDigits(4);
            formatter.setMaximumFractionDigits(4);
            double lat = Units.convertTo(param.getValue().positionProperty().get().latitude(), Units.Angle.DEGREE);
            return formatter.format(lat);
        }));
        latitudeColumn.setComparator(numberComparator());
        latitudeColumn.setPrefWidth(NUM_COLUMN_WIDTH);

        // ALTITUDE COLUMN
        TableColumn<ObservableAircraftState, String> altitudeColumn = new TableColumn<>("Altitude (m)");
        altitudeColumn.setCellValueFactory(param -> param.getValue().altitudeProperty().map(e -> {
            formatter.setMinimumFractionDigits(0);
            formatter.setMaximumFractionDigits(0);
            double alt = param.getValue().getAltitude();
            if (Double.isNaN(alt)) return "?";
            return formatter.format(alt);
        }));
        altitudeColumn.setComparator(numberComparator());
        altitudeColumn.setPrefWidth(NUM_COLUMN_WIDTH);

        // SPEED COLUMN
        TableColumn<ObservableAircraftState, String> speedColumn = new TableColumn<>("Vitesse (km/h)");
        speedColumn.setCellValueFactory(param -> param.getValue().velocityProperty().map(e -> {
            formatter.setMinimumFractionDigits(0);
            formatter.setMaximumFractionDigits(0);
            if (Double.isNaN(param.getValue().getVelocity())) return "?";
            double speed = Units.convertTo(param.getValue().getVelocity(), Units.Speed.KILOMETER_PER_HOUR);
            return formatter.format(speed);
        }));
        speedColumn.setComparator(numberComparator());
        speedColumn.setPrefWidth(NUM_COLUMN_WIDTH);

        longitudeColumn.getStyleClass().add("numeric");
        latitudeColumn.getStyleClass().add("numeric");
        altitudeColumn.getStyleClass().add("numeric");
        speedColumn.getStyleClass().add("numeric");

        table.getColumns().addAll(
                icaoAddressColumn,
                callSignColumn,
                registrationColumn,
                modelColumn,
                typeColumn,
                descriptionColumn,
                longitudeColumn,
                latitudeColumn,
                altitudeColumn,
                speedColumn
        );
    }

    /**
     * The comparator for numbers columns.
     * @return the comparator
     */
    private static Comparator<String> numberComparator() {
        return (a, b) -> {
            if (a.isEmpty() || b.isEmpty()) {
                return a.compareTo(b);
            }
            try {
                double aNumber = formatter.parse(a).doubleValue();
                double bNumber = formatter.parse(b).doubleValue();
                return Double.compare(aNumber, bNumber);
            } catch (ParseException e) {
                throw new Error(e);
            }
        };
    }

    /**
     * Returns the JavaFX pane of the table.
     * @return the pane
     */
    public TableView<ObservableAircraftState> pane() {
        return table;
    }
}
