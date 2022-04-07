package ch.epfl.javelo;

/**
 * Math upgrade
 *
 * @author Tim Kreslo (310686)
 * @author Wei-En Hsieh (341271)
 */
public final class Math2 {
    /**
     * Default (non instantiable) Math2 constructor
     */
    private Math2() {}


    /**
     * Division round to ceiling
     *
     * @param x numerator
     * @param y denominator
     * @return (int) ceiling division of x by y
     * @throws IllegalArgumentException if x or y negative, or y is null
     */
    public static int ceilDiv(int x, int y) {
        Preconditions.checkArgument(!(x < 0 || y <= 0));
        return (x + y - 1) / y;
    }

    /**
     * Linear interpolation between two points
     *
     * @param y0 Vertical component of point 0
     * @param y1 Vertical component of point 1
     * @param x  Horizontal component of middle point
     * @return y (double) coordinate positioned on the line (y1 - y0)*x + y0
     */
    public static double interpolate(double y0, double y1, double x) {
        return Math.fma((y1 - y0), x, y0); // Work with any value of x
        // returns y * x + y0
    }

    /**
     * clamp of value v
     *
     * @param min minimal value
     * @param v   value
     * @param max maximal value
     * @return v (int) limited to bounds
     * @throws IllegalArgumentException if maximal value is smaller than minimal value
     */
    public static int clamp(int min, int v, int max) {
        Preconditions.checkArgument(max > min);
        if (v < min) return min;
        return Math.min(v, max);
    }

    /**
     * Clamp of given value v
     *
     * @param min minimal value
     * @param v   value
     * @param max maximal value
     * @return v (double) limited to bounds
     * @throws IllegalArgumentException if maximal value is smaller than minimal value
     */
    public static double clamp(double min, double v, double max) {
        Preconditions.checkArgument(max > min);
        if (v < min) return min;
        return Math.min(v, max);
    }

    /**
     * Hyperbolic arcsine function
     *
     * @param x variable
     * @return (double) hyperbolic arcsine of x
     */
    public static double asinh(double x) {
        return Math.log(x + Math.sqrt(1 + x * x));
    }

    /**
     * Scalar product of two vectors
     *
     * @param uX X component of vector U
     * @param uY Y component of vector U
     * @param vX X component of vector V
     * @param vY Y component of vector V
     * @return (double) dot product of U and V
     */
    public static double dotProduct(double uX, double uY, double vX, double vY) {
        return uX * vX + uY * vY;
    }

    /**
     * Squared norm of a vector U
     *
     * @param uX X component of vector U
     * @param uY Y component of vector U
     * @return (double) squared norm of vector U
     */
    public static double squaredNorm(double uX, double uY) {
        return uX * uX + uY * uY;
    }

    /**
     * Norm of vector U
     *
     * @param uX X component of vector U
     * @param uY Y component of vector U
     * @return (double) norm of vector U
     */
    public static double norm(double uX, double uY) {
        return Math.sqrt(squaredNorm(uX, uY));
    }

    /**
     * Length of projection of vector from A to P on vector from A to B
     *
     * @param aX X component of point A
     * @param aY Y component of point A
     * @param bX X component of point B
     * @param bY Y component of point B
     * @param pX X component of point P
     * @param pY Y component of point P
     * @return (double) length of projection of vector from A to P on vector from A to B
     */
    public static double projectionLength(double aX, double aY, double bX, double bY, double pX, double pY) {
        double uX = (pX - aX);
        double uY = (pY - aY);
        double vX = (bX - aX);
        double vY = (bY - aY);

        return (uX * vX + uY * vY) / norm(vX, vY);
    }


}