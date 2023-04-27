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
    public void setZoom(int zoom) {
        this.zoom.set(zoom);
    }

    public ReadOnlyDoubleProperty xMinProperty() {
        return xMin;
    }
    public void setxMin(double xMin) {
        this.xMin.set(xMin);
        clampCoordinates();
    }

    public ReadOnlyDoubleProperty yMinProperty() {
        return yMin;
    }
    public void setyMin(double yMin) {
        this.yMin.set(yMin);
        clampCoordinates();
    }

    public void scroll(int x, int y) {
        this.xMin.set((int) (this.xMin.get() + x));
        this.yMin.set((int) (this.yMin.get() + y));
        clampCoordinates();
    }
    public void changeZoomLevel(int zoom) {
        int previousZoom = this.zoom.get();
        this.setZoom(Math2.clamp(6, this.zoom.get() + zoom, 19));
        if (previousZoom == this.zoom.get()) return;

        this.xMin.set(this.xMin.get() * Math.pow(2, zoom));
        this.yMin.set(this.yMin.get() * Math.pow(2, zoom));

        clampCoordinates();
    }

    private void clampCoordinates() {
        this.xMin.set(Math2.clamp(0, (int) (this.xMin.get()), 1 << (8 + this.zoom.get())));
        this.yMin.set(Math2.clamp(0, (int) (this.yMin.get()), 1 << (8 + this.zoom.get())));
    }
}
