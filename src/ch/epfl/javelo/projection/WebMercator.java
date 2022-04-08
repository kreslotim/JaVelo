package ch.epfl.javelo.projection;

import ch.epfl.javelo.Math2;

/**
 * The class WebMercator offers methods to convert between WGS 84 coordinates and Web Mercator coordinates.
 * Converter of parameters x and y, and longitude and latitude, between WGS 84 coordinates and Web Mercator coordinates.
 *
 * @author Tim Kreslo (310686)
 * @author Wei-En Hsieh (341271)
 */
public final class WebMercator {

    /**
     * Returns the horizontal coordinate x of the projection of a point located at the longitude.
     *
     * @param lon the longitude (given in radians)
     * @return the coordinate x (between 0 and 1).
     */
    public static double x(double lon) {
        return (lon + Math.PI) / (2 * Math.PI);
    }

    /**
     * Returns the vertical coordinate y of the projection of a point located at the latitude.
     *
     * @param lat the latitude (given in Radians)
     * @return the coordinate y (between 0 and 1).
     */
    public static double y(double lat) {
        return (Math.PI - Math2.asinh(Math.tan(lat))) / (2 * Math.PI);
    }

    /**
     * Converts the horizontal component x of a Web Mercator point to longitude (in radians).
     *
     * @param x the projection x of a Web Mercator point
     * @return the longitude (in radians).
     */
    public static double lon(double x) {
        return ((2 * Math.PI * x) - Math.PI);
    }

    /**
     * Converts the vertical component y of a Web Mercator point to latitude (in radians).
     *
     * @param y the projection y of a Web Mercator point
     * @return the latitude (in radians).
     */
    public static double lat(double y) {
        return (Math.atan(Math.sinh(Math.PI - 2 * Math.PI * y)));
    }
}
