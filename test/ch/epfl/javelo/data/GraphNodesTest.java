package ch.epfl.javelo.data;

import ch.epfl.javelo.projection.SwissBounds;
import org.junit.jupiter.api.Test;

import java.nio.IntBuffer;

import static org.junit.jupiter.api.Assertions.*;

public class GraphNodesTest {


    @Test
    void PreconditionTest() {
        // Only 0
        IntBuffer a = IntBuffer.wrap(new int[]{
                0_000_000 << 4,
                0_000_000 << 4,
                0000_0000_0000_0000
        });
        GraphNodes zeroValue = new GraphNodes(a);
        assertEquals(1, zeroValue.count());
        assertEquals(0, zeroValue.outDegree(0));
        //assertEquals(0,zeroValue.edgeId(0,0)); //TODO Ask assistant

        // Simply Null
        IntBuffer b = IntBuffer.wrap(new int[]{});
        GraphNodes ns = new GraphNodes(b);

        assertDoesNotThrow(() -> {
            ns.count();
        });
        assertThrows(IllegalArgumentException.class, () -> {
            ns.nodeE(0);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            ns.nodeN(0);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            ns.edgeId(0, 0);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            ns.outDegree(0);
        });

        //Out of Bound
        IntBuffer c = IntBuffer.wrap(new int[]{
                2_600_000 << 4,
                1_200_000 << 4,
                0x2_000_1234
        });


        GraphNodes outOfBound = new GraphNodes(c);
        assertThrows(IllegalArgumentException.class, () -> {
            outOfBound.nodeE(2);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            outOfBound.nodeE(-2);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            outOfBound.nodeN(2);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            outOfBound.nodeN(-2);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            outOfBound.outDegree(2);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            outOfBound.outDegree(-2);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            outOfBound.edgeId(2, 2);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            outOfBound.edgeId(-2, 2);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            outOfBound.edgeId(0, -2);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            outOfBound.edgeId(0, 3);
        });


        // Illegal Stream
        IntBuffer e = IntBuffer.wrap(new int[]{
                ((int) SwissBounds.MIN_E) << 4,
                ((int) SwissBounds.MIN_N) << 4,
                0x2_000_1234,
                ((int) SwissBounds.MIN_E) << 4,
                ((int) SwissBounds.MAX_N) << 4,
                0x2_000_1234,
                ((int) SwissBounds.MAX_E) << 4,
                ((int) SwissBounds.MIN_N) << 4,
                0x2_000_1234,
                ((int) SwissBounds.MAX_E) << 4,
                ((int) SwissBounds.MAX_N) << 4

        });
        assertThrows(IllegalArgumentException.class, () -> {
            GraphNodes illegalBuffer = new GraphNodes(e);
        });
        // Illegal Stream
        IntBuffer f = IntBuffer.wrap(new int[]{
                ((int) SwissBounds.MIN_E) << 4,
                ((int) SwissBounds.MIN_N) << 4,
                0x2_000_1234,
                ((int) SwissBounds.MIN_E) << 4,
                ((int) SwissBounds.MAX_N) << 4,
                0x2_000_1234,
                ((int) SwissBounds.MAX_E) << 4,
                ((int) SwissBounds.MIN_N) << 4,
                0x2_000_1234,
                ((int) SwissBounds.MAX_E) << 4,
                ((int) SwissBounds.MAX_N) << 4,
                0x2_000_1234,
                ((int) SwissBounds.MAX_N) << 4
        });
        assertThrows(IllegalArgumentException.class, () -> {
            GraphNodes illegalBuffer = new GraphNodes(f);
        });
    }

}
