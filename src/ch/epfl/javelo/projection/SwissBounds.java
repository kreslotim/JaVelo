package ch.epfl.javelo.projection;
/**
 * Switzerland bounds
 *
 * @author Tim Kreslo (310686)
 * @author Wei-En Hsieh (341271)
 */
public final class SwissBounds {
    /**
     * Default (not instantiable) SwissBounds constructor
     */
    private SwissBounds() {}

    /* Switzerland's bounds defined with swiss parameters E (east) & N (north) coordinates */
    public static final double MIN_E = 2485000;
    public static final double MAX_E = 2834000;
    public static final double MIN_N = 1075000;
    public static final double MAX_N = 1296000;
    public static final double WIDTH = MAX_E - MIN_E;
    public static final double HEIGHT = MAX_N - MIN_N;

    /**
     * Checks if E (east) and N (north) coordinates are inside Switzerland's bounds
     *
     * @param e East coordinate
     * @param n North coordinate
     * @return (boolean) true if East and North coordinates are in bounds of Switzerland
     */
    public static boolean containsEN(double e, double n) {
        return (e >= MIN_E) && (e <= MAX_E) && (n >= MIN_N) && (n <= MAX_N);
    }

}
