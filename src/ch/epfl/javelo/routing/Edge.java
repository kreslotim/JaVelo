package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;

import java.util.function.DoubleUnaryOperator;

/**
 * Recorded class representing an edge of a route.
 *
 * @author Tim Kreslo (310686)
 * @author Wei-En Hsieh (341271)
 */
public record Edge(int fromNodeId, int toNodeId, PointCh fromPoint, PointCh toPoint, double length,
                   DoubleUnaryOperator profile) {


    /**
     * Provides the construction of an instance of type Edge
     *
     * @param graph      the graph (Graph)
     * @param edgeId     the identity edge
     * @param fromNodeId the identity of the start node of the edge
     * @param toNodeId   the identity of the arrival node of the edge,
     * @return an instance of Edge whose attributes fromNodeId and toNodeId are those given, the others being those of
     * the edge's identity edgeId in the graph Graph.
     */
    public static Edge of(Graph graph, int edgeId, int fromNodeId, int toNodeId) {
        return new Edge(fromNodeId, toNodeId, graph.nodePoint(fromNodeId), graph.nodePoint(toNodeId),
                graph.edgeLength(edgeId), graph.edgeProfile(edgeId));
    }

    /**
     * Return the position (in meters) along the edge,
     * that is the closest to the given reference point (anywhere on the map),
     * using orthogonal projection on the current edge.
     *
     * @param point the point (PointCh) that is anywhere on the map
     * @return the position (in meters) along the edge, that is the closest to the given point
     */
    public double positionClosestTo(PointCh point) {
        return Math2.projectionLength(fromPoint.e(), fromPoint.n(), toPoint.e(), toPoint.n(), point.e(), point.n());
    }

    /**
     * Return the point (PointCh) at the given position (in meters), on the edge.
     *
     * @param position the given position (in meters) on the edge
     * @return the point at the given position on the edge.
     */
    public PointCh pointAt(double position) {
        return new PointCh(Math2.interpolate(fromPoint.e(), toPoint.e(), position / length),
                // divide by length because we want to bring x between 0 and 1, where length is the length of a single edge.
                Math2.interpolate(fromPoint.n(), toPoint.n(), position / length));
    }

    /**
     * Return the altitude, in meters, at the given position (in meters), on the edge.
     *
     * @param position the given position
     * @return the altitude, in meters, at the given position on the edge
     */
    public double elevationAt(double position) {
        return profile.applyAsDouble(position);
    }
}