package ch.epfl.javelo.routing;


import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A simple route composed of edges, linking starting and ending points without intermediate waypoints
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
        positionsTab = new double[edges.size()+1]; //one more than the edges
        //must shift the entire tab

        //first position set to .0 automatically
        for (int i = 1; i <= edges.size(); i++) {
            positionsTab[i] = positionsTab[i-1] + edges.get(i-1).length();
        }
    }


    /**
     * Returns the index of the segment on the route, containing the given position (always 0 in a single route)
     *
     * @param position on the segment (single route)
     * @return 0 (the index of this single route)
     */
    @Override
    public int indexOfSegmentAt(double position) {
        return 0;
    }


    /**
     * Returns the length of the route, in meters
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
     * Returns all the edges of the route
     *
     * @return all the edges of the route
     */
    @Override
    public List<Edge> edges() {
        return edges;
    }


    /**
     * Returns all the points located at the extremities of each edge on the route
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
     * Returns the point at the given position along the route
     *
     * @param position on the route, in meters
     * @return
     */
    @Override
    public PointCh pointAt(double position) {
        double pos = Math2.clamp(0, position, length());

        int edgeId = edgeId(pos);

        //edgeId = (edgeId < edges.size()) ? edgeId : edgeId - 1;

        double positionOnEdge = pos - positionsTab[edgeId];
        Edge rightEdge = edges.get(edgeId);
        return rightEdge.pointAt(positionOnEdge);
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

        int edgeId = edgeId(pos);
        edgeId = (edgeId < edges.size()) ? edgeId : edgeId + 1;

        double positionOnEdge = pos - positionsTab[edgeId];
        Edge rightEdge = edges.get(edgeId);
        return rightEdge.elevationAt(positionOnEdge);
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
        double dProjection = 0;
        double l = 0;
        double d = 0;
        PointCh pointOnEdge = new PointCh(SwissBounds.MAX_E, SwissBounds.MAX_N);
        RoutePoint nearestRoutePoint = RoutePoint.NONE;

        for (int i = 0; i < edges.size(); i++) {

            dProjection = Math2.clamp(0, edges.get(i).positionClosestTo(point), edges.get(i).length());
            PointCh pointOnEdgeTemp =  edges.get(i).pointAt(dProjection);
            d = pointOnEdgeTemp.distanceTo(point);

            if (d <= minDistance) {
                minDistance = d; //minimazing distance d
                l = dProjection;
                pointOnEdge = pointOnEdgeTemp;
            }

            nearestRoutePoint = nearestRoutePoint.min(pointOnEdge, positionsTab[i] + l, minDistance);
        }

        return nearestRoutePoint;
    }

    private int edgeId(double pos) {
        int insertionPoint = Arrays.binarySearch(positionsTab, pos);
        int edgeIndex;

        //might be exactly the edge -> disjonction des cas
        edgeIndex = (insertionPoint < 0) ? -insertionPoint - 2 : insertionPoint;
        //return edgeIndex;
        return (edgeIndex < edges.size()) ? edgeIndex : edgeIndex-1;
    }

}