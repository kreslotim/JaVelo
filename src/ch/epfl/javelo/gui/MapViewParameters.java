package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointWebMercator;
import javafx.geometry.Point2D;

/**
 * Basemap settings presented in the GUI
 *
 * @author Tim Kreslo (310686)
 * @author Wei-En Hsieh (341271)
 */
public record MapViewParameters(int zoomLevel, double mapTopLeftPositionX, double mapTopLeftPositionY) {

    /**
     * Returns the coordinates of the top-left corner (Point2D)
     *
     * @return top-left corner coords
     */
    public Point2D topLeft() {
        return new Point2D(mapTopLeftPositionX, mapTopLeftPositionY);
    }

    /**
     * Returns this (MapViewParameters) with new coordinates of top-left corner
     *
     * @param newUpLeftX new top-left X coordinate
     * @param newUpLeftY new top-left Y coordinate
     * @return this, with new top-left coordinates
     */
    public MapViewParameters withMinXY(double newUpLeftX, double newUpLeftY) {
        return new MapViewParameters(zoomLevel, newUpLeftX, newUpLeftY);
    }

    /*********************************************************************************************
     The purpose of the pointAt and viewX / viewY methods, which are inverses of each other,
     is to convert between the coordinate system of the OpenStreetMap map's image and
     the coordinate system of the portion displayed on the screen.
     *********************************************************************************************/

    /**
     * Returns a point (PointWebMercator) with coordinates,
     * expressed in relation to the top-left corner, of the map portion displayed on the screen.
     *
     * @param x coordinate X of the point
     * @param y coordinate Y of the point
     * @return point on map with coordinates X and Y.
     */
    public PointWebMercator pointAt(double x, double y) {
        return new PointWebMercator(mapTopLeftPositionX + x, mapTopLeftPositionY + y);
    }

    /**
     * Returns the corresponding X position of the given point (PointWebMercator),
     * expressed in relation to the top-left corner of the map portion,
     * displayed on the screen.
     *
     * @param pointWebMercator a point on the map
     * @return position X of the given point
     */
    public double viewX(PointWebMercator pointWebMercator) {
        return pointWebMercator.xAtZoomLevel(zoomLevel) - mapTopLeftPositionX; //todo : - mapTopLeftPositionX ?
    }

    /**
     * Returns the corresponding Y position of the given point (PointWebMercator),
     * expressed in relation to the top-left corner of the map portion,
     * displayed on the screen.
     *
     * @param pointWebMercator a point on the map
     * @return position Y of the given point
     */
    public double viewY(PointWebMercator pointWebMercator) {
        return pointWebMercator.yAtZoomLevel(zoomLevel) - mapTopLeftPositionY; //todo : - mapTopLeftPositionY ?
    }
}
