package ch.epfl.javelo.projection;

import ch.epfl.javelo.Preconditions;

// c'est pour un point(x,y) du Map entier aka du WebMercator
public record PointWebMercator(double x, double y) {
    // x et y pour le map "normal"
//    private double x; // la coordonnée x du point du WebMercator
//    private double y; // la coordonnée y du point du WebMercator

    public PointWebMercator { // compact constructor
        // utilise les preconditions
        Preconditions.checkArgument(x < 0 || x > 1 || y < 0 || y > 1);
    }

    //public static methods

    /**
     * §2.3
     * retourne le point dont les coordonnées sont x et y au niveau de zoom zoomLeve
     * càd retourner le pointZoomé à un zoomLevel donné au point sans zoomLevel
     *
     * @param zoomLevel : can be from 0 to 20
     * @param x
     * @param y
     * @return
     */

    public static PointWebMercator of(int zoomLevel, double x, double y) {
        // énoncé : la position d'un point = il suffit de multiplier ses coordonnées(lon, lat)
        // par la taille de l'image à ce niveau de zoom
        double lon = WebMercator.lon(x) * Math.scalb(x, -zoomLevel - 8);
        double lat = WebMercator.lat(y) * Math.scalb(y, -zoomLevel - 8);
        return new PointWebMercator(lon, lat); // euh retourner deux valeurs ??
    }

    /**
     * §2.3.1
     * retourne le point Web Mercator correspondant au point du système de coordonnées suisse(E, N) donné
     *
     * @param pointCh
     * @return
     */
    public static PointWebMercator ofPointCh(PointCh pointCh) { // dans le code de Tim
        return new PointWebMercator(pointCh.e(), pointCh.n());
    }


    //public methods

    // une multiplication par une puissance de deux entière, qui dépend du niveau de zoom

    /**
     * retourne la coordonnée x au niveau de zoom donné
     *
     * @param zoomLevel
     * @return
     */
    public double xAtZoomLevel(int zoomLevel) {
        return Math.scalb(x, 8 + zoomLevel); // = x * Math.pow(2, 8 + zoomLevel);
    }


    /**
     * retourne la coordonnée y au niveau de zoom donné
     *
     * @param zoomLevel
     * @return
     */
    public double yAtZoomLevel(int zoomLevel) {
        return Math.scalb(y, 8 + zoomLevel); // = y * Math.pow(2, 8 + zoomLevel);
    }


    /**
     * retourne la longitude du point, en radians
     *
     * @return
     */
    public double lon() { // c'est la methode lon de WebMercator mais sauf que cette methode fonctionne comme un getter
        // car y'a deja l'attribut x
        return WebMercator.lon(x);
    }


    /**
     * retourne la latitude du point, en radians
     *
     * @return
     */
    public double lat() { // mm idee
        return WebMercator.lat(y);
    }


    /**
     * retourne le point de coordonnées suisses se trouvant à la même position que le récepteur ( aka this) ou null
     * si ce point n'est pas dans les limites de la Suisse définies par SwissBounds
     *
     * @return
     */
    public PointCh toPointCh() {
        // e = Ch1903.e(lon(), lat())
        // n = Ch1903.n(lon(), lat())
        return (SwissBounds.containsEN(Ch1903.e(lon(), lat()), Ch1903.n(lon(), lat())) ? new PointCh(Ch1903.e(lon(), lat()), Ch1903.n(lon(), lat())) : null);
    }


}
