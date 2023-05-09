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
 */
public final class AircraftTableController {
    private final TableView<ObservableAircraftState> table;
    private Consumer<ObservableAircraftState> consumer;

    /**
     * Creates a new aircraft table controller.
     * @param states the set of aircraft states
     * @param state the selected aircraft state
     */
    public AircraftTableController(ObservableSet<ObservableAircraftState> states, ObjectProperty<ObservableAircraftState> state) {
        Objects.requireNonNull(states);


        this.table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_SUBSEQUENT_COLUMNS);
        table.setTableMenuButtonVisible(true);
        table.getStylesheets().add("table.css");

        createColumns();

        states.addListener((SetChangeListener<ObservableAircraftState>) change -> {
            if (change.wasAdded()) {
                table.getItems().add(change.getElementAdded());
                table.sort();
            } else if (change.wasRemoved()) {
                table.getItems().remove(change.getElementRemoved());
            }
        });

        state.addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                table.getSelectionModel().select(newValue);
                if (!newValue.equals(oldValue)) {
                    table.scrollTo(newValue);
                }
                table.requestFocus();
            }
        });
        table.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                setOnDoubleClick(state::set);
            }
        });

        table.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && event.getButton() == MouseButton.PRIMARY) {
                ObservableAircraftState e = table.getSelectionModel().getSelectedItem();
                if (consumer != null && e != null) {
                    consumer.accept(e);
                }
            }
        });
    }

    public void setOnDoubleClick(Consumer<ObservableAircraftState> consumer) {
        this.consumer = consumer;
    }

    /**
     * Creates the columns of the table.
     */
    private void createColumns() {
        TableColumn<ObservableAircraftState, String> icaoAddressColumn = new TableColumn<>("OACI");
        icaoAddressColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().getIcaoAddress().string()));
        icaoAddressColumn.setPrefWidth(60);

        TableColumn<ObservableAircraftState, String> callSignColumn = new TableColumn<>("Indicatif");
        callSignColumn.setCellValueFactory(param -> param.getValue().callSignProperty().map(CallSign::string));
        callSignColumn.setPrefWidth(70);

        TableColumn<ObservableAircraftState, String> registrationColumn = new TableColumn<>("Immatriculation");
        registrationColumn.setCellValueFactory(param -> {
            if (param.getValue().getAircraftData() == null) {
                return new ReadOnlyObjectWrapper<>("");
            }
            return new ReadOnlyObjectWrapper<>(param.getValue().getAircraftData().registration().string());
        });
        registrationColumn.setPrefWidth(90);

        TableColumn<ObservableAircraftState, String> modelColumn = new TableColumn<>("Modèle");
        modelColumn.setCellValueFactory(param -> {
            if (param.getValue().getAircraftData() == null) {
                return new ReadOnlyObjectWrapper<>("");
            }
            return new ReadOnlyObjectWrapper<>(param.getValue().getAircraftData().model());
        });
        modelColumn.setPrefWidth(230);

        TableColumn<ObservableAircraftState, String> typeColumn = new TableColumn<>("Type");
        typeColumn.setCellValueFactory(param -> {
            if (param.getValue().getAircraftData() == null) {
                return new ReadOnlyObjectWrapper<>("");
            }
            return new ReadOnlyObjectWrapper<>(param.getValue().getAircraftData().typeDesignator().string());
        });
        typeColumn.setPrefWidth(50);

        TableColumn<ObservableAircraftState, String> descriptionColumn = new TableColumn<>("Description");
        descriptionColumn.setCellValueFactory(param -> {
            if (param.getValue().getAircraftData() == null) {
                return new ReadOnlyObjectWrapper<>("");
            }
            return new ReadOnlyObjectWrapper<>(param.getValue().getAircraftData().description().string());
        });
        descriptionColumn.setPrefWidth(70);

        TableColumn<ObservableAircraftState, String> longitudeColumn = new TableColumn<>("Longitude (°)");
        longitudeColumn.setCellValueFactory(param -> param.getValue().positionProperty().map(e -> {
            NumberFormat formatter = NumberFormat.getInstance(new Locale("fr", "CH"));
            formatter.setMinimumFractionDigits(4);
            formatter.setMaximumFractionDigits(4);
            double lon = Units.convertTo(param.getValue().positionProperty().get().longitude(), Units.Angle.DEGREE);
            return formatter.format(lon);
        }));
        longitudeColumn.setComparator(numberComparator());
        longitudeColumn.setPrefWidth(85);

        TableColumn<ObservableAircraftState, String> latitudeColumn = new TableColumn<>("Latitude (°)");
        latitudeColumn.setCellValueFactory(param -> param.getValue().positionProperty().map(e -> {
            NumberFormat formatter = NumberFormat.getInstance(new Locale("fr", "CH"));
            formatter.setMinimumFractionDigits(4);
            formatter.setMaximumFractionDigits(4);
            double lat = Units.convertTo(param.getValue().positionProperty().get().latitude(), Units.Angle.DEGREE);
            return formatter.format(lat);
        }));
        latitudeColumn.setComparator(numberComparator());
        latitudeColumn.setPrefWidth(85);

        TableColumn<ObservableAircraftState, String> altitudeColumn = new TableColumn<>("Altitude (m)");
        altitudeColumn.setCellValueFactory(param -> param.getValue().altitudeProperty().map(e -> {
            NumberFormat formatter = NumberFormat.getInstance(new Locale("fr", "CH"));
            formatter.setMinimumFractionDigits(0);
            formatter.setMaximumFractionDigits(0);
            double alt = param.getValue().getAltitude();
            if (Double.isNaN(alt)) {
                return "?";
            }
            return formatter.format(alt);
        }));
        altitudeColumn.setComparator(numberComparator());
        altitudeColumn.setPrefWidth(85);

        TableColumn<ObservableAircraftState, String> speedColumn = new TableColumn<>("Vitesse (km/h)");
        speedColumn.setCellValueFactory(param -> param.getValue().velocityProperty().map(e -> {
            NumberFormat formatter = NumberFormat.getInstance(new Locale("fr", "CH"));
            formatter.setMinimumFractionDigits(0);
            formatter.setMaximumFractionDigits(0);
            if (Double.isNaN(param.getValue().getVelocity())) {
                return "?";
            }
            double speed = Units.convertTo(param.getValue().getVelocity(), Units.Speed.KILOMETER_PER_HOUR);
            return formatter.format(speed);
        }));
        speedColumn.setComparator(numberComparator());
        speedColumn.setPrefWidth(85);

        longitudeColumn.getStyleClass().add("numeric");
        latitudeColumn.getStyleClass().add("numeric");
        altitudeColumn.getStyleClass().add("numeric");
        speedColumn.getStyleClass().add("numeric");

        table.getColumns().addAll(icaoAddressColumn,
                callSignColumn,
                registrationColumn,
                modelColumn,
                typeColumn,
                descriptionColumn,
                longitudeColumn,
                latitudeColumn,
                altitudeColumn,
                speedColumn);
    }

    private static Comparator<String> numberComparator() {
        return (a, b) -> {
            if (a.isEmpty() || b.isEmpty()) {
                return a.compareTo(b);
            }
            NumberFormat formatter = NumberFormat.getInstance();
            try {
                Number aNumber = formatter.parse(a);
                Number bNumber = formatter.parse(b);
                return Double.compare(aNumber.doubleValue(), bNumber.doubleValue());
            } catch (ParseException e) {
                throw new Error(e);
            }
        };
    }

    public TableView<ObservableAircraftState> pane() {
        return table;
    }
}
