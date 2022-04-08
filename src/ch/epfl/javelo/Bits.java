package ch.epfl.javelo;

/**
 * The class Bits contains two methods for extracting a sequence of bits of a 32-bit vector.
 *
 * @author Tim Kreslo (310686)
 * @author Wei-En Hsieh (341271)
 */
public final class Bits {
    /**
     * Default (not instantiable) Bits constructor
     */
    private Bits() {
    }


    /**
     * Extracts from the 32-bit vector value the range of length bits starting at the index bit,
     * which it interprets as a two's complement signed value,
     * throwing an exception if the range to extract is not between 0 and 31(inclusive).
     *
     * @param value  32-bit (or less) sequence
     * @param start  index of start (must be between 0 and 31(inclusive))
     * @param length interval of extraction (must be positive and not exceeding last bit)
     * @return extracted signed sequence of length bits
     * @throws IllegalArgumentException if the interval of extraction is not between 0 and 31(inclusive).
     */
    public static int extractSigned(int value, int start, int length) {
        Preconditions.checkArgument(start >= 0 && start <= 31
                && (start + length) >= 0 && (start + length) <= 32 && length > 0);
        if (length == 32) return value;

        int i = value << 32 - (start + length);
        return i >> 32 - length;
    }

    /**
     * Extracts from the 32-bit vector value the range of length bits starting at the index bit,
     * which it interprets as a one's complement unsigned value,
     * throwing an exception if the range to extract is not between 0 and 31(inclusive)
     * or if the interval of extraction (length) is equal to 32.
     *
     * @param value  32-bit (or less) sequence
     * @param start  index of start (must be between 0 and 31(inclusive))
     * @param length interval of extraction (must be positive and not exceeding last bit)
     * @return extracted unsigned sequence of length bits
     * @throws IllegalArgumentException if the interval of extraction is not between 0 and 31(inclusive)
     *                                  or if the interval of extraction (length) is equal to 32.
     */
    public static int extractUnsigned(int value, int start, int length) {
        Preconditions.checkArgument(start >= 0 && start <= 31
                && (start + length) >= 0 && (start + length) <= 32 && length > 0 && length < 32);
        int i = value << 32 - (start + length);
        return i >>> 32 - length;
    }
}