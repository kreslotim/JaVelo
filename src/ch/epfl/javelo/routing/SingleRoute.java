package ch.epfl.javelo.routing;


import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Immutable class representing a Singleroute (simple route) composed of edges,
 * linking starting and ending points without intermediate waypoints.
 *
 * @author Tim Kreslo (310686)
 * @author Wei-En Hsieh (341271)
 */
public final class SingleRoute implements Route {
    private final List<Edge> edges;
    private final double[] positionsTab;

    /**
     * Default SingleRoute constructor
     */
    public SingleRoute(List<Edge> edges) {
        Preconditions.checkArgument(!edges.isEmpty());
        this.edges = List.copyOf(edges);
        positionsTab = new double[edges.size()+1];
        //Must shift the entire tab right, for inserting position 0 at the first index.

        //first position set to .0 automatically
        for (int i = 1; i <= edges.size(); i++) {
            positionsTab[i] = positionsTab[i-1] + edges.get(i-1).length();
        }
    }


    /**
     * Returns the index of the segment on the route, containing the given position (always 0 in a simple route)
     *
     * @param position on the segment (simple route)
     * @return 0 (the index of this single route)
     */
    @Override
    public int indexOfSegmentAt(double position) {
        return 0;
    }


    /**
     * Returns the length of the (simple) route, in meters
     *
     * @return
     */
    @Override
    public double length() {
        double lenghtOfEdge = 0;
        for (Edge edge : edges) lenghtOfEdge += edge.length();
        return lenghtOfEdge;
    }


    /**
     * Returns the list of all the edges, of the (simple) route
     *
     * @return all edges of the route
     */
    @Override
    public List<Edge> edges() { return edges;}


    /**
     * Returns the list of all the points, located at the extremities of each edge, on the (simple) route
     *
     * @return all the points located at the extremities of each edge on the route
     */
    @Override
    public List<PointCh> points() {
        List<PointCh> pointChList = new ArrayList<>();
        for (Edge edge : edges) pointChList.add(edge.fromPoint());
        pointChList.add(edges.get(edges.size()-1).toPoint());
        return pointChList;
    }

    /**
     * Returns the point (Swiss point) at the given position along the (simple) route
     *
     * @param position on the route (in meters)
     * @return the point at the given position
     */
    @Override
    public PointCh pointAt(double position) {
        double pos = Math2.clamp(0, position, length());

        int edgeId = edgeId(pos);

        double positionOnEdge = pos - positionsTab[edgeId];
        Edge rightEdge = edges.get(edgeId);
        return rightEdge.pointAt(positionOnEdge);
    }


    /**
     * Returns the altitude at the given position along the (simple) route,
     * which can be NaN if the edge containing this position has no profile
     *
     * @param position on the route (in meters)
     * @return altitude at the given position along the route
     */
    @Override
    public double elevationAt(double position) {
        double pos = Math2.clamp(0, position, length());

        int edgeId = edgeId(pos);

        double positionOnEdge = pos - positionsTab[edgeId];
        Edge rightEdge = edges.get(edgeId);
        return rightEdge.elevationAt(positionOnEdge);
    }

    /**
     * Returns the identity of the node that belongs to the route and is the closest to the given position
     *
     * @param position on the route (in meters)
     * @return nodeId of the node that belongs to the route, and is the closest to the given position
     */
    @Override
    public int nodeClosestTo(double position) {
        double pos = Math2.clamp(0, position, length());

        int edgeId = edgeId(pos);

        double positionOnEdge = pos - positionsTab[edgeId];
        Edge rightEdge = edges.get(edgeId);
        return (positionOnEdge <= rightEdge.length()/2.) ? rightEdge.fromNodeId() : rightEdge.toNodeId();
    }


    /**
     * Returns the closest route point to the given reference point
     *
     * @param point A reference point with Swiss coordinates (anywhere on the map)
     * @return the closest point on the route, to the given reference point
     */
    @Override
    public RoutePoint pointClosestTo(PointCh point) {
        double minDistance = Integer.MAX_VALUE;
        double posClosestToPoint;
        double gapOnEdge = 0;
        double distanceToReference;
        PointCh pointOnEdge = new PointCh(SwissBounds.MAX_E, SwissBounds.MAX_N);
        RoutePoint nearestRoutePoint = RoutePoint.NONE;

        for (int i = 0; i < edges.size(); i++) {

            posClosestToPoint = Math2.clamp(0, edges.get(i).positionClosestTo(point), edges.get(i).length());
            PointCh pointOnEdgeTemp =  edges.get(i).pointAt(posClosestToPoint);
            distanceToReference = pointOnEdgeTemp.distanceTo(point);

            if (distanceToReference <= minDistance) { //minimizing distance to reference
                minDistance = distanceToReference;
                gapOnEdge = posClosestToPoint;
                pointOnEdge = pointOnEdgeTemp;
            }
            nearestRoutePoint = nearestRoutePoint.min(pointOnEdge, positionsTab[i] + gapOnEdge, minDistance);
        }

        return nearestRoutePoint;
    }

    /**
     * Auxiliary (private) method, that computes (using binary search) the index of the edge,
     * to be searched in the array of positions.
     *
     * @param pos position clamped between 0 and the length of the (simple) route
     * @return index of the edge, to be searched in the array
     */
    private int edgeId(double pos) {
        int insertionPoint = Arrays.binarySearch(positionsTab, pos);
        int edgeIndex;
        //might be exactly the edge's position -> Severance of cases
        edgeIndex = (insertionPoint < 0) ? -insertionPoint - 2 : insertionPoint;
        //return edge's index;
        return (edgeIndex < edges.size()) ? edgeIndex : edgeIndex-1;
    }

}