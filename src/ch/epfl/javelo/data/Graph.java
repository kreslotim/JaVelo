package ch.epfl.javelo.data;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.projection.PointCh;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

/**
 * Immutable class representing the JaVelo Graph
 *
 * @author Timofey Kreslo (310686)
 * @author Wei-En Hsieh (341271)
 */
public final class Graph {

    private final GraphNodes nodes;
    private final GraphSectors sectors;
    private final GraphEdges edges;
    private final List<AttributeSet> attributeSets;


    /**
     * Default Graph constructor
     *
     * @param nodes         the graph nodes
     * @param sectors       the graph sectors
     * @param edges         the graph edges
     * @param attributeSets the graph's attribute sets
     */
    public Graph(GraphNodes nodes, GraphSectors sectors, GraphEdges edges, List<AttributeSet> attributeSets) {
        this.nodes = nodes;
        this.sectors = sectors;
        this.edges = edges;
        this.attributeSets = List.copyOf(attributeSets);
    }


    /**
     * Returns Javelo graph (Graph), obtained from the files located in the given directory
     *
     * @param  basePath path to given directory (Path)
     * @return Javelo graph (Graph)
     * @throws IOException if the expected file does not exist
     */
    public static Graph loadFrom(Path basePath) throws IOException { // basePath = file lausanne
        LongBuffer attributesChannel = fileName(basePath, "attributes.bin").asLongBuffer();
        ByteBuffer edgesChannel = fileName(basePath, "edges.bin");
        ShortBuffer elevationsChannel = fileName(basePath, "elevations.bin").asShortBuffer();
        IntBuffer nodesChannel = fileName(basePath, "nodes.bin").asIntBuffer();// from graphNodes
        IntBuffer nodesOsmidChannel = fileName(basePath, "nodes_osmid.bin").asIntBuffer();
        IntBuffer profileIdsChannel = fileName(basePath, "profile_ids.bin").asIntBuffer();
        ByteBuffer sectorsChannel = fileName(basePath, "sectors.bin");

        List<AttributeSet> attributeSets = new ArrayList<>();
        for (int i = 0; i < attributesChannel.capacity(); ++i) {
            attributeSets.add(new AttributeSet(attributesChannel.get(i)));
        }
        return new Graph(new GraphNodes(nodesChannel), new GraphSectors(sectorsChannel),
                new GraphEdges(edgesChannel, profileIdsChannel, elevationsChannel), attributeSets);
    }


    /**
     * Auxiliary (private) method that opens the given fileName,
     * gets the path to the file containing the nodes,
     * and creates a buffer (ByteBuffer) with the needed information.
     *
     * @param basePath (Path) path to given file
     * @param fileName (String) name of the file to load from
     * @return buffer (ByteBuffer) containing the nodes
     * @throws IOException if the expected file does not exist
     */
    private static ByteBuffer fileName(Path basePath, String fileName) throws IOException {
        // open the file :
        try (FileChannel nodesChannel = FileChannel.open(basePath.resolve(fileName))) {
            return nodesChannel.map(FileChannel.MapMode.READ_ONLY, 0, nodesChannel.size());
        }
    }


    /**
     * Counts the total number of nodes inside the JaVelo Graph
     *
     * @return total number of nodes
     */
    public int nodeCount() {
        return nodes.count();
    }


    /**
     * Returns the point (PointCh) of a given node
     *
     * @param nodeId node's identity
     * @return point (PointCh) at the node's position
     */
    public PointCh nodePoint(int nodeId) {
        return new PointCh(nodes.nodeE(nodeId), nodes.nodeN(nodeId));
    }


    /**
     * Returns the number of outgoing edges, out of the given node's identity
     *
     * @param nodeId node's identity
     * @return number of outgoing edges
     */
    public int nodeOutDegree(int nodeId) {
        return nodes.outDegree(nodeId);
    }


    /**
     * Returns the identity of the edgeIndex-th edge that leaves the given node
     *
     * @param nodeId node's identity
     * @param edgeIndex edge's index
     * @return the identity of the edgeIndex-th edge that leaves the given node
     */
    public int nodeOutEdgeId(int nodeId, int edgeIndex) {
        return nodes.edgeId(nodeId, edgeIndex);
    }


    /**
     * Returns closest node's identity (nodeId) to the given point (PointCh), anywhere on the map.
     *
     * @param point (PointCh) anywhere on the map
     * @param searchDistance distance to search around
     * @return closest node's identity
     */
    public int nodeClosestTo(PointCh point, double searchDistance) {

        List<GraphSectors.Sector> sectorsNearestBy = sectors.sectorsInArea(point, searchDistance);

        double minDistance = Integer.MAX_VALUE;
        int nearestNodeId = -1;
        double actualDistance;

        for (GraphSectors.Sector sector : sectorsNearestBy) {
            for (int nodeId = sector.startNodeId(); nodeId < sector.endNodeId(); nodeId++) {

                PointCh targetPoint = nodePoint(nodeId);

                actualDistance = point.squaredDistanceTo(targetPoint);

                if (actualDistance < minDistance && actualDistance <= Math.pow(searchDistance, 2)) {
                    minDistance = actualDistance;
                    nearestNodeId = nodeId;
                }
            }
        }

        return nearestNodeId;
    }


    /**
     * Returns node's identity, which is at the end of the given edge
     *
     * @param edgeId edge's identity
     * @return target node's ID, of the given edge
     */
    public int edgeTargetNodeId(int edgeId) {
        return edges.targetNodeId(edgeId);
    }


    /**
     * Returns true if the given edge goes in the opposite direction of the OSM way, from where it comes
     *
     * @param edgeId edge's identity
     * @return (boolean) true if the given edge goes in the opposite direction of the OSM way
     */
    public boolean edgeIsInverted(int edgeId) {
        return edges.isInverted(edgeId);
    }


    /**
     * Return the set of OSM attributes attached to the given edge's identity
     *
     * @param edgeId edge's Identity
     * @return the set (aka the list) of OSM attributes attached to the given identity edge
     */
    public AttributeSet edgeAttributes(int edgeId) {
        return attributeSets.get(edges.attributesIndex(edgeId));
    }


    /**
     * Return the length, in meters, of the given edge's identity
     *
     * @param edgeId edge's identity
     * @return the length (in meters) of the given identity edge
     */
    public double edgeLength(int edgeId) {
        return edges.length(edgeId); //
    }


    /**
     * Return the positive elevation gain (in meters) of the edge with the given identity
     *
     * @param edgeId edge's identity
     * @return the elevation gain of the edge
     */
    public double edgeElevationGain(int edgeId) {
        return edges.elevationGain(edgeId);
    }


    /**
     * Return the profile along the given edge's identity, as a function
     *
     * @param edgeId edge's identity
     * @return the profile along the given edge
     */
    public DoubleUnaryOperator edgeProfile(int edgeId) {
        return (edges.hasProfile(edgeId) ? Functions.sampled(edges.profileSamples(edgeId), edgeLength(edgeId)) :
                Functions.constant(Double.NaN));
    }
}