package ch.epfl.javions.gui;

import ch.epfl.javions.Math2;
import javafx.beans.property.*;

public final class MapParameters {
    private final IntegerProperty zoom = new SimpleIntegerProperty();
    private final DoubleProperty xMin = new SimpleDoubleProperty();
    private final DoubleProperty yMin = new SimpleDoubleProperty();

    public MapParameters(int zoom, double xMin, double yMin) {
        if (zoom < 6 || zoom > 19) {
            throw new IllegalArgumentException("Zoom level must be between 6 and 19");
        }
        this.zoom.set(zoom);
        this.xMin.set(xMin);
        this.yMin.set(yMin);
    }

    public ReadOnlyIntegerProperty zoomProperty() {
        return zoom;
    }
    public int getZoom() {
        return zoom.get();
    }

    public ReadOnlyDoubleProperty xMinProperty() {
        return xMin;
    }
    public double getxMin() {
        return xMin.get();
    }

    public ReadOnlyDoubleProperty yMinProperty() {
        return yMin;
    }
    public double getyMin() {
        return yMin.get();
    }

    public void scroll(double x, double y) {
        this.xMin.set(getxMin() + x);
        this.yMin.set(getyMin() + y);
    }
    public void changeZoomLevel(int zoom) {
        int previousZoom = getZoom();
        this.zoom.set(Math2.clamp(6, getZoom() + zoom, 19));
        if (previousZoom == getZoom()) return;

        this.xMin.set(Math.scalb(getxMin(), zoom));
        this.yMin.set(Math.scalb(getyMin(), zoom));
    }
}
