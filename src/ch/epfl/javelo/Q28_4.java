package ch.epfl.javelo;

/* convert numbers between Q28.4 representation and other representations */
public final class Q28_4 {

    private Q28_4() {
    } // classe non instanciable

    /**
     * @param i the given integer (not a misuse of language)
     * @return the Q28.4 value corresponding to the given integer
     */


    public static int ofInt(int i) {
        //a value x of type int has 32 bits, but double needs more places.
        // so if we want to pass from int to double, we have to shift left the int x.
        // so we use the operator << to shift left

        // i must be bigger than :-134217728 and smaller than :134217727
        Preconditions.checkArgument(i > 0b11111000000000000000000000000000 && i <= 0b00000111111111111111111111111111);
        if (i < 0) return (i << 4) | 0b10000000000000000000000000000000;
        return i << 4;
    }

// changer le poids en multipliant dans asDouble asfloat

    /**
     * § 2.4.2
     *
     * @param q28_4 an integer (a misuse of language)
     * @return the double type value equal to the given Q28.4 value
     */
    public static double asDouble(int q28_4) {
        //pass from int to double.
        // as the two's complement interpretation can only represent integer values,
        // so we have to modify the "scale" of the weight of a bit, i.e. multiply by $2^{-4}$
        return Math.scalb((double) q28_4, -4); // = change the weight of the original vector (here := q28_4), i.e. q28_4 multiplying by $2^{-4}$
    }
    // Test : avec un vecteur du bit 1 au poids de plus fort, doit retourner une valeur reel strictement negative.


    /**
     * § 2.4.2
     *
     * @param q28_4 an integer (a misuse of language)
     * @return the float type value corresponding to the given Q28.4 value
     */
    public static float asFloat(int q28_4) {
        // footnote 2 sur Math.scalb(x, y) me permet de ctrl+c et ctrl+v la ligne suivante
        return (float) Math.scalb((double) q28_4, -4);
    }


}
