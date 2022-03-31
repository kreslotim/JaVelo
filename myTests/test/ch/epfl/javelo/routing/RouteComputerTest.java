package ch.epfl.javelo.routing;

import ch.epfl.javelo.Q28_4;
import ch.epfl.javelo.data.*;
import ch.epfl.javelo.projection.PointCh;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.function.DoubleUnaryOperator;

import static ch.epfl.test.TestRandomizer.RANDOM_ITERATIONS;
import static ch.epfl.test.TestRandomizer.newRandom;
import static org.junit.jupiter.api.Assertions.*;

class RouteComputerTest {

    public static final Graph GRAPH = graphBuilder();

    private static Graph graphBuilder() {
        ByteBuffer sectorsBuffer = ByteBuffer.wrap(new byte[]{0, 1, 2, 3});
        GraphSectors graphSectors = new GraphSectors(sectorsBuffer);

        // d from origin multiplied by 100
        PointCh A = new PointCh(2600000, 1200000); // origin
        PointCh B = new PointCh(2600000 + 7400, 1200000 + 7400); // far away
        PointCh C = new PointCh(2600000 + 347, 1200000 + 200);
        PointCh D = new PointCh(2600000 + 200, 1200000 - 14);
        PointCh E = new PointCh(2600000 + 100, 1200000 + 100);
        PointCh F = new PointCh(2600000 + 550, 1200000 + 240);
        PointCh G = new PointCh(2600000 + 490, 1200000 - 100);
        PointCh H = new PointCh(2600000 + 630, 1200000 + 306);

        IntBuffer nodesBuffer = IntBuffer.wrap(new int[]{Q28_4.ofInt(2600000), Q28_4.ofInt(1200000),
                Q28_4.ofInt(2600000 + 7400), Q28_4.ofInt(1200000 + 7400),
                Q28_4.ofInt(2600000 + 347), Q28_4.ofInt(1200000 + 200),
                Q28_4.ofInt(2600000 + 200), Q28_4.ofInt(1200000 - 14),
                Q28_4.ofInt(2600000 + 100), Q28_4.ofInt(1200000 + 100),
                Q28_4.ofInt(2600000 + 550), Q28_4.ofInt(1200000 + 240),
                Q28_4.ofInt(2600000 + 490), Q28_4.ofInt(1200000 - 100),
                Q28_4.ofInt(2600000 + 630), Q28_4.ofInt(1200000 + 306)});
        GraphNodes graphNodes = new GraphNodes(nodesBuffer);


        ByteBuffer edgesBuffer = ByteBuffer.allocate(10);
        edgesBuffer.putInt(0, ~12); // Sens : inverse. Node destination : 12.
        edgesBuffer.putShort(4, (short) 0x10_b); // Longueur : 0x10.b m (= 16.6875 m)
        edgesBuffer.putShort(6, (short) 0x10_0); // Dénivelé : 0x10.0 m (= 16.0 m)
        edgesBuffer.putShort(8, (short) 2022); // Identité de l'ensemble d'attributs OSM : 2022
        IntBuffer profileIds = IntBuffer.wrap(new int[]{(3 << 30) | 1}); // Type : 3. Index du premier échantillon : 1.
        ShortBuffer elevations = ShortBuffer.wrap(new short[]{(short) 0, (short) 0x180C, (short) 0xFEFF, (short) 0xFFFE, (short) 0xF000});
        GraphEdges edges = new GraphEdges(edgesBuffer, profileIds, elevations);
        List<AttributeSet> attributeSets = new ArrayList<>();
        attributeSets.add(new AttributeSet(1L));
        return new Graph(graphNodes, graphSectors, edges, attributeSets);
    }

    @Test
    void BestRouteBetweenWorksOnKnownValues() {
        PointCh A = new PointCh(2600000, 1200000); // origin
        PointCh B = new PointCh(2600000 + 7400, 1200000 + 7400); // far away
        PointCh C = new PointCh(2600000 + 347, 1200000 + 200);
        PointCh D = new PointCh(2600000 + 200, 1200000 - 14);
        PointCh E = new PointCh(2600000 + 100, 1200000 + 100);
        PointCh F = new PointCh(2600000 + 550, 1200000 + 240);
        PointCh G = new PointCh(2600000 + 490, 1200000 - 100);
        PointCh H = new PointCh(2600000 + 630, 1200000 + 306);

        Edge edgeAC = new Edge(0, 2, A, C, 4, DoubleUnaryOperator.identity());
        Edge edgeAD = new Edge(0, 3, A, B, 2, DoubleUnaryOperator.identity());
        Edge edgeCB = new Edge(2, 1, C, B, 10952, DoubleUnaryOperator.identity());
        Edge edgeCE = new Edge(2, 4, C, E, 1, DoubleUnaryOperator.identity());
        Edge edgeCF = new Edge(2, 5, C, F, 3, DoubleUnaryOperator.identity());
        Edge edgeDA = new Edge(3, 0, B, A, 2, DoubleUnaryOperator.identity());
        Edge edgeDE = new Edge(3, 5, D, E, 1, DoubleUnaryOperator.identity());
        Edge edgeDG = new Edge(3, 6, D, G, 3, DoubleUnaryOperator.identity());
        Edge edgeEH = new Edge(4, 7, E, H, 2, DoubleUnaryOperator.identity());
        Edge edgeFC = new Edge(5, 2, F, C, 3, DoubleUnaryOperator.identity());
        Edge edgeGD = new Edge(6, 3, G, D, 3, DoubleUnaryOperator.identity());
        Edge edgeGH = new Edge(6, 7, G, H, 2, DoubleUnaryOperator.identity());
        Edge edgeHE = new Edge(7, 4, H, E, 2, DoubleUnaryOperator.identity());
        Edge edgeHF = new Edge(7, 5, H, F, 1, DoubleUnaryOperator.identity()); // 14 edges

        List<Edge> edges = new ArrayList<>(List.of(edgeAC, edgeAD, edgeCB, edgeCE, edgeCF, edgeDA, edgeDE, edgeDG, edgeEH, edgeFC, edgeGD, edgeGH, edgeHE, edgeHF));
        Route singleRoute = new SingleRoute(edges);

        int nodeA = 0; // Node A
        int nodeF = 5; // Node F

        CityBikeCF costFunction = new CityBikeCF(GRAPH);
        RouteComputer routeComputer = new RouteComputer(GRAPH, costFunction);

        assertEquals(singleRoute, routeComputer.bestRouteBetween(nodeA, nodeF));
    }



    @Test
    void PriorityQueue() {
        PriorityQueue<Integer> p = new PriorityQueue<>();
        p.addAll(List.of(5, 2, 17, 29, 33, 1, 8));
        System.out.println(p);
        assertEquals(1, p.remove());
        assertEquals(2, p.remove());
        assertEquals(5, p.remove());
    }
}