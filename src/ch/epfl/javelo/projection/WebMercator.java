package ch.epfl.javelo.projection;
import ch.epfl.javelo.Math2;

/**
 * Converter of parameters x & y, and longitude & latitude, between WGS 84 and Web Mercator coordinates
 *
 * @author Tim Kreslo (310686)
 * @author Wei-En Hsieh (341271)
 */
public final class WebMercator {

    /**
     * Converts longitude (given in radians) to horizontal component x, of a point
     *
     * @param lon longitude (given in radians)
     * @return coordinate X (between 0 & 1)
     */
    public static double x(double lon) {
        return (lon + Math.PI) / (2 * Math.PI);
    }

    /**
     * Converts latitude (given in radians) to vertical component y, of a point
     *
     * @param lat (latitude) (given in Radians)
     * @return coordinate Y (between 0 & 1)
     */
    public static double y(double lat) {
        return (Math.PI - Math2.asinh(Math.tan(lat))) / (2 * Math.PI);
    }

    /**
     * Converts the horizontal component X of a Web Mercator point, to longitude
     *
     * @param x projection X of a Web Mercator point
     * @return longitude (given in radians)
     */
    public static double lon(double x) {
        return ((2 * Math.PI * x) - Math.PI);
    }

    /**
     * Converts the vertical component Y of a Web Mercator point, to latitude
     *
     * @param y projection Y of a Web Mercator point
     * @return latitude (given in radians)
     */
    public static double lat(double y) {
        return (Math.atan(Math.sinh(Math.PI - 2 * Math.PI * y)));
    }
}
