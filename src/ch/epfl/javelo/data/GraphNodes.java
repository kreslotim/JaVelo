package ch.epfl.javelo.data;

import ch.epfl.javelo.Bits;
import ch.epfl.javelo.Q28_4;

import java.nio.IntBuffer;

/**
 * GraphNodes, a record representing the array of all nodes of the JaVelo graph.
 *
 * @author Timofey Kreslo (310686)
 * @author Wei-En Hsieh (341271)
 */
public record GraphNodes(IntBuffer buffer) {

    /* Node's attributes are distributed over 96 bits in total = 12 Bytes = 3 Integers */
    private static final int OFFSET_E = 0;
    private static final int OFFSET_N = OFFSET_E + 1;
    private static final int OFFSET_OUT_EDGES = OFFSET_N + 1;
    private static final int NODE_INTS = OFFSET_OUT_EDGES + 1;

    /**
     * Counts the total number of nodes inside the GraphNodes buffer
     *
     * @return the total number of nodes
     */
    public int count() { return (buffer.capacity()) / 3;}

    /**
     * Returns the East coordinate of the given node of the graph
     *
     * @param nodeId node's identity
     * @return the E (East) coordinate of the given node's identity
     */
    public double nodeE(int nodeId) { return Q28_4.asDouble(buffer.get(nodeId * NODE_INTS + OFFSET_E)); }

    /**
     * Returns the North coordinate of the given node of the graph
     *
     * @param nodeId node's identity
     * @return the N (North) coordinate of the given node's identity
     */
    public double nodeN(int nodeId) { return Q28_4.asDouble(buffer.get(nodeId * NODE_INTS + OFFSET_N)); }

    /**
     * Returns the number of outgoing edges of the given node of the graph
     *
     * @param nodeId node's identity
     * @return the number of edges exiting the given node's identity
     */
    public int outDegree(int nodeId) {
        int slice = buffer.get(nodeId * NODE_INTS + OFFSET_OUT_EDGES);
        return Bits.extractUnsigned(slice, 28, 4);
    }

    /**
     * Returns the identity of the edgeIndex-th edge that exits the given node's identity of the graph
     *
     * @param nodeId    node's identity
     * @param edgeIndex edge's index
     * @return the identity of the edgeIndex-th edge that exits the given node
     */
    public int edgeId(int nodeId, int edgeIndex) {
        assert 0 <= edgeIndex && edgeIndex < outDegree(nodeId);
        int slice = buffer.get(nodeId * NODE_INTS + OFFSET_OUT_EDGES);
        return Bits.extractUnsigned(slice, 0, 28) + edgeIndex;
    }
}