package ch.epfl.javelo;

/**
 * The class Q28_4 contains methods for converting numbers between the Q28.4 representation and other representations.
 *
 * @author Tim Kreslo (310686)
 * @author Wei-En Hsieh (341271)
 */
public final class Q28_4 {
    /**
     * Default (not instantiable) Q28_4 constructor
     */
    private Q28_4() {
    }


    /**
     * Returns a bit sequence in Q28.4 representation of the given integer.
     *
     * @param i a given integer
     * @return the bit sequence in Q28.4 representation.
     */
    public static int ofInt(int i) {
        // i must be greater than or equal to -134217728 and smaller than or equal to 134217727,
        // otherwise, we lose information.
        return i << 4;
    }

    /**
     * Returns a (double) type value equal to the given Q28.4 value.
     *
     * @param q28_4 an integer given in Q28.4 representation
     * @return a (double) type value equal to the given Q28.4 value.
     */
    public static double asDouble(int q28_4) {
        return Math.scalb((double) q28_4, -4);
    }

    /**
     * Returns a (float) type value equal to the given Q28.4 value.
     *
     * @param q28_4 an integer given in Q28.4 representation
     * @return a (float) type value equal to the given Q28.4 value.
     */
    public static float asFloat(int q28_4) {
        return (float) Math.scalb((double) q28_4, -4);
    }
}