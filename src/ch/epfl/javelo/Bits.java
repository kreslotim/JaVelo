package ch.epfl.javelo;

/**
 * Extractor of bit sequences
 *
 * @author Tim Kreslo (310686)
 * @author Wei-En Hsieh (341271)
 */
public final class Bits {
    /**
     * Default (not instantiable) Bits constructor
     */
    private Bits() {}

    /**
     * Extracts bit sequence from 32-bit vector, interpreted in two's complement representation (with sign)
     *
     * @param value 32-bit (or less) sequence
     * @param start index of start (must be between 0 & 31)
     * @param length interval of extraction (must be positive and not exceeding last bit)
     * @return extracted signed sequence of length bits
     * @throws IllegalArgumentException if the interval to extract is not valid
     */
    public static int extractSigned(int value, int start, int length) {
        Preconditions.checkArgument(start >=0 && start <= 31 && (start+length) >=0 && (start+length) <= 32 && length>0);
        if (length == 32) return value;

        int i = value << 32 - (start + length);
        return i >> 32 - length;
    }


    /**
     * Extracts bit sequence from 32-bit vector, interpreted in one's complement representation (without sign)
     *
     * @param value 32-bit (or less) sequence
     * @param start index of start (must be between 0 & 31)
     * @param length interval of extraction (must be positive and not exceeding last bit)
     * @return extracted unsigned sequence of length bits
     * @throws IllegalArgumentException if the interval to extract is not valid
     */
    public static int extractUnsigned(int value, int start, int length) {
        Preconditions.checkArgument(start >=0 && start <= 31 && (start+length) >=0 && (start+length) <= 32 && length>0 && length<32);
        int i = value << 32 - (start + length);
        return i >>> 32 - length;
    }
}
