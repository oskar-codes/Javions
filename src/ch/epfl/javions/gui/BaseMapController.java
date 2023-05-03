package ch.epfl.javions.gui;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.WebMercator;
import javafx.application.Platform;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

import static javafx.scene.Cursor.CLOSED_HAND;
import static javafx.scene.Cursor.DEFAULT;

/**
 * Controller for the base map.
 *
 * @author Eddy Rashed (360667)
 * @author Oskar Zanota (361595)
 */
public final class BaseMapController {
    private final TileManager tileManager;
    private final MapParameters mapParameters;
    private final Pane pane;
    private final Canvas canvas;
    private boolean redrawNeeded = true;

    /**
     * Creates a new BaseMapController.
     * @param tileManager the tile manager
     * @param mapParameters the map parameters
     */
    public BaseMapController(TileManager tileManager, MapParameters mapParameters) {
        this.tileManager = tileManager;
        this.mapParameters = mapParameters;

        // Setup the pane and canvas
        this.pane = new Pane();
        this.canvas = new Canvas();
        this.pane.getChildren().add(canvas);
        this.canvas.widthProperty().bind(pane.widthProperty());
        this.canvas.heightProperty().bind(pane.heightProperty());

        // If the pane is resized, redraw the map
        this.pane.widthProperty().addListener((p, o, n) -> redrawOnNextPulse());
        this.pane.heightProperty().addListener((p, o, n) -> redrawOnNextPulse());

        // If the map parameters change, redraw the map
        canvas.sceneProperty().addListener((p, oldS, newS) -> {
            assert oldS == null;
            newS.addPreLayoutPulseListener(this::redrawIfNeeded);
        });

        // Enable scrolling and zooming
        LongProperty minScrollTime = new SimpleLongProperty();
        pane.setOnScroll(e -> {
            int zoomDelta = (int) Math.signum(e.getDeltaY());
            if (zoomDelta == 0) return;

            long currentTime = System.currentTimeMillis();
            if (currentTime < minScrollTime.get()) return;
            minScrollTime.set(currentTime + 200);

            mapParameters.scroll((int) e.getX(), (int) e.getY());
            mapParameters.changeZoomLevel(zoomDelta);
            mapParameters.scroll((int) -e.getX(), (int) -e.getY());
        });

        ObjectProperty<Point2D> lastMousePos = new SimpleObjectProperty<>();
        // Move the pane on mouse drag
        pane.setOnMousePressed(e -> {
            pane.requestFocus();
            pane.setCursor(CLOSED_HAND);
            lastMousePos.set(new Point2D(e.getX(), e.getY()));
        });
        pane.setOnMouseReleased(e -> pane.setCursor(DEFAULT));
        pane.setOnMouseDragged(e -> {
            mapParameters.scroll((int) (lastMousePos.get().getX() - e.getX()), (int) (lastMousePos.get().getY() - e.getY()));
            lastMousePos.set(new Point2D(e.getX(), e.getY()));
        });

        // Redraw the map when the map parameters change
        mapParameters.zoomProperty().addListener((p, o, n) -> redrawOnNextPulse());
        mapParameters.xMinProperty().addListener((p, o, n) -> redrawOnNextPulse());
        mapParameters.yMinProperty().addListener((p, o, n) -> redrawOnNextPulse());
    }

    public Pane pane() {
        return pane;
    }

    /**
     * Centers the map on the given {@code GeoPos} position.
     * @param pos the position to center on
     */
    public void centerOn(GeoPos pos) {
        double x = WebMercator.x(mapParameters.getZoom(), pos.longitude());
        double y = WebMercator.y(mapParameters.getZoom(), pos.latitude());

        double deltaX = x - mapParameters.getxMin() - canvas.getWidth() / 2;
        double deltaY = y - mapParameters.getyMin() - canvas.getHeight() / 2;

        mapParameters.scroll(deltaX, deltaY);
    }

    /**
     * Requests a redraw on the next pulse. This throttles the number of redraws to one per pulse (60 times per second).
     */
    private void redrawOnNextPulse() {
        redrawNeeded = true;
        Platform.requestNextPulse();
    }

    /**
     * Redraws the map if needed.
     */
    private void redrawIfNeeded() {
        if (!redrawNeeded) return;
        redrawNeeded = false;

        int xMin = (int) mapParameters.getxMin();
        int yMin = (int) mapParameters.getyMin();

        // Gets the tile coordinates of the top left corner of the canvas
        int startX = xMin >>> 8;
        int startY = yMin >>> 8;
        GraphicsContext ctx = this.canvas.getGraphicsContext2D();

        for (int x = -1; x < canvas.getWidth() / 256 + 1; x++) {
            for (int y = -1; y < canvas.getHeight() / 256 + 1; y++) {

                // Gets the image from the tilemanager
                Image image = tileManager.imageForTileAt(new TileManager.TileId(
                        mapParameters.getZoom(),
                        startX + x,
                        startY + y
                ));

                // Draws the image on the canvas
                ctx.drawImage(
                        image, x * 256 - (xMin % 256),
                               y * 256 - (yMin % 256));
            }
        }
    }
}
