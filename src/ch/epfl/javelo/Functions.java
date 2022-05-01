package ch.epfl.javelo;

import java.util.function.DoubleUnaryOperator;

/**
 * The class Functions contains methods for creating objects representing mathematical functions from reals to reals.
 * The functions of this class are all represented by values of type DoubleUnaryOperator.
 *
 * @author Tim Kreslo (310686)
 * @author Wei-En Hsieh (341271)
 */
public final class Functions {
    /**
     * Default (not instantiable) Functions constructor
     */
    private Functions() {}


    /**
     * Returns a constant function, whose value is always y.
     *
     * @param y a constant value
     * @return the constant value y
     */
    public static DoubleUnaryOperator constant(double y) {
        return new Constant(y);
    }

    /**
     * Returns a function obtained by linear interpolation between the samples,
     * separated by evenly spaced intervals,and through 0 to xMax (inclusive),
     * throwing an exception if the array of elevations has less than 2 elements
     * or the maximal interval between samples is negative.
     *
     * @param samples the array (collection) of elevations
     * @param xMax    the maximal interval between samples
     * @return sampled function of elevations
     * @throws IllegalArgumentException if the array of elevations has less than 2 elements
     *                                  or the maximal interval between samples has no length (xMax is negative).
     */
    public static DoubleUnaryOperator sampled(float[] samples, double xMax) {
        Preconditions.checkArgument(samples.length >= 2 && xMax > 0);
        return new Sampled(samples, xMax);
    }

    /**
     * Constant, a record that returns a constant object.
     */
    private static record Constant(double c) implements DoubleUnaryOperator {
        /**
         * Returns a constant function.
         *
         * @param y constant value
         * @return the operator result (constant value)
         */
        @Override
        public double applyAsDouble(double y) {
            return c;
        }
    }

    /**
     * Sampled, a record that returns a sampled object.
     */
    private static record Sampled(float[] samples, double xMax) implements DoubleUnaryOperator {
        /**
         * A function returning y(x) obtained by linear interpolation between a list of samples.
         *
         * @param x precise value on X-axis, for which we compute y(x) by linear interpolation between samples
         * @return y(x) precise value on Y-axis, obtained by linear interpolation between samples.
         */
        @Override
        public double applyAsDouble(double x) {
            if (x <= 0) {
                return samples[0];
            } else if (x >= xMax) {
                return samples[samples.length - 1];
            } else {
                double interval = xMax / (samples.length - 1);
                int i = (int) (x / interval);
                return Math2.interpolate(samples[i], samples[i + 1], (x - interval * i) / interval);
            }
        }
    }
}