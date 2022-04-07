package ch.epfl.javelo.projection;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;

/**
 * Recorded class that measures distance between two points (PointCh) in meters
 *
 * @author Tim Kreslo (310686)
 * @author Wei-En Hsieh (341271)
 */

public record PointCh(double e, double n) {

    /**
     * Compact PointCh constructor
     *
     * @param e East coordinate
     * @param n North coordinate
     * @throws IllegalArgumentException if the given coordinates are outside Switzerland's bounds
     */
    public PointCh {
        Preconditions.checkArgument(SwissBounds.containsEN(e, n));
    }

    /**
     * Measures squared distance between this and another point on the map
     *
     * @param that target point on the map
     * @return (double) squared distance between received and target point on the map
     */
    public double squaredDistanceTo(PointCh that) {
        //return Math.pow(distanceTo(that),2); // squaring distanceTo()
        return Math2.squaredNorm(that.e - this.e, that.n - this.n); // usage of Math2
    }

    /**
     * Measures distance between this and another point on the map
     *
     * @param that target point on the map
     * @return (double) distance between received and target point on the map
     */
    public double distanceTo(PointCh that) {
        //return Math.hypot(that.e - this.e, that.n - this.n); // returns hypothesis length
        return Math2.norm(that.e - this.e, that.n - this.n); // usage of Math2
    }

    /**
     * Computes longitude using East and North coordinates
     *
     * @return (double) longitude in radians
     */
    public double lon() {
        return Ch1903.lon(e, n);
    }

    /**
     * Computes latitude using East and North coordinates
     *
     * @return (double) latitude in radians
     */
    public double lat() {
        return Ch1903.lat(e, n);
    }

}
