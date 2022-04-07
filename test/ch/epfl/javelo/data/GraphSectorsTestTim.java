package ch.epfl.javelo.data;

import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class GraphSectorsTestTim {


    @Test
    void GraphSectorWorksNormally() {
        byte[] bufferId = new byte[128 * 128 * 6];
        for (int i = 0; i < bufferId.length; ++i) {
            bufferId[i] = (byte) i;
        }

        ByteBuffer buffer = ByteBuffer.wrap(bufferId);
        PointCh point = new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N);
        GraphSectors sectors1 = new GraphSectors(buffer);
        List<GraphSectors.Sector> expected1 = new ArrayList<>();
        expected1.add(new GraphSectors.Sector(buffer.getInt(0), buffer.getShort(4) + buffer.getInt(0)));
        assertArrayEquals(expected1.toArray(), sectors1.sectorsInArea(point, 1).toArray());

        List<GraphSectors.Sector> expected2 = new ArrayList<>();
        GraphSectors sectors2 = new GraphSectors(buffer);
        expected2.add(new GraphSectors.Sector(buffer.getInt(0), buffer.getShort(4) + buffer.getInt(0)));
        expected2.add(new GraphSectors.Sector(buffer.getInt(6), buffer.getShort(10) + buffer.getInt(6)));
        expected2.add(new GraphSectors.Sector(buffer.getInt(128 * 6), buffer.getShort(128 * 6 + 4) + buffer.getInt(128 * 6)));
        expected2.add(new GraphSectors.Sector(buffer.getInt(129 * 6), buffer.getShort(129 * 6 + 4) + buffer.getInt(129 * 6)));
        assertArrayEquals(expected2.toArray(), sectors2.sectorsInArea(point, 2800).toArray());

    }


    @Test
    void SectorWorkOnMultipleKnowValues() {
        ByteBuffer a = ByteBuffer.allocate(128 * 128 * 6);
        for (int i = 0; i < 1600; i++) {
            a.putInt(i * 6, i);
            a.putShort(i * 6 + 4, (short) i);
        }
        GraphSectors ns = new GraphSectors(a);
        List<GraphSectors.Sector> n = ns.sectorsInArea(new PointCh((SwissBounds.MIN_E + SwissBounds.MAX_E) / 2, (SwissBounds.MIN_N + SwissBounds.MAX_N) / 2), 1);
        assertEquals(4, n.size());

    }


    @Test
    void workOnMultipleKnowValuesSingleNode() {
        ByteBuffer b = ByteBuffer.allocate(16384 * 6);     // Numbers of sectors
        for (int i = 0; i < 16384; i++) {
            b.putInt(i * 6, i);               // ID of Node
            b.putShort(i * 6 + 4, (short) 1);  // Numbers of Node in this sectors
        }
        GraphSectors ns2 = new GraphSectors(b);
        double avgE = (SwissBounds.MIN_E + SwissBounds.MAX_E) / 2;
        double avgN = (SwissBounds.MIN_N + SwissBounds.MAX_N) / 2;
        double offsetE = (SwissBounds.MAX_E - SwissBounds.MIN_E) / 256; // given half the length of a rectangle
        double offsetN = (SwissBounds.MAX_N - SwissBounds.MIN_N) / 256;
        // avgE = k * offsetE*2 => Intersection  => k = 128/2 for middle of the map
        if (128 / 2 * offsetE * 2 == avgE) System.out.println("true");
        if (128 / 2 * offsetN * 2 == avgN) System.out.println("true");
        ArrayList<GraphSectors.Sector> test = new ArrayList<>();
        test.add(new GraphSectors.Sector(128 * 64 + 64, 128 * 64 + 65));

        double xIn = 64 + 0.5;
        double yIn = 64 + 0.5;

        assertEquals(15, ns2.sectorsInArea(new PointCh((SwissBounds.MIN_E + ((349_000 / 128) * xIn)), (SwissBounds.MIN_N + ((221_000 / 128) * yIn))), ((349_000 / 128) * 1)).size());

        assertEquals(1, ns2.sectorsInArea(new PointCh(avgE + offsetE, avgN + offsetN), 1).size());
        assertEquals(test, ns2.sectorsInArea(new PointCh(avgE + offsetE, avgN + offsetN), 1));
        assertEquals(4, ns2.sectorsInArea(new PointCh(avgE, avgN), 1).size());   // 0 doesn't really matter just between [0,4]
        assertEquals(9, ns2.sectorsInArea(new PointCh(SwissBounds.MIN_E + 11 * offsetE, SwissBounds.MIN_N + 11 * offsetN), (2799 / 2)).size()); // This should Work
        assertEquals(15, ns2.sectorsInArea(new PointCh(avgE + offsetE, avgN + offsetN), (2727)).size());
        assertEquals(16384, ns2.sectorsInArea(new PointCh(avgE + offsetE, avgN + offsetN), (Integer.MAX_VALUE)).size());
        assertEquals(16383, ns2.sectorsInArea(new PointCh(avgE + offsetE, avgN + offsetN), (Integer.MAX_VALUE)).get(16383).startNodeId());
    }

    @Test
    void workOnMultipleKnowValuesSequenceOfNodes() {
        ByteBuffer b = ByteBuffer.allocate(16384 * 6);     // Numbers of sectors
        int counter = 1;
        for (int i = 0; i < 16384; i++) {
            if (counter == 5) counter = 1;
            b.putInt(i * 6, i * i);                     // ID of Node
            b.putShort(i * 6 + 4, (short) (i + 1));  // Numbers of Node in this sectors, using Un+1 = Un + n + 1
            counter++;

        }
        GraphSectors ns2 = new GraphSectors(b);

        double avgE = (SwissBounds.MIN_E + SwissBounds.MAX_E) / 2;
        double avgN = (SwissBounds.MIN_N + SwissBounds.MAX_N) / 2;
        double offsetE = (SwissBounds.MAX_E - SwissBounds.MIN_E) / 256; // given half the length of a rectangle
        double offsetN = (SwissBounds.MAX_N - SwissBounds.MIN_N) / 256;

        ArrayList<GraphSectors.Sector> test = new ArrayList<>();
        test.add(new GraphSectors.Sector((128 * 64 + 64) * (128 * 64 + 64), (128 * 64 + 64) * (128 * 64 + 64) + (128 * 64 + 64) + 1));
        assertEquals(test, ns2.sectorsInArea(new PointCh(avgE + offsetE, avgN + offsetN), 1));

        /*  Math are currently innacurate
        test.add(new GraphSectors.Sector((128 * 63 + 63)*(128 * 63 + 63)+(128 * 64 + 64), (128 * 63 + 63)*(128 * 63 + 63) +(128 * 63 + 63)+1+(128 * 63 + 63)));
        test.add(new GraphSectors.Sector((128 * 65 + 65)*(128 * 65 + 65)-(128 * 65 + 65), (128 * 65 + 65)*(128 * 65 + 65) +(128 * 65 + 65)+1-(128 * 65 + 65)));
        assertEquals(test, ns2.sectorsInArea(new PointCh(avgE + offsetE, avgN + offsetN), 1740 / 2));
        */
        // Ina
        assertEquals(9, ns2.sectorsInArea(new PointCh(SwissBounds.MIN_E + 11 * offsetE, SwissBounds.MIN_N + 11 * offsetN), (2799 / 2)).size()); // This should Work
        assertEquals(15, ns2.sectorsInArea(new PointCh(avgE + offsetE, avgN + offsetN), (2727)).size());
        assertEquals(16384, ns2.sectorsInArea(new PointCh(avgE + offsetE, avgN + offsetN), (Integer.MAX_VALUE)).size());

    }


    @Test
    void workOnLimitsCasesValue() {

        // Triple Sectors MIN, MIN
        ByteBuffer a = ByteBuffer.allocate(6 * 16384);    // Numbers of sectors
        a.putInt(0, 0);                   // ID of Node
        a.putShort(4, (short) 1);              // Numbers of Node in this sectors
        a.putInt(6 * 1, 1);
        a.putShort(6 * 1 + 4, (short) 1);
        a.putInt(6 * 2, 2);
        a.putShort(6 * 2 + 4, (short) 1);
        a.putInt(6 * 3, 3);
        a.putShort(6 * 3 + 4, (short) 1);
        a.putInt(6 * 128, 2);
        a.putShort(6 * 128 + 4, (short) 1);
        a.putInt(6 * 129, 3);
        a.putShort(6 * 129 + 4, (short) 1);

        GraphSectors ns = new GraphSectors(a);
        assertEquals(1, ns.sectorsInArea(new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), 1).size());
        assertEquals(4, ns.sectorsInArea(new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), 2830).size());

        ArrayList<GraphSectors.Sector> test = new ArrayList<>();
        test.add(new GraphSectors.Sector(0, 1));
        test.add(new GraphSectors.Sector(1, 2));
        test.add(new GraphSectors.Sector(2, 3));
        test.add(new GraphSectors.Sector(3, 4));

        assertEquals(test.get(0), ns.sectorsInArea(new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), 1).get(0));
        assertEquals(test, ns.sectorsInArea(new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), 2730));


        // Multiple Sectors MAX, MAX
        ByteBuffer c = ByteBuffer.allocate(16384 * 6);     // Numbers of sectors
        for (int i = 0; i < 16384; i++) {
            c.putInt(i * 6, i * 3);               // ID of Node
            c.putShort(i * 6 + 4, (short) 3);  // Numbers of Node in this sectors
        }


        GraphSectors ns3 = new GraphSectors(c);
        //assertEquals(3, ns3.sectorsInArea(new PointCh(SwissBounds.MAX_E, SwissBounds.MAX_N), 1).size()); // Unsure
        //assertEquals(9 + 3, ns3.sectorsInArea(new PointCh(SwissBounds.MAX_E, SwissBounds.MAX_N), 2730).size()); // Unsure
        //assertEquals(15 + 9 + 3, ns3.sectorsInArea(new PointCh(SwissBounds.MAX_E, SwissBounds.MAX_N), 2730 * 2).size()); // Unsure

        ArrayList<GraphSectors.Sector> expected = new ArrayList<>();

        expected.add(new GraphSectors.Sector(16382 * 3, 16382 * 3 + 3));
        expected.add(new GraphSectors.Sector(16383 * 3, 16383 * 3 + 3));
        expected.add(new GraphSectors.Sector(16384 * 3, 16384 * 3));
    }

    @Test
    public void sectorsInAreaTest() {

        //Initialization of ByteBuffer.
        ByteBuffer b = ByteBuffer.allocate(98304);
        for (int i = 0; i < 16384; ++i) {
            b.putInt(i);
            b.putShort((short) 0);
        }
        GraphSectors gs = new GraphSectors(b);
        // Every sector's first node's value is the index of the sector in the buffer of sectors. Every sector contains exactly 0 nodes.


        //First test: we want to get only the sector #8256, located in the middle.
        //For that we use the fact that the equality between record objects was modified so that each attribute gets compared instead of the references.
        assertEquals(new GraphSectors.Sector(8256, 8256), gs.sectorsInArea(new PointCh(SwissBounds.MIN_E + 64 * (2.7265625 * 1000),
                SwissBounds.MIN_N + 64 * (1.7265625 * 1000)), 0).get(0));


        //Second test: we want to get only the sector in the top right corner (last one according to its index).
        assertEquals(new GraphSectors.Sector(16383, 16383), gs.sectorsInArea(new PointCh(SwissBounds.MIN_E + 127.5 * (2.7265625 * 1000),
                SwissBounds.MIN_N + 127.5 * (1.7265625 * 1000)), 0).get(0));


        //Third test: multiple sectors in the area.
        assertEquals(new GraphSectors.Sector(8127, 8127), gs.sectorsInArea(new PointCh(SwissBounds.MIN_E + 64 * (2.7265625 * 1000),
                SwissBounds.MIN_N + 64 * (1.7265625 * 1000)), 300).get(0));
        assertEquals(new GraphSectors.Sector(8128, 8128), gs.sectorsInArea(new PointCh(SwissBounds.MIN_E + 64 * (2.7265625 * 1000),
                SwissBounds.MIN_N + 64 * (1.7265625 * 1000)), 300).get(1));
        assertEquals(new GraphSectors.Sector(8255, 8255), gs.sectorsInArea(new PointCh(SwissBounds.MIN_E + 64 * (2.7265625 * 1000),
                SwissBounds.MIN_N + 64 * (1.7265625 * 1000)), 300).get(2));
        assertEquals(new GraphSectors.Sector(8256, 8256), gs.sectorsInArea(new PointCh(SwissBounds.MIN_E + 64 * (2.7265625 * 1000),
                SwissBounds.MIN_N + 64 * (1.7265625 * 1000)), 300).get(3));


        //Fourth test: what if the drawn square gets passed the borders defined by the class Swissbound ?
        //If no error is thrown while using an absurdly high distance value (1000000000 meters), this may indicate that you treat this case appropriately.
        gs.sectorsInArea(new PointCh(SwissBounds.MIN_E + 64 * (2.7265625 * 1000), SwissBounds.MIN_N + 64 * (1.7265625 * 1000)), 1000000000);
        // We draw a square from the bottom left corner of the grid. We are supposed to list the sectors 0, 1, 128 and 129 without errors.
        assertEquals(new GraphSectors.Sector(0, 0), gs.sectorsInArea(new PointCh(SwissBounds.MIN_E + 0.1 * (2.7265625 * 1000),
                SwissBounds.MIN_N + 0.1 * (1.7265625 * 1000)), 3000).get(0));
        assertEquals(new GraphSectors.Sector(1, 1), gs.sectorsInArea(new PointCh(SwissBounds.MIN_E + 0.1 * (2.7265625 * 1000),
                SwissBounds.MIN_N + 0.1 * (1.7265625 * 1000)), 3000).get(1));
        assertEquals(new GraphSectors.Sector(128, 128), gs.sectorsInArea(new PointCh(SwissBounds.MIN_E + 0.1 * (2.7265625 * 1000),
                SwissBounds.MIN_N + 0.1 * (1.7265625 * 1000)), 3000).get(2));
        assertEquals(new GraphSectors.Sector(129, 129), gs.sectorsInArea(new PointCh(SwissBounds.MIN_E + 0.1 * (2.7265625 * 1000),
                SwissBounds.MIN_N + 0.1 * (1.7265625 * 1000)), 3000).get(3));

        // Hope you found this test useful ! - Léo.


        /* Additional test that is not based on the instructions. I wanted to verify that we could not input a negative value as a distance.
        assertThrows(AssertionError.class, () -> {
            gs.sectorsInArea(new PointCh(SwissBounds.MIN_E,SwissBounds.MIN_N),-1);
        });
         */
    }


}