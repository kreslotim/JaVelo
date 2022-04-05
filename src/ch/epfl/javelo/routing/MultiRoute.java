package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;

import java.util.ArrayList;
import java.util.List;

/**
 * Immutable class representing a Multiroute (multiple routes) composed of segments,
 * that can be either Multiroutes or Singleroutes,
 * linking starting and ending points, and may have intermediate waypoints.
 *
 * @author Tim Kreslo (310686)
 * @author Wei-En Hsieh (341271)
 */
public final class MultiRoute implements Route {
    private final List<Route> segments;
    public MultiRoute(List<Route> segments) {
        Preconditions.checkArgument(!segments.isEmpty());
        this.segments = List.copyOf(segments);
    }

    /**
     * Returns the index of the segment on the route, containing the given position along the route
     *
     * @param position on the entire route (in meters)
     * @return the index of the segment on the route
     */
    @Override
    public int indexOfSegmentAt(double position) {
        double pos = Math2.clamp(0, position, length());
        int indexOfSegment = 0;
        double lengthTotal = 0;
        for (Route segment : segments) { // Use of recursion throughout each segment
                                         // that can be either a Multiroute or a Singleroute
            indexOfSegment = (pos > lengthTotal + segment.length())
                    ? indexOfSegment + segment.indexOfSegmentAt(segment.length()) + 1
                    : indexOfSegment + segment.indexOfSegmentAt(pos - lengthTotal);
            lengthTotal += segment.length();
        }
        return indexOfSegment;
    }

    /**
     * Returns the length of the entire route, in meters
     *
     * @return the length of the route, in meters
     */
    @Override
    public double length() {
        double lengthOfRoute = 0;
        for (Route segment : segments) lengthOfRoute += segment.length();
        return lengthOfRoute;
    }

    /**
     * Returns all the edges of the entire route
     *
     * @return all the edges of the route
     */
    @Override
    public List<Edge> edges() {
        List<Edge> allEdgesOnRoute = new ArrayList<>();
        for (Route segment : segments) allEdgesOnRoute.addAll(segment.edges());
        return allEdgesOnRoute;
    }

    /**
     * Returns all the points located at the extremities of each edge on the route
     *
     * @return all the points on the route
     */
    @Override
    public List<PointCh> points() {
        List<PointCh> allPointsOnRoute = new ArrayList<>();
        for (Route segment : segments) {
            allPointsOnRoute.addAll(segment.points());
            allPointsOnRoute.remove(allPointsOnRoute.size()-1);
        }
        Route lastRoute = segments.get(segments.size()-1);
        Edge lastEdge = lastRoute.edges().get(lastRoute.edges().size()-1);
        PointCh lastPoint = lastEdge.toPoint();
        allPointsOnRoute.add(lastPoint);
        return allPointsOnRoute;
    }

    /**
     * Returns the point (Swiss point) at the given position along the entire route
     *
     * @param position on the entire route (in meters)
     * @return the point at the given position, along the entire route
     */
    @Override
    public PointCh pointAt(double position) {
        double pos = Math2.clamp(0, position, length());
        double lengthTotal = 0;
        for (Route segment : segments) {
            if (pos <= lengthTotal + segment.length()) return segment.pointAt(pos - lengthTotal);
            lengthTotal += segment.length();
        }
        //Unreachable case for correct data -> should never be executed.
        //However, if the condition is never satisfied, return result of the last position
        return segments.get(segments.size()-1).pointAt(lengthTotal);
    }

    /**
     * Returns the altitude at the given position along the route,
     * which can be NaN if the edge containing this position has no profile
     *
     * @param position on the route (in meters)
     * @return altitude at the given position, along the route
     */
    @Override
    public double elevationAt(double position) {
        double pos = Math2.clamp(0, position, length());
        double lengthTotal = 0;
        for (Route segment : segments) {
            if (pos <= lengthTotal + segment.length()) return segment.elevationAt(pos - lengthTotal);
            lengthTotal += segment.length();
        }
        //Unreachable case for correct data -> should never be executed.
        //However, if the condition is never satisfied, return result of the last position
        return segments.get(segments.size()-1).elevationAt(lengthTotal);
    }

    /**
     * Returns the identity of the node that belongs to the route, and is the closest to the given position
     *
     * @param position on the route (in meters)
     * @return nodeId of the node that belongs to the route, and is the closest to the given position
     */
    @Override
    public int nodeClosestTo(double position) {
        double pos = Math2.clamp(0, position, length());
        double lengthTotal = 0;
        for (Route segment : segments) {
            if (pos <= lengthTotal + segment.length()) return segment.nodeClosestTo(pos - lengthTotal);
            lengthTotal += segment.length();
        }
        //Unreachable case for correct data -> should never be executed.
        //However, if the condition is never satisfied, return result of the last position
        return segments.get(segments.size()-1).nodeClosestTo(lengthTotal);
    }

    /**
     * Returns the point on the route (a RoutePoint), that is the closest to the given reference point
     *
     * @param point a reference point with Swiss coordinates (anywhere on the map)
     * @return the closest point on the route, to the given reference point
     */
    @Override
    public RoutePoint pointClosestTo(PointCh point) {

        RoutePoint currentRoutePoint;
        RoutePoint nearestRoutePoint = segments.get(0).pointClosestTo(point); // Closest RoutePoint on first segment
        double lengthTotal = 0;

        for (Route segment : segments) {
            currentRoutePoint = segment.pointClosestTo(point);
            nearestRoutePoint = nearestRoutePoint.min(currentRoutePoint.withPositionShiftedBy(lengthTotal));
            lengthTotal += segment.length();
        }

        return nearestRoutePoint;
    }
}
