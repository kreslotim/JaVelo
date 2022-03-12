package ch.epfl.javelo.data;

import ch.epfl.javelo.Bits;
import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.Q28_4;

import java.nio.IntBuffer;

/**
 * Recorded class representing the array of all nodes of the JaVelo graph
 *
 * @author Timofey Kreslo (310686)
 * @author Wei-En Hsieh (341271)
 */
public record GraphNodes(IntBuffer buffer) {

    private static final int OFFSET_E = 0;
    private static final int OFFSET_N = OFFSET_E + 1;
    private static final int OFFSET_OUT_EDGES = OFFSET_N + 1;
    private static final int NODE_INTS = OFFSET_OUT_EDGES + 1;

    /**
     * Counts the total number of nodes inside the GraphNodes buffer
     *
     * @return the total number of nodes
     */
    public int count() {
        return (buffer.capacity()) / 3;
    }

    /**
     * Returns the east coordinate of the given node
     *
     * @param nodeId Node's identity
     * @return the E coordinate of the given identity node
     */
    public double nodeE(int nodeId) {
        return Q28_4.asDouble(buffer.get(nodeId * NODE_INTS + OFFSET_E));
    }

    /**
     * Returns the north coordinate of the given node
     *
     * @param nodeId Node's identity
     * @return the N coordinate of the given identity node
     */
    public double nodeN(int nodeId) {
        return Q28_4.asDouble(buffer.get(nodeId * NODE_INTS + OFFSET_N));
    }

    /**
     * Returns the number of edges outgoing of the given node
     *
     * @param nodeId Node's identity
     * @return the number of edges exiting the given identity node
     */
    public int outDegree(int nodeId) {
        int slice = buffer.get(nodeId * NODE_INTS + OFFSET_OUT_EDGES);
        return Bits.extractUnsigned(slice, 28, 4);
    }

    /**
     * Returns the identity of the edgeIndex-th edge that exits the given node
     *
     * @param nodeId Node's identity
     * @param edgeIndex Edge's index
     * @return the identity of the edgeIndex-th edge that exits the given node
     */
    public int edgeId(int nodeId, int edgeIndex) {
        assert 0 <= edgeIndex && edgeIndex < outDegree(nodeId);
        int slice = buffer.get(nodeId*NODE_INTS + OFFSET_OUT_EDGES);
        return Bits.extractUnsigned(slice, 0, 28) + edgeIndex;
    }
}
