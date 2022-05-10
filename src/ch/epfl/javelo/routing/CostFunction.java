package ch.epfl.javelo.routing;

/**
 * Interface representing the cost function, applied on the route
 *
 * @author Tim Kreslo (310686)
 * @author Wei-En Hsieh (341271)
 */
public interface CostFunction {

    /**
     * Returns the factor by which the length of the edgeId,
     * starting from the nodeId, should be multiplied;
     * This factor must imperatively be greater than or equal to 1
     *
     * @param nodeId Identity of the node, from which the edge exists.
     * @param edgeId Identity of the edge, in the whole graph (with all edges)
     * @return factor to multiply by (must be greater or equal to 1)
     */
    double costFactor(int nodeId, int edgeId);
}
