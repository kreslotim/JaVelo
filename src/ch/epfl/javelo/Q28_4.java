package ch.epfl.javelo;

/**
 * Converter between bits in Q28.4 & basic representation
 *
 * @author Tim Kreslo (310686)
 * @author Wei-En Hsieh (341271)
 */
public final class Q28_4 {
    /**
     * Default (not instantiable) Q28_4 constructor
     */
    private Q28_4() {}

    /**
     * Gives bit sequence in Q28.4 representation, corresponding to the given Integer
     *
     * @param i given Integer
     * @return bit sequence in Q28.4 representation, corresponding to the given Integer
     */
    public static int ofInt(int i) {
        // i must be bigger than or equal to :-134217728 and smaller than or equal to :134217727 // must check !
        Preconditions.checkArgument( i >= 0b11111000000000000000000000000000 && i <= 0b00000111111111111111111111111111);

        if (i < 0) return (i << 4) | 0b10000000000000000000000000000000;
        return i << 4;
    }


    /**
     * Gives a (double) type value, equal to given Q28.4 value
     *
     * @param q28_4 Integer given in Q28.4 representation
     * @return (double) type value equal to given Q28.4 value
     */
    public static double asDouble(int q28_4) {
        return Math.scalb((double) q28_4, -4);
    }

    /**
     * Gives a (float) type value, equal to given Q28.4 value
     *
     * @param q28_4 Integer given in Q28.4 representation
     * @return (float) type value equal to given Q28.4 value
     */
    public static float asFloat(int q28_4) {
        return (float) Math.scalb((double) q28_4, -4);
    }

}