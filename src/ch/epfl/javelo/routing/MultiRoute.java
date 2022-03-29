package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.projection.PointCh;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class MultiRoute implements Route {
    private final List<Route> segments;

    public MultiRoute(List<Route> segments) {
        Preconditions.checkArgument(!segments.isEmpty());
        this.segments = List.copyOf(segments);
        //TODO should do a positionTab in constructor, like in SingleRoute ?

    }

    /**
     * Returns the index of the segment on the route, containing the given position
     *
     * @param position on the segment
     * @return the index of the segment
     */
    @Override
    public int indexOfSegmentAt(double position) {
        double pos = Math2.clamp(0, position, length());
        int indexOfSegment = 0;
        for (int segmentIndex = 0; segmentIndex < segments.size(); segmentIndex++) {

            Route currentSegment = segments.get(segmentIndex);

            if (pos < currentSegment.length()) {
                currentSegment.indexOfSegmentAt(pos);
            }

            else {
                indexOfSegment = currentSegment.indexOfSegmentAt(currentSegment.length()); // must return 2

                return indexOfSegment;
            }
        }
        return indexOfSegment;
    }

    /**
     * Returns the length of the route, in meters
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
     * Returns all the edges of the route
     *
     * @return all the edges of the route
     */
    @Override
    public List<Edge> edges() {
        List<Edge> allEdgesOnRoute = new ArrayList<>();
        for (Route segment : segments) {
            allEdgesOnRoute.addAll(segment.edges());
        }
        return allEdgesOnRoute;
    }

    /**
     * Returns all the points located at the extremities of each edge on the route
     *
     * @return all the points located at the extremities of each edge on the route
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
     * Returns the point at the given position along the route
     *
     * @param position on the route, in meters
     * @return
     */
    @Override
    public PointCh pointAt(double position) {
        double pos = Math2.clamp(0, position, length());
        return null;
    }

    /**
     * Returns the altitude at the given position along the route,
     * which can be NaN if the edge containing this position has no profile
     *
     * @param position on the route, in meters
     * @return altitude at the given position along the route
     */
    @Override
    public double elevationAt(double position) {
        double pos = Math2.clamp(0, position, length());
        return 0;
    }

    /**
     * Returns the identity of the node that belongs to the route and is the closest to the given position
     *
     * @param position on the route, in meters
     * @return nodeId of the node that belongs to the route, and is the closest to the given position
     */
    @Override
    public int nodeClosestTo(double position) {
        double pos = Math2.clamp(0, position, length());
        return 0;
    }

    /**
     * Returns the closest route point to the given reference point
     *
     * @param point A reference point with Swiss coordinates (anywhere on the map)
     * @return the closest point on the route, to the given reference point
     */
    @Override
    public RoutePoint pointClosestTo(PointCh point) {
        return null;
    }


    //private int segmentId(double pos) {}
}
