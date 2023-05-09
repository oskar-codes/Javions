package ch.epfl.javions.gui;

import ch.epfl.javions.Units;
import ch.epfl.javions.WebMercator;
import ch.epfl.javions.aircraft.AircraftDescription;
import ch.epfl.javions.aircraft.AircraftTypeDesignator;
import ch.epfl.javions.aircraft.IcaoAddress;
import ch.epfl.javions.aircraft.WakeTurbulenceCategory;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static javafx.scene.paint.CycleMethod.NO_CYCLE;

/**
 * Controller for the aircraft.
 *
 * @author Oskar Zanota (361595)
 * @author Eddy Rashed (360667)
 */
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

    private void createAircraftGroup(ObservableAircraftState s) {
        Group container = new Group();
        aircraftGroups.put(s.getIcaoAddress(), container);

        container.setId(s.getIcaoAddress().string());
        container.viewOrderProperty().bind(s.altitudeProperty().negate());

        Group descriptionBox = createDescriptionBox(s);
        Group trajectory = createTrajectory(s);
        container.getChildren().addAll(trajectory, descriptionBox);

        pane.getChildren().add(container);
    }

    private Group createDescriptionBox(ObservableAircraftState s) {
        Group info = new Group();
        Group label = createLabel(s);
        SVGPath path = createIconSVG(s);
        info.getChildren().addAll(label, path);

        info.layoutXProperty().bind(
                Bindings.createDoubleBinding(() -> WebMercator.x(mapParameters.getZoom(), s.getPosition().longitude()) - mapParameters.getxMin(),
                        mapParameters.zoomProperty(), s.positionProperty(), mapParameters.xMinProperty())
        );

        info.layoutYProperty().bind(
                Bindings.createDoubleBinding(() -> WebMercator.y(mapParameters.getZoom(), s.getPosition().latitude()) - mapParameters.getyMin(),
                        mapParameters.zoomProperty(), s.positionProperty(), mapParameters.yMinProperty())
        );
        return info;
    }

    private SVGPath createIconSVG(ObservableAircraftState s) {
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
        return path;
    }

    private Group createLabel(ObservableAircraftState s) {
        Group label = new Group();
        label.getStyleClass().add("label");
        label.visibleProperty().bind(
                Bindings.createBooleanBinding(() ->
                        mapParameters.getZoom() >= 11 ||
                        state.get() != null &&
                        state.get().getIcaoAddress().equals(s.getIcaoAddress()),
                mapParameters.zoomProperty(), state)
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

                    String speed;
                    if (Double.isNaN(s.getVelocity())) {
                        speed = "?";
                    } else {
                        speed = String.valueOf(
                                (int) s.getVelocity() * 3600 / 1000
                        );
                    }
                    String altitude;
                    if (Double.isNaN(s.getAltitude())) {
                        altitude = "?";
                    } else {
                        altitude = String.valueOf((int) s.getAltitude());
                    }

                    return identifier + "\n" + speed + "km/h\u2002" + altitude + "m";
                }, s.altitudeProperty(), s.velocityProperty(), s.callSignProperty())
        );

        label.getChildren().addAll(rect, txt);
        return label;
    }

    private Group createTrajectory(ObservableAircraftState s) {
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

        s.trajectoryProperty().addListener(((ListChangeListener<ObservableAircraftState.AirbornePos>) change -> updateTrajectory(s, trajectory)));
        mapParameters.zoomProperty().addListener((p, o, n) -> updateTrajectory(s, trajectory));

        trajectory.layoutXProperty().bind(mapParameters.xMinProperty().negate());
        trajectory.layoutYProperty().bind(mapParameters.yMinProperty().negate());
        return trajectory;
    }

    private void updateTrajectory(ObservableAircraftState s, Group trajectory) {
        trajectory.getChildren().clear();
        List<ObservableAircraftState.AirbornePos> trajectoryPoints = s.getTrajectory();
        for (int i = 0; i < trajectoryPoints.size() - 1; i++) {
            ObservableAircraftState.AirbornePos p1 = trajectoryPoints.get(i);
            ObservableAircraftState.AirbornePos p2 = trajectoryPoints.get(i + 1);

            double x1 = WebMercator.x(mapParameters.getZoom(), p1.position().longitude());
            double y1 = WebMercator.y(mapParameters.getZoom(), p1.position().latitude());
            double x2 = WebMercator.x(mapParameters.getZoom(), p2.position().longitude());
            double y2 = WebMercator.y(mapParameters.getZoom(), p2.position().latitude());

            Line path = new Line(
                    x1, y1,
                    x2, y2
            );

            if (p1.altitude() == p2.altitude()) {
                path.setStroke(ColorRamp.PLASMA.at(Math.pow(p1.altitude() / 12000, 1d/3d)));
            } else {
                Color c1 = ColorRamp.PLASMA.at(Math.pow(p1.altitude() / 12000, 1d/3d));
                Color c2 = ColorRamp.PLASMA.at(Math.pow(p2.altitude() / 12000, 1d/3d));
                Stop s1 = new Stop(0, c1);
                Stop s2 = new Stop(1, c2);
                path.setStroke(new LinearGradient(0, 0, 1, 0, true, NO_CYCLE, s1, s2));
            }

            trajectory.getChildren().add(path);
        }
    }

    public Pane pane() {
        return pane;
    }
}
