package ch.epfl.javelo.projection;

/**
 * The class SwissBounds contains constants and methods related to the bounds of Switzerland.
 *
 * @author Tim Kreslo (310686)
 * @author Wei-En Hsieh (341271)
 */
public final class SwissBounds {
    /* Switzerland's bounds defined with Swiss parameters E (East) & N (North) coordinates. */
    public static final double MIN_E = 2485000;
    public static final double MAX_E = 2834000;
    public static final double MIN_N = 1075000;
    public static final double MAX_N = 1296000;
    public static final double WIDTH = MAX_E - MIN_E;
    public static final double HEIGHT = MAX_N - MIN_N;

    /**
     * Default (not instantiable) SwissBounds constructor
     */
    private SwissBounds() {
    }


    /**
     * Checks if a point (E, N) is in Switzerland's bounds,
     * returns true iff (if and only if) the given E (East) and N (North) coordinates are within Switzerland's bounds.
     *
     * @param e the East coordinate
     * @param n the North coordinate
     * @return (boolean) true iff the East and North coordinates are in the bounds of Switzerland.
     */
    public static boolean containsEN(double e, double n) {
        return (e >= MIN_E) && (e <= MAX_E) && (n >= MIN_N) && (n <= MAX_N);
    }
}
