package ch.epfl.javelo.data;

import ch.epfl.javelo.projection.SwissBounds;
import org.junit.jupiter.api.Test;

import java.nio.IntBuffer;

import static org.junit.jupiter.api.Assertions.*;

class GraphNodesTest {

    @Test
    void NodesWorksOnKnownValues() {

        IntBuffer b = IntBuffer.wrap(new int[]{
                2_600_000 << 4,
                1_200_000 << 4,
                0x2_000_1234
        });
        GraphNodes ns = new GraphNodes(b);
        assertEquals(1, ns.count());
        assertEquals(2_600_000, ns.nodeE(0));
        assertEquals(1_200_000, ns.nodeN(0));
        assertEquals(2, ns.outDegree(0));
        assertEquals(0x1234, ns.edgeId(0, 0));
        assertEquals(0x1235, ns.edgeId(0, 1));
    }

    @Test
    void severalNodesTest(){
        IntBuffer b = IntBuffer.wrap(new int[]{
                2_600_000 << 4,
                1_200_000 << 4,
                0x2_000_1234,
                6_600_000 << 4,
                1_200_700 << 4,
                0b11110000000000000000000000000000,
                1_607_000 << 4,
                1_289_000 << 4,
                0b11001010111111101011101010111110
        });
        GraphNodes ns = new GraphNodes(b);
        assertEquals(3, ns.count());
        assertEquals(2_600_000, ns.nodeE(0));
        assertEquals(1_200_000, ns.nodeN(0));
        assertEquals(2, ns.outDegree(0));
        assertEquals(0x1234, ns.edgeId(0, 0));
        assertEquals(0x1235, ns.edgeId(0, 1));

        assertEquals(6_600_000, ns.nodeE(1));
        assertEquals(1_200_700, ns.nodeN(1));
        assertEquals(15, ns.outDegree(1));
        assertEquals(0, ns.edgeId(1, 0));
        assertEquals(0x0008, ns.edgeId(1, 8));

        assertEquals(1_607_000, ns.nodeE(2));
        assertEquals(1_289_000, ns.nodeN(2));
        assertEquals(12, ns.outDegree(2));
        assertEquals(0b00001010111111101011101010111110, ns.edgeId(2, 0));
        assertEquals(0b00001010111111101011101010111111, ns.edgeId(2, 1));
    }


    @Test
    void givenTestOnKnowValue(){
        IntBuffer b = IntBuffer.wrap(new int[]{
                2_600_000 << 4,
                1_200_000 << 4,
                0x2_000_1234
        });
        GraphNodes ns = new GraphNodes(b);
        assertEquals(1, ns.count());
        assertEquals(2_600_000, ns.nodeE(0));
        assertEquals(1_200_000, ns.nodeN(0));
        assertEquals(2, ns.outDegree(0));
        assertEquals(0x1234, ns.edgeId(0, 0));
        assertEquals(0x1235, ns.edgeId(0, 1));

    }

    @Test
    void OnMultipleKnowValueTest(){
        IntBuffer b = IntBuffer.wrap(new int[]{
                2_600_000 << 4,
                1_200_000 << 4,
                0x9_000_1234,

                ((int)SwissBounds.MIN_E +1)<< 4,
                ((int) SwissBounds.MAX_N - 1) << 4,
                0xf_000_1234,

                ((int)SwissBounds.MAX_E -1)<< 4,
                ((int) SwissBounds.MAX_N - 1) << 4,
                0x0_000_0000,

        });
        GraphNodes ns = new GraphNodes(b);
        assertEquals(3, ns.count());
        assertEquals(SwissBounds.MIN_E + 1, ns.nodeE(1));
        assertEquals(SwissBounds.MAX_E - 1, ns.nodeE(2));
        assertEquals(SwissBounds.MAX_N - 1, ns.nodeN(1));
        assertEquals(SwissBounds.MAX_N - 1, ns.nodeN(2));

        assertEquals(9, ns.outDegree(0));
        assertEquals(15, ns.outDegree(1));
        assertEquals(0, ns.outDegree(2));

        assertEquals(0x123c, ns.edgeId(0,8));
        assertEquals(0x1242, ns.edgeId(1,14));
    }



}