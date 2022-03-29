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

        List<Edge> edgesOfTheRoute = new ArrayList<>();
        Set<Integer> Visiting = new HashSet<>();

        float[] distance = new float[graph.nodeCount()]; //TODO might add + 1 later
        int[] predecessor = new int[graph.nodeCount()]; //TODO might add + 1 later

        record WeightedNode(int nodeId, float distance)
                implements Comparable<WeightedNode> {
            @Override
            public int compareTo(WeightedNode that) {
                return Float.compare(this.distance, that.distance);
            }
        }


        int currentNode = 0; //index of the current Node
        int edge = 0; //index of the current Edge
        int Ntemp = 0; // index of a temporary Node
        float d = 0; // total distance through visited Nodes
        float minDistance = Integer.MAX_VALUE;
        float tempDistance;

        for (int N = 0; N < graph.nodeCount(); N++) {
            distance[N] = Float.POSITIVE_INFINITY;
            predecessor[N] = 0;
        }

        distance[startNodeId] = 0f;
        Visiting.add(startNodeId);

        while (!Visiting.isEmpty()) {
            for (int N : Visiting) {
                tempDistance = distance[N];
                if (tempDistance < minDistance) {
                    minDistance = tempDistance;
                    currentNode = N;
                }
             }
            int outGoingEdges = graph.nodeOutDegree(currentNode);
            Visiting.remove(currentNode); // for which distance[N] is minimal

            if (currentNode == endNodeId) break;

            for (int E = 0; E < graph.nodeOutDegree(currentNode); E++) {
                edge = graph.nodeOutEdgeId(currentNode,E); // considering current edge
                Ntemp = graph.edgeTargetNodeId(edge);

                d = distance[currentNode] + (float) graph.edgeLength(edge);

                if (d < distance[Ntemp]) {
                    distance[Ntemp] = d;
                    predecessor[Ntemp] = currentNode;
                    Visiting.add(Ntemp);
                }
            }
        }
        //TODO return null if the route does not exist.
        // Build the path here

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

        return new SingleRoute(edgesOfTheRoute);
    }
}
