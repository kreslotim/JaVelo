package ch.epfl.javelo.projection;

import ch.epfl.javelo.Math2;

/* classe utilitaire : convertir entre les coordonnées WGS 84 et les coordonnées Web Mercator */
public final class WebMercator {

    private WebMercator(){}
    /**
     * retourne la coordonnée x de la projection d'un point se trouvant à la longitude lon, donnée en radians
     *
     * @param lon
     * @return
     */
    public static double x(double lon) {
        return (lon + Math.PI) / (2 * Math.PI);
    }

    /**
     * retourne la coordonnée y de la projection d'un point se trouvant à la latitude lat, donnée en radians
     *
     * @param lat
     * @return
     */
    public static double y(double lat) {
        return (Math.PI - Math2.asinh(Math.tan(lat))) / (2 * Math.PI); //la fonction arsinh ??
    }

    /**
     * retourne la longitude, en radians, d'un point dont la projection se trouve à la coordonnée x donnée
     *
     * @param x
     * @return
     */
    public static double lon(double x) {
        return (2 * Math.PI * x) - Math.PI;
    }

    /**
     * retourne la latitude, en radians, d'un point dont la projection se trouve à la coordonnée y donnée
     *
     * @param y
     * @return
     */
    public static double lat(double y) {
        return Math.atan(Math.sinh(Math.PI - 2 * Math.PI * y));
    }
}
