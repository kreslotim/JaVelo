package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointWebMercator;
import javafx.geometry.Point2D;

/**
 * MapViewParameters, a record
 * There exists four points (mapTopLeftPositionX, mapTopLeftPositionY) in a map.
 *
 * @author Tim Kreslo (310686)
 * @author Wei-En Hsieh (341271)
 */
public record MapViewParameters(int zoomLevel, double mapTopLeftPositionX, double mapTopLeftPositionY) {

    /**
     * Returns the coordinates (x, y) of the top-left corner as a Point2D type object.
     *
     * @return the coordinates (x, y) of the top-left corner as a Point2D type object.
     */
    public Point2D topLeft() {
        return new Point2D(mapTopLeftPositionX, mapTopLeftPositionY);
    }

    /**
     * Returns an instance of MapViewParameters, whose coordinates aren't those of the top-left corner, identical to
     * the receiver. The coordinates of an instance of MapViewParameters are passed as arguments to the method.
     *
     * @param minX the coordinate x of the instance of MapViewParameters
     * @param minY the coordinate y of the instance of MapViewParameters
     * @return an instance of MapViewParameters identical to the receiver.
     */
    public MapViewParameters withMinXY(double minX, double minY) {
        return new MapViewParameters(zoomLevel, minX, minY);
    }

    /*********************************************************************************************
     The purpose of the pointAt and viewX / viewY methods, which are inverses of each other,
     is to convert between the coordinate system of the OpenStreetMap map's image and
     the coordinate system of the portion displayed on the screen.
     *********************************************************************************************/

    /**
     * Takes the x and y coordinates of a point as arguments, expressed relative to the top-left corner of the
     * map portion displayed on the screen, and returns this point as an instance of PointWebMercator.
     *
     * @param x the coordinate x of a point
     * @param y the coordinate y of a point
     * @return an object of PointWebMercator where the object has relative coordinates to the coordinates
     * of the top-left corner of the map portion.
     */
    public PointWebMercator pointAt(double x, double y) {
        return new PointWebMercator(mapTopLeftPositionX + x, mapTopLeftPositionY + y);
    }

    /**
     * Returns the corresponding x position, expressed relative to the top-left corner of the map portion displayed
     * on the screen.
     *
     * @param pointWebMercator an object of PointWebMercator
     * @return the corresponding x position relative to the top-left corner of the map portion.
     */
    public double viewX(PointWebMercator pointWebMercator) {
        return pointWebMercator.xAtZoomLevel(zoomLevel) - mapTopLeftPositionX; //todo : - mapTopLeftPositionX ?
    }

    /**
     * Returns the corresponding y position, expressed relative to the top-left corner of the map portion displayed
     * on the screen.
     *
     * @param pointWebMercator an object of PointWebMercator
     * @return the corresponding y position relative to the top-left corner of the map portion.
     */
    public double viewY(PointWebMercator pointWebMercator) {
        return pointWebMercator.yAtZoomLevel(zoomLevel) - mapTopLeftPositionY; //todo : - mapTopLeftPositionY ?
    }
}
