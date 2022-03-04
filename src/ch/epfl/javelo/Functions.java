package ch.epfl.javelo;
/* contains methods for creating objects representing mathematical functions from reals to reals,
i.e. of type $\mathbb{R}\rightarrow\mathbb{R}$ */

import java.util.function.DoubleUnaryOperator;

/* Functions is a nested class */
public final class Functions {

    // the constructor of Functions
    private Functions() {
    } // classe non instanciable

    /**
     * §2.5
     *
     * @param y
     * @return a constant function, whose value is always y
     */
    public static DoubleUnaryOperator constant(double y) {
        return new Constant(y);
    }

    /**
     * cf fin §3.5
     * le premier d'entre eux donne la valeur de la fonction en 0, le dernier donne sa valeur à xMax
     * any remaining samples are evenly distributed between these two extremes
     * the function returned by sampled is also defined outside the range from 0 to xMax
     * applyToDouble de Sampled doit faire tout ca ?
     *
     * @param samples
     * @param xMax
     * @return
     */
    public static DoubleUnaryOperator sampled(float[] samples, double xMax) {
        Preconditions.checkArgument(samples.length >= 2 && xMax > 0); //contains more than two elements AND if xMax is more than 0
        return new Sampled(samples, xMax);
    }

    /* a class Constant in the class Functions */

    private static record Constant(double y) implements DoubleUnaryOperator {

        /**
         * @param y
         * @return the operator result
         */
        @Override
        public double applyAsDouble(double y) {
            return this.y;
        }
    }

    /* do the same thing and this time for Sampled */

    /* a class Sampled in the class Functions */
    // record Sampled §3.6 step1
    private static record Sampled(float[] samples, double xMax) implements DoubleUnaryOperator {

        /**
         * fin §3.5 : plqge = intervalle     argument = double x
         * le premier d'entre eux donne la valeur de la fonction en 0, le dernier donne sa valeur à xMax
         * any remaining samples are evenly distributed between these two extremes
         * the function returned by sampled is also defined outside the range from 0 to xMax
         *
         * @param x
         * @return a function obtained by linear interpolation between samples, regularly spaced and covering the range
         * from 0 to xMax;
         * Throws IllegalArgumentException if the samples array contains less than two elements, or if xMax is less than or equal to 0.
         */
        @Override
        public double applyAsDouble(double x) { // "x" = "argument" de l'enonce
            if (x < 0) {
                return samples[0];
            } else if (x > xMax) {
                return samples[samples.length - 1];
            } else {
                // do a linear interpolation between the two closest points of your input :
                double interval = xMax / (samples.length - 1); // divide by nbOfSamples-1 to bring back to [0, 1]
                int i = (int) Math.floor(x / interval); // la division entiere
                return Math2.interpolate(samples[i], samples[i + 1], (x - interval * i) / interval); // we divide by interval again because the interval of x in interpolate(...) is in [0, 1]
            }
        }
        // Alban : je vais pourvoir ecrire Sampled.applyAsDouble(truc) plus tard
    }


}
