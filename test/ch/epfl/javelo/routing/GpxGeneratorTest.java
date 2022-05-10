package ch.epfl.javelo.routing;

import ch.epfl.javelo.data.Graph;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;

class GpxGeneratorTest {


    void writeGpx() throws IOException {
        Graph g2 = Graph.loadFrom(Path.of("ch_west"));
        CostFunction cf2 = new CityBikeCF(g2);
        RouteComputer rc2 = new RouteComputer(g2, cf2);
        Route r2 = rc2.bestRouteBetween(2046055, 2694240);
        ElevationProfile e = ElevationProfileComputer.elevationProfile(r2, 5);
        GpxGenerator.writeGpx("javelo.gpx", r2, e);
    }

    @Test
    void createGpxEPFLSauvabelin() throws IOException {
        Graph g2 = Graph.loadFrom(Path.of("lausanne"));
        CostFunction cf2 = new CityBikeCF(g2);
        RouteComputer rc2 = new RouteComputer(g2, cf2);
        Route r2 = rc2.bestRouteBetween(159049, 117669);
        ElevationProfile e = ElevationProfileComputer.elevationProfile(r2, 5);
        GpxGenerator.writeGpx("javeloEPFLSauvabelin.gpx", r2, e);
    }

}