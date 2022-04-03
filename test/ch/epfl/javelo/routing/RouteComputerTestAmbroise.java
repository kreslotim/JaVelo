package ch.epfl.javelo.routing;

import ch.epfl.javelo.data.Graph;

import java.io.IOException;
import java.nio.LongBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class RouteComputerTestAmbroise {
    public static void main(String[] args) throws IOException {
        /*Path filePath = Path.of("lausanne/nodes_osmid.bin");
        LongBuffer osmIdBuffer=null;
        try (FileChannel channel = FileChannel.open(filePath)) {
            osmIdBuffer = channel
                    .map(FileChannel.MapMode.READ_ONLY, 0, channel.size())
                    .asLongBuffer();
        }
        catch (Exception e ){
            System.out.print("rip");
        }
        System.out.println(osmIdBuffer.get(50));
        System.out.println(osmIdBuffer.get(1020));

        Graph g = Graph.loadFrom(Path.of("lausanne"));
        CostFunction cf = new CityBikeCF(g);
        RouteComputer rc = new RouteComputer(g, cf);
        Route r = rc.bestRouteBetween(159049, 117669);
        KmlPrinter.write("javelo.kml", r);
        Route rN= rc.bestRouteBetween(0, 2);
        System.out.println(rN.edges());*/
        Path filePath = Path.of("ch_west/nodes_osmid.bin");
        LongBuffer osmIdBuffer=null;
        try (FileChannel channel = FileChannel.open(filePath)) {
            osmIdBuffer = channel
                    .map(FileChannel.MapMode.READ_ONLY, 0, channel.size())
                    .asLongBuffer();
        }
        catch (Exception e ){
            System.out.print("rip");
        }
        System.out.println(osmIdBuffer.get(50));
        System.out.println(osmIdBuffer.get(1020));

        Graph g = Graph.loadFrom(Path.of("ch_west"));
        CostFunction cf = new CityBikeCF(g);
        RouteComputer rc = new RouteComputer(g, cf);
        /*Route r = rc.bestRouteBetween(1, 2);
        assertEquals("route containing only the edge that goes from 1 to 2", r);*/

        /*int idEgeImpraticable =2795; //Highway
        System.out.println(g.edgeAttributes(idEgeImpraticable));
        Route r = rc.bestRouteBetween(1, g.edgeTargetNodeId(idEgeImpraticable));
        System.out.println(osmIdBuffer.get(g.edgeTargetNodeId(idEgeImpraticable)));
        assertEquals(null, r);*/


        int idEgeImpraticable =2805; //private ACCESS
        System.out.println(g.edgeAttributes(idEgeImpraticable));
        Route r = rc.bestRouteBetween(1, g.edgeTargetNodeId(idEgeImpraticable));
        System.out.println(osmIdBuffer.get(g.edgeTargetNodeId(idEgeImpraticable)));
        assertEquals(null, r);

        if(r!=null) {
            long t0 = System.nanoTime();
            KmlPrinter.write("javelo.kml", r);
            System.out.printf("Itinéraire calculé en %d ms\n",
                    (System.nanoTime() - t0) / 1_000_000);
        }
    }

}