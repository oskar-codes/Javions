package ch.epfl.javions.gui;

import javafx.beans.property.*;

/**
 * Represents the parameters of a map, i.e. the zoom level and the xMin and yMin coordinates
 * of the map's top-left corner
 * @author Oskar Zanota (361595)
 * @author Eddy Rashed (360667)
 */
public final class MapParameters {
    /**
     * The minimum zoom level.
     */
    public final static int MIN_ZOOM = 6;
    /**
     * The maximum zoom level.
     */
    public final static int MAX_ZOOM = 19;

    private final IntegerProperty zoom = new SimpleIntegerProperty();
    private final DoubleProperty xMin = new SimpleDoubleProperty();
    private final DoubleProperty yMin = new SimpleDoubleProperty();


    /**
     * Constructs a new MapParameters object with the given zoom level and the given xMin and yMin coordinates
     * @param zoom - the zoom level
     * @param xMin - the xMin coordinate
     * @param yMin - the yMin coordinate
     * @throws IllegalArgumentException if the zoom level is not between 6 and 19
     */
    public MapParameters(int zoom, double xMin, double yMin) {
        if (!(MIN_ZOOM <= zoom && zoom <= MAX_ZOOM)) {
            throw new IllegalArgumentException("Zoom level must be between " + MIN_ZOOM + " and " + MAX_ZOOM);
        }
        this.zoom.set(zoom);
        this.xMin.set(xMin);
        this.yMin.set(yMin);
    }

    /**
     * Getter for the zoom level property
     * @return the zoom level property
     */
    public ReadOnlyIntegerProperty zoomProperty() {
        return zoom;
    }

    /**
     * Getter for the zoom level
     * @return the zoom level
     */
    public int getZoom() {
        return zoom.get();
    }

    /**
     * Getter for the xMin property
     * @return the xMin property
     */
    public ReadOnlyDoubleProperty xMinProperty() {
        return xMin;
    }

    /**
     * Getter for the xMin value
     * @return the xMin value
     */
    public double getxMin() {
        return xMin.get();
    }

    /**
     * Getter for the yMin property
     * @return the yMin property
     */
    public ReadOnlyDoubleProperty yMinProperty() {
        return yMin;
    }

    /**
     * Getter for the yMin value
     * @return the yMin value
     */
    public double getyMin() {
        return yMin.get();
    }

    /**
     * Scrolls the map by the given delta (x,y) vector
     * @param x - the x delta
     * @param y - the y delta
     */
    public void scroll(double x, double y) {
        this.xMin.set(getxMin() + x);
        this.yMin.set(getyMin() + y);
    }

    /**
     * Changes the zoom level of the map. The zoom level is clamped between the minimum and maximum zoom levels.
     * @param zoom - the zoom delta
     */
    public void changeZoomLevel(int zoom) {
        int newZoom = getZoom() + zoom;
        if (newZoom < MIN_ZOOM || newZoom > MAX_ZOOM) return;

        this.zoom.set(newZoom);
        this.xMin.set(Math.scalb(getxMin(), zoom));
        this.yMin.set(Math.scalb(getyMin(), zoom));
    }
}
