package ch.epfl.javelo.routing;


import ch.epfl.javelo.projection.PointCh;

/**
 * Recorded class representing a point on the route (RoutePoint),
 * that is the closest to a given reference point (PointCh), near the route.
 *
 * @author Timofey Kreslo (310686)
 * @author Wei-En Hsieh (341271)
 */
public record RoutePoint(PointCh point, double position, double distanceToReference) {
    /* @param point, and
       @param position are a chosen on the Route */

    /**
     * Non-existent point on a route (RoutePoint)
     */
    public static final RoutePoint NONE = new RoutePoint(null, Double.NaN, Double.POSITIVE_INFINITY);

    /**
     * Returns this (actual) RoutePoint with its position shifted by the given distance
     *
     * @param positionDifference distance d separating the reference point and the route point
     * @return new identical RoutePoint but shifted by the given distance
     */
    public RoutePoint withPositionShiftedBy(double positionDifference) {
        return new RoutePoint(point, position + positionDifference, distanceToReference);
    }

    /**
     * Compares this (actual) RoutePoint with a given RoutePoint,
     * and returns the nearest (with minimal distance to reference) RoutePoint to the reference point.
     *
     * @param that another point on the route
     * @return the nearest RoutePoint to the reference
     */
    public RoutePoint min(RoutePoint that) {
        return (distanceToReference <= that.distanceToReference) ? this : that;
    }


    /**
     * Overload of the previous min() method, that returns the route point
     * with the minimal distance to the reference point
     *
     * @param thatPoint another point on the route
     * @param thatPosition another route point's position
     * @param thatDistanceToReference another route point's distance to reference
     * @return the nearest (with minimal distance to reference) RoutePoint to the reference point
     */
    public RoutePoint min(PointCh thatPoint, double thatPosition, double thatDistanceToReference) {
        return (distanceToReference <= thatDistanceToReference) ?
                this : new RoutePoint(thatPoint, thatPosition, thatDistanceToReference);
    }

}