package ch.epfl.javelo.projection;
import ch.epfl.javelo.Preconditions;

/**
 * Representation of a point in Web Mercator system
 *
 * @author Tim Kreslo (310686)
 * @author Wei-En Hsieh (341271)
 */
public record PointWebMercator(double x, double y) {

    /**
     * Compact PointWebMercator constructor
     *
     * @param x Horizontal component X of a point (must be between 0 & 1)
     * @param y Vertical component Y of a point (must be between 0 & 1)
     */
    public PointWebMercator { // compact constructor
        Preconditions.checkArgument(x >= 0 && x <= 1 && y >= 0 && y <= 1);
    }

    /**
     * Returns a Web Mercator point (between 0 & 1), using X and Y at a certain Zoom level.
     *
     * @param zoomLevel : Level zoom taking values from 0 to 19 ~ 20 (or negative)
     * @param x Horizontal component x zoomed at a certain level
     * @param y Vertical component y zoomed at a certain level
     * @return Web Mercator point (between 0 & 1)
     */
    public static PointWebMercator of(int zoomLevel, double x, double y) {

        double X =  Math.scalb(x, -zoomLevel - 8);
        double Y =  Math.scalb(y, -zoomLevel - 8);
        return new PointWebMercator(X, Y);
    }

    /**
     * Returns a Web Mercator point (between 0 & 1) corresponding to a point in Swiss earth's parameters (East & North)
     *
     * @param pointCh point in Swiss earth's parameters (East & North)
     * @return Web Mercator point (between 0 & 1)
     */
    public static PointWebMercator ofPointCh(PointCh pointCh) {
        return new PointWebMercator(WebMercator.x(pointCh.lon()), WebMercator.y(pointCh.lat()));
    }


    /**
     * Returns X coordinate zoomed at a certain level
     *
     * @param zoomLevel Level of zoom
     * @return X coordinate zoomed at zoomLevel
     */
    public double xAtZoomLevel(int zoomLevel) {
        return Math.scalb(x, 8 + zoomLevel);
    }


    /**
     * Returns Y coordinate zoomed at a certain level
     *
     * @param zoomLevel Level of zoom
     * @return Y coordinate zoomed at zoomLevel
     */
    public double yAtZoomLevel(int zoomLevel) {
        return Math.scalb(y, 8 + zoomLevel);
    }


    /**
     * Returns longitude (in Radians) of this point
     *
     * @return longitude (in Radians) of this point
     */
    public double lon() {
        return WebMercator.lon(x);
    }


    /**
     * Returns latitude (in Radians) of this point
     *
     * @return latitude (in Radians) of this point
     */
    public double lat() { // mm idee
        return WebMercator.lat(y);
    }


    /**
     * Returns a point in Swiss earth's parameters (East & North), positioned at the same location that this point.
     *
     * @return a point in Swiss earth's parameters (East & North), positioned at the same location that this point.
     */
    public PointCh toPointCh() {
        return (SwissBounds.containsEN(Ch1903.e(lon(), lat()), Ch1903.n(lon(), lat())) ?
                new PointCh(Ch1903.e(lon(), lat()), Ch1903.n(lon(), lat())) : null);
    }


}
