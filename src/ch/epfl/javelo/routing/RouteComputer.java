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
     * @param graph the whole JaVelo graph
     * @param costFunction cost factor, for bike Routes.
     */
    public RouteComputer(Graph graph, CostFunction costFunction) {
        this.graph = graph;
        this.costFunction = costFunction;
    }


    /**
     * Returns the minimum total cost route from startNodeId to endNodeId,
     * in the graph passed to the constructor, or null if no route exists.
     * If the start and end node are the same, throws IllegalArgumentException.
     *
     * @param startNodeId starting Node identity
     * @param endNodeId ending Node identity
     * @return the ideal route between two given nodes
     */
    public Route bestRouteBetween(int startNodeId, int endNodeId) {
        Preconditions.checkArgument(startNodeId != endNodeId);

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

        int currentNode = 0; //index of the current Node
        int edge = 0; //index of the current Edge
        int Ntemp = 0; // index of a temporary Node
        double totalDistance = 0; // total distance through visited Nodes

        Arrays.fill(distance, 0, distance.length, Float.POSITIVE_INFINITY);
        Arrays.fill(predecessor, 0, predecessor.length, 0);

        distance[startNodeId] = 0f;
        Visiting.add(new WeightedNode(startNodeId, distance[startNodeId]));


        while (!Visiting.isEmpty()) { // do currentNode = Visiting.remove while !Visiting.isEmpty() && distance[currentNode] == Float.NEGATIVE_INFINITY
            currentNode = Visiting.remove().nodeId; // for which distance[N] is minimal

            if (currentNode == endNodeId) return computeRoute(startNodeId, endNodeId, predecessor); //TODO give predecessor ?

            if (distance[currentNode] != Float.NEGATIVE_INFINITY) {
                for (int E = 0; E < graph.nodeOutDegree(currentNode); E++) {
                    edge = graph.nodeOutEdgeId(currentNode, E); // considering current edge
                    Ntemp = graph.edgeTargetNodeId(edge);

                    totalDistance = distance[currentNode] + costFunction.costFactor(currentNode, edge) * graph.edgeLength(edge);

                    if (totalDistance < distance[Ntemp]) {
                        distance[Ntemp] = (float) totalDistance;
                        predecessor[Ntemp] = currentNode;
                        Visiting.add(new WeightedNode(Ntemp, distance[Ntemp]));
                    }
                }
                distance[currentNode] = Float.NEGATIVE_INFINITY; // TODO where to implement Negative Infinity ?
            }
        }
        return null;
    }



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

        Collections.reverse(edgesOfTheRoute);

        SingleRoute finalRoute = new SingleRoute(edgesOfTheRoute);
        System.out.println("Route's length: "+finalRoute.length());
        return finalRoute;
    }
}