package ch.epfl.javelo;

/**
 * The class Math2, a variation of the class Math, contains methods for performing some mathematical calculations.
 *
 * @author Tim Kreslo (310686)
 * @author Wei-En Hsieh (341271)
 */
public final class Math2 {
    /**
     * Default (non instantiable) Math2 constructor.
     */
    private Math2() {
    }


    /**
     * Returns the integer part by excess of the division of x by y,
     * throwing an exception if x is strictly negative or y is negative.
     *
     * @param x numerator
     * @param y denominator
     * @return the ceiling division of x by y and is equal to a mathematical integer.
     * @throws IllegalArgumentException if x is strictly negative or y is negative
     */
    public static int ceilDiv(int x, int y) {
        Preconditions.checkArgument(!(x < 0 || y <= 0));
        return (x + y - 1) / y;
    }

    /**
     * Returns the y coordinate of the point on the line passing through (0,y0) and (1,y1) and given x coordinate,
     * y is the linear interpolation between two points.
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
     * Limits the value v to the interval from the minimal value to the maximal value,
     * where the minimal and maximal values are both mathematical integers.
     * returning the minimal value if v is less than the minimal value, the maximal value if v is greater than
     * the maximal value; otherwise, returns v,
     * throwing an exception if the minimal value is (strictly) greater than the maximal value.
     *
     * @param min (int) minimal value
     * @param v   value
     * @param max (int) maximal value
     * @return v (int) limited to bounds
     * @throws IllegalArgumentException if the minimal value is (strictly) greater than the maximal value
     */
    public static int clamp(int min, int v, int max) {
        Preconditions.checkArgument(max >= min);
        if (v < min) return min;
        return Math.min(v, max);
    }

    /**
     * Limits the value v to the interval from the minimal value to the maximal value,
     * where the minimal and maximal values are both real numbers.
     * returning the minimal value if v is less than the minimal value, the maximal value if v is greater than
     * the maximal value; otherwise, returns v,
     * throwing an exception if the minimal value is (strictly) greater than the maximal value.
     *
     * @param min (double) minimal value
     * @param v   value
     * @param max (double) maximal value
     * @return v (double) limited to bounds
     * @throws IllegalArgumentException if the minimal value is (strictly) greater than the maximal value
     */
    public static double clamp(double min, double v, double max) {
        Preconditions.checkArgument(max >= min);
        if (v < min) return min;
        return Math.min(v, max);
    }

    /**
     * Returns the inverse hyperbolic sine of a value x.
     *
     * @param x the given variable
     * @return (double) returns the inverse hyperbolic sine of x
     */
    public static double asinh(double x) {
        return Math.log(x + Math.hypot(1,x));
    }

    /**
     * Returns the scalar product of two vectors u (of components uX and uY) and v (of components vX and vY).
     *
     * @param uX the x component of vector u
     * @param uY the y component of vector u
     * @param vX the x component of vector v
     * @param vY the y component of vector v
     * @return (double) the scalar product of two vectors u and v.
     */
    public static double dotProduct(double uX, double uY, double vX, double vY) {
        return uX * vX + uY * vY;
    }

    /**
     * Returns the square norm of the vector u (of components uX and uY).
     *
     * @param uX x component of vector u
     * @param uY y component of vector u
     * @return (double) the square norm of the vector u.
     */
    public static double squaredNorm(double uX, double uY) {
        return dotProduct(uX, uY, uX, uY);
    }

    /**
     * Returns the norm of the vector u (of components uX and uY).
     *
     * @param uX x component of vector u
     * @param uY y component of vector u
     * @return (double) the norm of the vector u.
     */
    public static double norm(double uX, double uY) {
        return Math.sqrt(squaredNorm(uX, uY));
    }

    /**
     * Returns the length of the projection of the vector going from point A (of coordinates aX and aY)
     * to point P (of coordinates pX and pY) on the vector going from point A to point B (of components bY and bY).
     *
     * @param aX x component of point A
     * @param aY y component of point A
     * @param bX x component of point B
     * @param bY y component of point B
     * @param pX x component of point P
     * @param pY y component of point P
     * @return (double) the length of projection of vector from A to P on vector from A to B.
     */
    public static double projectionLength(double aX, double aY, double bX, double bY, double pX, double pY) {
        double uX = (pX - aX);
        double uY = (pY - aY);
        double vX = (bX - aX);
        double vY = (bY - aY);
        return dotProduct(uX, uY, vX, vY) / norm(vX, vY);
    }
}