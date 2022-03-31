package ch.epfl.javelo;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.routing.*;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.PriorityQueue;

public final class Stage6Test {
    public static void main(String[] args) throws IOException {
        Graph g = Graph.loadFrom(Path.of("lausanne"));
        //Graph g = Graph.loadFrom(Path.of("ch_west"));
        CostFunction cf = new CityBikeCF(g);
        RouteComputer rc = new RouteComputer(g, cf);
        long t0 = System.nanoTime();
        Route r = rc.bestRouteBetween(159049, 117669);
        //Route r = rc.bestRouteBetween(2046055, 2694240);
        KmlPrinter.write("javeloTim.kml", r);
        System.out.printf("Itinéraire calculé en %d ms\n",
                (System.nanoTime() - t0) / 1_000_000);
    }
}
