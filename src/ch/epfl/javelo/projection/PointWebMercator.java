package ch.epfl.javelo.projection;

import ch.epfl.javelo.Preconditions;

/**
 * The record PointWebMercator represents a point in the Web Mercator system.
 *
 * @author Tim Kreslo (310686)
 * @author Wei-En Hsieh (341271)
 */
public record PointWebMercator(double x, double y) {
    private static final int PIXELS_SIDE = 8;

    /**
     * Compact PointWebMercator constructor,
     * throwing an exception if one of the given components x and y are not valid (not between 0 and 1 (inclusive)).
     *
     * @param x Horizontal component x of a point (must be between 0 and 1 (inclusive))
     * @param y Vertical component y of a point (must be between 0 and 1 (inclusive))
     * @throws IllegalArgumentException if one of the given components x and y are not between 0 and 1 (inclusive).
     */
    public PointWebMercator {
        Preconditions.checkArgument(x >= 0 && x <= 1 && y >= 0 && y <= 1);
    }


    /**
     * Returns a Web Mercator point (between 0 and 1 (inclusive)), via the coordinates x and y at a certain Zoom level.
     *
     * @param zoomLevel : Level zoom taking values from 0 to 19, even 20, (or negative)
     * @param x         Horizontal component x zoomed at a certain level
     * @param y         Vertical component y zoomed at a certain level
     * @return Web Mercator point (between  0 and 1 (inclusive))
     */
    public static PointWebMercator of(int zoomLevel, double x, double y) {
        double finalX = Math.scalb(x, -zoomLevel - PIXELS_SIDE);
        double finalY = Math.scalb(y, -zoomLevel - PIXELS_SIDE);
        return new PointWebMercator(finalX, finalY);
    }

    /**
     * Returns a Web Mercator point (between 0 and 1 (inclusive)) of a given point in Swiss coordinates (E, N).
     *
     * @param pointCh a given point in Swiss coordinates (E, N)
     * @return Web Mercator point (between 0 and 1 (inclusive)).
     */
    public static PointWebMercator ofPointCh(PointCh pointCh) {
        return new PointWebMercator(WebMercator.x(pointCh.lon()), WebMercator.y(pointCh.lat()));
    }

    /**
     * Returns the coordinate x zoomed at a certain given level.
     *
     * @param zoomLevel Level of zoom
     * @return the coordinate x zoomed at zoomLevel.
     */
    public double xAtZoomLevel(int zoomLevel) {
        return Math.scalb(x, PIXELS_SIDE + zoomLevel);
    }

    /**
     * Returns the coordinate y zoomed at a certain given level.
     *
     * @param zoomLevel Level of zoom
     * @return the coordinate y zoomed at zoomLevel.
     */
    public double yAtZoomLevel(int zoomLevel) {
        return Math.scalb(y, PIXELS_SIDE + zoomLevel);
    }

    /**
     * Returns the longitude (in radians) of a given point's horizontal component.
     *
     * @return the longitude of a point.
     */
    public double lon() {
        return WebMercator.lon(x);
    }

    /**
     * Returns the latitude (in radians) of a given point's vertical component.
     *
     * @return latitude of a point.
     */
    public double lat() { // mm idee
        return WebMercator.lat(y);
    }

    /**
     * Returns the point (PointCh) with Swiss coordinates (E, N) located at the same position as
     * the receiver point (this), or null if the point is not in Switzerland's bounds defined by SwissBounds.
     *
     * @return a point located at the same position as the receiver point (this).
     */
    public PointCh toPointCh() {

        double chE = Ch1903.e(lon(), lat());
        double chN = Ch1903.n(lon(), lat());
        return (SwissBounds.containsEN(chE, chN) ? new PointCh(chE, chN) : null);
    }
}
