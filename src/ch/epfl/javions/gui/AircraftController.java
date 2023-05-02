package ch.epfl.javions.gui;

import ch.epfl.javions.Units;
import ch.epfl.javions.WebMercator;
import ch.epfl.javions.aircraft.AircraftDescription;
import ch.epfl.javions.aircraft.AircraftTypeDesignator;
import ch.epfl.javions.aircraft.IcaoAddress;
import ch.epfl.javions.aircraft.WakeTurbulenceCategory;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class AircraftController {
    private final MapParameters mapParameters;
    private final ObjectProperty<ObservableAircraftState> state;
    private final Pane pane;
    private final Map<IcaoAddress, Group> aircraftGroups;

    public AircraftController(MapParameters mapParameters, ObservableSet<ObservableAircraftState> states, ObjectProperty<ObservableAircraftState> state) {
        Objects.requireNonNull(mapParameters);
        Objects.requireNonNull(states);

        this.mapParameters = mapParameters;
        this.state = state;

        this.pane = new Pane();
        pane.setPickOnBounds(false);
        pane.getStylesheets().add("aircraft.css");

        this.aircraftGroups = new HashMap<>();

        states.addListener((SetChangeListener<ObservableAircraftState>) change -> {
            if (change.wasAdded()) {
                createAircraftGroup(change.getElementAdded());
            } else if (change.wasRemoved()) {
                IcaoAddress icaoAddress = change.getElementRemoved().getIcaoAddress();
                pane.getChildren().remove(aircraftGroups.get(icaoAddress));
                aircraftGroups.remove(icaoAddress);
            }
        });
    }

    // TODO: extract in several sub-methods
    private void createAircraftGroup(ObservableAircraftState s) {
        Group container = new Group();

        container.setId(s.getIcaoAddress().string());
        aircraftGroups.put(s.getIcaoAddress(), container);

        container.viewOrderProperty().bind(s.altitudeProperty().negate());

        Group trajectory = new Group();
        trajectory.getStyleClass().add("trajectory");
        trajectory.visibleProperty().bind(
                Bindings.createBooleanBinding(() -> {
                    if (state.get() == null) {
                        return false;
                    }
                    return state.get().getIcaoAddress().equals(s.getIcaoAddress());
                }, state)
        );


        Group info = new Group();

        Group label = new Group();
        label.getStyleClass().add("label");
        label.visibleProperty().bind(
                Bindings.createBooleanBinding(() -> mapParameters.getZoom() >= 11, mapParameters.zoomProperty())
        );

        Rectangle rect = new Rectangle();
        Text txt = new Text();

        rect.widthProperty().bind(
                txt.layoutBoundsProperty().map(b -> b.getWidth() + 4));
        rect.heightProperty().bind(
                txt.layoutBoundsProperty().map(b -> b.getHeight() + 4));
        txt.textProperty().bind(
                Bindings.createStringBinding(() -> {
                    String identifier = s.getAircraftData() != null ?
                              s.getAircraftData().registration().string() :
                              s.getCallSign() != null ? s.getCallSign().string() :
                              s.getIcaoAddress().string();

                    String speed = String.valueOf(
                            (int)s.getVelocity() * 3600 / 1000
                    );
                    String altitude = String.valueOf((int)s.getAltitude());
                    return identifier + "\n" + speed + "km/h\u2002" + altitude + "m";
                }, s.altitudeProperty(), s.velocityProperty(), s.callSignProperty())
        );

        label.getChildren().addAll(rect, txt);

        SVGPath path = new SVGPath();
        path.getStyleClass().add("aircraft");
        path.fillProperty().bind(
                Bindings.createObjectBinding(() -> {
                    double t = Math.pow(s.getAltitude() / 12000, 1d/3d);
                    return ColorRamp.PLASMA.at(t);
                }, s.altitudeProperty())
        );
        path.contentProperty().bind(
                Bindings.createStringBinding(() -> {
                    if (s.getAircraftData() == null)
                        return AircraftIcon.iconFor(new AircraftTypeDesignator(""), new AircraftDescription(""), 0, WakeTurbulenceCategory.UNKNOWN).svgPath();
                    return AircraftIcon.iconFor(s.getAircraftData().typeDesignator(), s.getAircraftData().description(), s.getCategory(), s.getAircraftData().wakeTurbulenceCategory()).svgPath();
                })
        );
        path.rotateProperty().bind(
                Bindings.createDoubleBinding(() -> Units.convertTo(s.getTrackOrHeading(), Units.Angle.DEGREE),
                        s.trackOrHeadingProperty())
        );
        path.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            state.set(s);
            e.consume();
        });

        info.getChildren().addAll(label, path);

        container.getChildren().addAll(trajectory, info);

        container.layoutXProperty().bind(
                Bindings.createDoubleBinding(() -> WebMercator.x(mapParameters.getZoom(), s.getPosition().longitude()) - mapParameters.getxMin(),
                        mapParameters.zoomProperty(), s.positionProperty(), mapParameters.xMinProperty())
        );

        container.layoutYProperty().bind(
                Bindings.createDoubleBinding(() -> WebMercator.y(mapParameters.getZoom(), s.getPosition().latitude()) - mapParameters.getyMin(),
                        mapParameters.zoomProperty(), s.positionProperty(), mapParameters.yMinProperty())
        );

        pane.getChildren().add(container);
    }

    public Pane pane() {
        return pane;
    }
}
