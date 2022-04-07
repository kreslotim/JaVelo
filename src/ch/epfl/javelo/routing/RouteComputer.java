package ch.epfl.javelo.routing;

import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.data.Graph;

import java.util.*;

public final class RouteComputer {
    private final Graph graph;
    private final CostFunction costFunction;


    /**
     * Default RouteComputer constructor
     *
     * @param graph        the whole JaVelo graph
     * @param costFunction cost factor, for bike Routes.
     */
    public RouteComputer(Graph graph, CostFunction costFunction) {
        this.graph = graph;
        this.costFunction = costFunction;
    }


    /**
     * Returns the minimum total cost route from startNodeId to endNodeId,
     * in the graph given to the constructor, or null if no route exists.
     *
     * @param startNodeId starting Node identity
     * @param endNodeId   ending Node identity
     * @return the ideal route between two given nodes
     * @throws IllegalArgumentException if startNodeId and endNodeId are identical (no route exists)
     * @throws NullPointerException     if the route couldn't be built with provided data
     */
    public Route bestRouteBetween(int startNodeId, int endNodeId) {
        Preconditions.checkArgument(startNodeId != endNodeId);

        /*
          Nested recorded class representing a node, which is compared to a given node,
          by its distance to target node.

          @param nodeId node's identity
          @param distance between this and target node
         */
        record WeightedNode(int nodeId, float distance)
                implements Comparable<WeightedNode> {
            @Override
            public int compareTo(WeightedNode that) {
                return Float.compare(this.distance, that.distance);
            }
        }

        PriorityQueue<WeightedNode> Visiting = new PriorityQueue<>();

        float[] distance = new float[graph.nodeCount()];
        int[] predecessor = new int[graph.nodeCount()];

        int currentNode;      //index of the current Node
        int edge;             //index of the current Edge
        int nTemp;            // index of a temporary Node
        double totalDistance; // total distance through visited Nodes

        Arrays.fill(distance, 0, distance.length, Float.POSITIVE_INFINITY);
        Arrays.fill(predecessor, 0, predecessor.length, 0);

        distance[startNodeId] = 0f;
        Visiting.add(new WeightedNode(startNodeId, distance[startNodeId]));

        while (!Visiting.isEmpty()) {
            currentNode = Visiting.remove().nodeId; // for which distance[N] is minimal

            if (currentNode == endNodeId) return computeRoute(startNodeId, endNodeId, predecessor);

            if (distance[currentNode] != Float.NEGATIVE_INFINITY) {
                for (int E = 0; E < graph.nodeOutDegree(currentNode); E++) {
                    edge = graph.nodeOutEdgeId(currentNode, E); // considering current edge
                    nTemp = graph.edgeTargetNodeId(edge);

                    totalDistance = distance[currentNode]
                                  + costFunction.costFactor(currentNode, edge) * graph.edgeLength(edge);

                    if (totalDistance < distance[nTemp]) {
                        distance[nTemp] = (float) totalDistance;
                        predecessor[nTemp] = currentNode;
                        Visiting.add(new WeightedNode(nTemp, distance[nTemp]
                                + (float) graph.nodePoint(nTemp).distanceTo(graph.nodePoint(endNodeId))));
                    }
                }
                distance[currentNode] = Float.NEGATIVE_INFINITY;
            }
        }
        //case reached only if the route couldn't be build with provided data
        // -> should never be executed with correct data.
        return null;
    }

    /**
     * Auxiliary (private) method, that computes the final route
     * (using starting node index, ending node index and the array of predecessors), and returns a Route,
     * providing it a list of edges, composing the route.
     *
     * @param startNodeId Starting node index
     * @param endNodeId   Ending node index
     * @param predecessor Array of preceding nodes
     * @return Route, composed of a list of edges
     */
    private SingleRoute computeRoute(int startNodeId, int endNodeId, int[] predecessor) {
        List<Edge> edgesOfTheRoute = new ArrayList<>();

        int nodeId = endNodeId;

        while (nodeId != startNodeId) {

            for (int edgeTemp = 0; edgeTemp < graph.nodeOutDegree(predecessor[nodeId]); edgeTemp++) {

                int edgeId = graph.nodeOutEdgeId(predecessor[nodeId], edgeTemp);
                int nodeTemp = graph.edgeTargetNodeId(edgeId);
                if (nodeTemp == nodeId) {
                    Edge edgeOnRoute = Edge.of(graph, edgeId, predecessor[nodeId], nodeId);
                    edgesOfTheRoute.add(edgeOnRoute);
                    break;
                }
            }
            nodeId = predecessor[nodeId];
        }

        //The list contains inverted edges, in the reverse order -> order must be reversed for building path.
        Collections.reverse(edgesOfTheRoute);

        return new SingleRoute(edgesOfTheRoute);
    }
}