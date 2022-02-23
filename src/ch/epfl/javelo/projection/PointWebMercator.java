package ch.epfl.javelo.projection;

public class PointWebMercator {
    private double x; // la coordonnée x du point
    private double y; // la coordonnée y du point

    public PointWebMercator() { // compact constructor
        if (x < 0 || x > 1 || y < 0 || y > 1) {
            throw new IllegalArgumentException();
        }

    }

    //public static methods

    /**
     * §2.3
     * retourne le point dont les coordonnées sont x et y au niveau de zoom zoomLeve
     *
     * @param zoomLevel : can be from 0 to 20
     * @param x
     * @param y
     * @return
     */
    public static PointWebMercator of(int zoomLevel, double x, double y) {
        // énoncé : la position d'un point = il suffit de multiplier ses coordonnées(lon, lat)
        // par la taille de l'image à ce niveau de zoom
        double imageSize = (256 * Math.pow(2, zoomLevel)) * (256 * Math.pow(2, zoomLevel)); // the imageSize is a squre at the zoomLevel

        double lon = WebMercator.lon(x) * imageSize;
        double lat = WebMercator.lat(y) * imageSize;
        return (lon,lat); // euh retourner deux valeurs ??
    }

    /**
     * §2.3.1
     * retourne le point Web Mercator correspondant au point du système de coordonnées suisse(E, N) donné
     *
     * @param pointCh
     * @return
     */
    public static PointWebMercator ofPointCh(PointCh pointCh) { // dans le code de Tim


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
    public double lon() {


    }


    /**
     * retourne la latitude du point, en radians
     *
     * @return
     */
    public double lat() {


    }


    /**
     * retourne le point de coordonnées suisses se trouvant à la même position que le récepteur (this) ou null
     * si ce point n'est pas dans les limites de la Suisse définies par SwissBounds
     *
     * @return
     */
    public PointCh toPointCh() {

    }


}
