package ch.epfl.javelo;
import java.util.function.DoubleUnaryOperator;

/**
 * Collection of mathematical functions of real numbers
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
     * Constant function
     *
     * @param y constant value
     * @return constant value y
     */
    public static DoubleUnaryOperator constant(double y) {
        return new Constant(y);
    }

    /**
     * Function obtained by linear interpolation between samples, separated by constant intervals, from 0 to xMax
     * @throws IllegalArgumentException (samples.length < 2 || xMax <= 0)
     * @param samples
     * @param xMax
     * @return
     */
    public static DoubleUnaryOperator sampled(float[] samples, double xMax) {
        Preconditions.checkArgument(samples.length >= 2 && xMax > 0);
        return new Sampled(samples, xMax);
    }

    /**
     * Recorded class that returns a Constant object
     */
    private static record Constant(double c) implements DoubleUnaryOperator {

        /**
         * Constant function
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
     * Recorded class that returns a Sampled object
     */
    private static record Sampled(float[] samples, double xMax) implements DoubleUnaryOperator {

        /**
         * Function returning y(x) obtained by linear interpolation between a list of samples
         *
         * @param x precise value on X-axis, for which we compute y(x) by linear interpolation between samples
         * @return y(x) precise value on Y-axis, obtained by linear interpolation between samples
         */
        @Override
        public double applyAsDouble(double x) {
            if (x <= 0) {
                return samples[0];
            } else if (x >= xMax) {
                return samples[samples.length-1];
            } else {
                double interval = xMax / (samples.length-1);
                int i = (int) Math.floor(x / interval);
                return Math2.interpolate(samples[i], samples[i+1],(x-interval*i)/interval);
            }
        }
    }
}