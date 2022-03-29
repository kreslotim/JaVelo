package ch.epfl.javelo.data;


import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import static org.junit.jupiter.api.Assertions.*;

class GraphEdgesTest {

    @Test
    void EdgesWorksOnKnownValues() {
        ByteBuffer edgesBuffer = ByteBuffer.allocate(10);
// Sens : inversé. Nœud destination : 12.
        edgesBuffer.putInt(0, ~12);
// Longueur : 0x10.b m (= 16.6875 m)
        edgesBuffer.putShort(4, (short) 0x10_b);
// Dénivelé : 0x10.0 m (= 16.0 m)
        edgesBuffer.putShort(6, (short) 0x10_0);
// Identité de l'ensemble d'attributs OSM : 2022
        edgesBuffer.putShort(8, (short) 2022);

        IntBuffer profileIds = IntBuffer.wrap(new int[]{
                // Type : 3. Index du premier échantillon : 1.
                (3 << 30) | 1
        });

        ShortBuffer elevations = ShortBuffer.wrap(new short[]{
                (short) 0,
                (short) 0x180C, (short) 0xFEFF,
                (short) 0xFFFE, (short) 0xF000
        });

        GraphEdges edges =
                new GraphEdges(edgesBuffer, profileIds, elevations);

        assertTrue(edges.isInverted(0));
        assertEquals(12, edges.targetNodeId(0));
        assertEquals(16.6875, edges.length(0));
        assertEquals(16.0, edges.elevationGain(0));
        assertEquals(2022, edges.attributesIndex(0));
        float[] expectedSamples = new float[]{
                384.0625f, 384.125f, 384.25f, 384.3125f, 384.375f,
                384.4375f, 384.5f, 384.5625f, 384.6875f, 384.75f
        };
        assertArrayEquals(expectedSamples, edges.profileSamples(0));
    }

    
    @Test
    void testType1Et2Louis() {
        /**
         * Created a specific test with correct values for a type 0, 1 and 2 profile.
         * It also tests if the program takes the correct values in edgesBuffer.
         * This does not test limit cases, but checks the overall functionality of the method.
         */

        ByteBuffer edgesBuffer = ByteBuffer.allocate(30);
        // Profil 1 :
        // Sens : Normal. Nœud destination : 52.
        edgesBuffer.putInt(0, 0b110101);
        // Longueur : 4.0m
        edgesBuffer.putShort(4, (short)0x04_0);
        // Dénivelé : -0.25m
        edgesBuffer.putShort(6, (short)0b1111111111111100);
        //Indentité de l'ensemble d'attributs OSM : ptdrrrr jsp j'ai mis au pif
        edgesBuffer.putShort(8, (short)2102);

        //Profil 2 :
        edgesBuffer.putInt(10, 0b01100);
// Longueur : 0x12.b m (= 17.6875 m)
        edgesBuffer.putShort(14, (short) 0x11_b);
// Dénivelé : 0x10.0 m (= 26.75 m)
        edgesBuffer.putShort(16, (short) 0x1A_c);
// Identité de l'ensemble d'attributs OSM : tkt
        edgesBuffer.putShort(18, (short) 30921);
        for (int i = 0; i < 5; i++) {
            edgesBuffer.putShort(i+20, (short)0b0);
        }

        IntBuffer profileIds = IntBuffer.wrap(new int[]{
                // Type : 1 | Index du premier echantillon : 0
                (1 << 30 ),
                // Type : 2. Index du premier échantillon : 4.
                (2 << 30) | 4,
                // Type : 0.
                0
        });

        ShortBuffer elevations = ShortBuffer.wrap(new short[]{
                (short)0x180C, (short)0x181C, (short)0x180D,
                (short) 1102.23f,
                (short) 0x180C, (short) 0xBE0F,
                (short) 0x2E20, (short) 0xFFEE,
                (short) 0x2020, (short) 0x1000
        });

        GraphEdges edges =
                new GraphEdges(edgesBuffer, profileIds, elevations);

        assertFalse(edges.isInverted(1));
        assertEquals(12, edges.targetNodeId(1));
        assertEquals(17.6875, edges.length(1));
        assertEquals(26.75, edges.elevationGain(1));
        assertEquals(30921, edges.attributesIndex(1));
        float[] expectedSamplesType2 = new float[]{
                384.75f, 380.625f, 381.5625f, 384.4375f, 386.4375f, 386.375f,
                385.25f, 387.25f, 389.25f, 390.25f,
        };
        assertFalse(edges.isInverted(0));
        assertEquals(53, edges.targetNodeId(0));
        assertEquals(4.0, edges.length(0));
        //assertEquals(-4.0/16, edges.elevationGain(0)); // may not be negative
        assertEquals(2102, edges.attributesIndex(0));
        float[] expectedSamplesType1 = new float []{
                384.75f, 385.75f, 384.8125f
        };
        assertArrayEquals(expectedSamplesType1, edges.profileSamples(0));
        assertArrayEquals(expectedSamplesType2, edges.profileSamples(1));
        assertArrayEquals(new float[]{}, edges.profileSamples(2));
    }
}