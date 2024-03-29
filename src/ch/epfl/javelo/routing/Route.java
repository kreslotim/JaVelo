package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;

import java.util.List;

/**
 * Interface representing a Route
 *
 * @author Tim Kreslo (310686)
 * @author Wei-En Hsieh (341271)
 */
public interface Route {


    /**
     * Return the index of the segment at the given position (in meters)
     *
     * @param position the given position (in meters)
     * @return the index of the segment at the given position (in meters)
     */
    int indexOfSegmentAt(double position);


    /**
     * Return the length of the route, in meters
     *
     * @return the length of the route, in meters
     */
    double length();


    /**
     * Return all the edges of the route
     *
     * @return all the edges of the route
     */
    List<Edge> edges();


    /**
     * Return all the points located at the extremities of the edges of the route
     *
     * @return all the points located at the extremities of the edges of the route
     */
    List<PointCh> points();


    /**
     * Return the point at the given position along the route
     *
     * @param position the given position along the route
     * @return the point at the given position along the route
     */
    PointCh pointAt(double position);


    /**
     * Return the altitude at the given position along the route
     *
     * @param position the given position along the route
     * @return the altitude at the given position along the route
     */
    double elevationAt(double position);


    /**
     * Return the identity of the node belonging to the route and located closest to the given position
     *
     * @param position the given position
     * @return the identity of the node belonging to the route and located closest to the given position
     */
    int nodeClosestTo(double position);


    /**
     * Return the point on the route that is closest to the given reference point
     *
     * @param point the point of the route
     * @return the point on the route that is closest to the given reference point
     */
    RoutePoint pointClosestTo(PointCh point);
}