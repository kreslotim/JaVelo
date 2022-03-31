package ch.epfl.javelo.routing;


import ch.epfl.javelo.projection.PointCh;

/**
 * Recorded class representing a point on a route, closest to a given reference point which is near the route.
 *
 * @author Timofey Kreslo (310686)
 * @author Wei-En Hsieh (341271)
 */
public record RoutePoint(PointCh point, double position, double distanceToReference) {
    /**
     * point, position are a point on the Route
     */

    /**
     * Non-existent point on a route
     */
    public static final RoutePoint NONE = new RoutePoint(null, Double.NaN, Double.POSITIVE_INFINITY);

    /**
     * Returns an identical RoutePoint with position shifted by the given distance
     *
     * @param positionDifference distance d separating the reference point and the route point
     * @return new identical RoutePoint but shifted by the given distance
     */
    public RoutePoint withPositionShiftedBy(double positionDifference) {
        return new RoutePoint(point, position + positionDifference, distanceToReference);
    }

    /**
     * Returns the nearest (with minimal distance) RoutePoint to the reference point
     *
     * @param that another point on the route
     * @return the nearest RoutePoint to the reference
     */
    public RoutePoint min(RoutePoint that) {
        return (distanceToReference <= that.distanceToReference) ? this : that;
    }


    /**
     * Overload of the previous min() method, that returns the route point
     * of the minimal distance to the reference point
     *
     * @param thatPoint another point on the route
     * @param thatPosition another's route point's position
     * @param thatDistanceToReference another route point's distance to reference
     * @return the nearest (with minimal distance) RoutePoint to the reference point
     */
    public RoutePoint min(PointCh thatPoint, double thatPosition, double thatDistanceToReference) {
        return (distanceToReference <= thatDistanceToReference) ?
                this : new RoutePoint(thatPoint, thatPosition, thatDistanceToReference);
    }

}