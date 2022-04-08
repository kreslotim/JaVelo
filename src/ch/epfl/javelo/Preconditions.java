package ch.epfl.javelo;
/**
 * Argument value Check
 *
 * @author Tim Kreslo (310686)
 * @author Wei-En Hsieh (341271)
 */
public final class Preconditions {
    /**
     * Default (non instantiable) Preconditions constructor
     */
    private Preconditions() {}

    /**
     * Checks validity of an input
     *
     * @param shouldBeTrue argument must be true
     * @throws IllegalArgumentException if the given argument is false
     */
    public static void checkArgument(boolean shouldBeTrue) {
        if (!shouldBeTrue) throw new IllegalArgumentException();
    }
}
