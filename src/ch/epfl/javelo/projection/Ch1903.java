package ch.epfl.javelo.projection;
/**
 * Converter of Switzerland's earth parameters (longitude & latitude) to WGS 84 coordinates
 *
 * @author Tim Kreslo (310686)
 * @author Wei-En Hsieh (341271)
 */
public final class Ch1903 {
    /**
     * Default private Ch1903 constructor
     */
    private Ch1903() {}

    /**
     * Converts an earth point's longitude & latitude using WGS 84 system
     *
     * @param lon longitude
     * @param lat latitude
     * @return (double) East coordinate
     */
    public static double e(double lon, double lat) {
        double lambda1 = 0.0001 * (3600 * Math.toDegrees(lon) - 26782.5);
        double phi1 = 0.0001 * (3600 * Math.toDegrees(lat) - 169028.66);  // Find a way to avoid attributes's duplicate

        return 2600072.37
                + 211455.93 * lambda1
                - 10938.51 * lambda1 * phi1
                - 0.36 * lambda1 * phi1*phi1
                - 44.54 * lambda1*lambda1*lambda1;

    }

    /**
     * Converts an earth point's longitude & latitude using WGS 84 system
     *
     * @param lon longitude
     * @param lat latitude
     * @return (double) North coordinate
     */
    public static double n(double lon, double lat) {
        double lambda1 = 0.0001 * (3600 * Math.toDegrees(lon) - 26782.5);
        double phi1 = 0.0001 * (3600 * Math.toDegrees(lat) - 169028.66);

        return 1200147.07
                + 308807.95 * phi1
                + 3745.25 * lambda1*lambda1
                + 76.63 * phi1*phi1
                - 194.56 * lambda1*lambda1 * phi1
                + 119.79 * phi1*phi1*phi1;
    }

    /**
     * Converts a point's swiss parameters to WGS 84 system
     *
     * @param e East coordinate
     * @param n North coordinate
     * @return (double) longitude
     */
    public static double lon(double e, double n) {
        double x = 1e-6 * (e-2600000);
        double y = 1e-6 * (n-1200000);

        double lambda0 = 2.6779094
                + 4.728982 * x
                + 0.791484 * x * y
                + 0.1306 * x * y*y
                - 0.0436 * x*x*x;

        return Math.toRadians(lambda0 * 100/36.);
    }

    /**
     * Converts a point's swiss parameters to WGS 84 system
     *
     * @param e East coordinate
     * @param n North coordinate
     * @return (double) latitude
     */
    public static double lat(double e, double n) {
        double x = 1e-6 * (e-2600000);
        double y = 1e-6 * (n-1200000);

        double phi0 = 16.9023892
                + 3.238272 * y
                - 0.270978 * x*x
                - 0.002528 * y*y
                - 0.0447 * x*x * y
                - 0.0140 * y*y*y;

        return Math.toRadians(phi0 * 100/36.);
    }
}


