package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;
import org.junit.jupiter.api.Test;

import java.util.List;

import static ch.epfl.test.TestRandomizer.RANDOM_ITERATIONS;
import static ch.epfl.test.TestRandomizer.newRandom;
import static org.junit.jupiter.api.Assertions.*;

class ElevationProfileComputerTest {


    @Test
    void testingNaNCases(){
        var rng = newRandom();


        //Only NaN
        PointCh A0 = new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N + 1000);
        PointCh B0 = new PointCh(SwissBounds.MIN_E + 20, SwissBounds.MIN_N + 1000);

        Edge edge0 = new Edge(0, 1, A0, B0, 20, (x)->Double.NaN);

        Route route0 = new SingleRoute(List.of(edge0));
        ElevationProfile test0 = ElevationProfileComputer.elevationProfile(route0, 1);

        for(int i=0; i < RANDOM_ITERATIONS; i++){
            assertEquals(0, test0.elevationAt(rng.nextDouble(0, 20)));
        }

        //[NaN, num, NaN]
        PointCh A1 = new PointCh(SwissBounds.MIN_E + 00, SwissBounds.MIN_N + 1000);
        PointCh B1 = new PointCh(SwissBounds.MIN_E + 20, SwissBounds.MIN_N + 1000);
        PointCh C1 = new PointCh(SwissBounds.MIN_E + 40, SwissBounds.MIN_N + 1000);
        PointCh D1 = new PointCh(SwissBounds.MIN_E + 60, SwissBounds.MIN_N + 1000);

        Edge edge10 = new Edge(0, 1, A1, B1, 20, (x)->Double.NaN);
        Edge edge11 = new Edge(0, 1, B1, C1, 20, (x)->100);
        Edge edge12 = new Edge(0, 1, C1, D1, 20, (x)->Double.NaN);

        Route route1 = new SingleRoute(List.of(edge10, edge11, edge12));
        ElevationProfile test1 = ElevationProfileComputer.elevationProfile(route1, 1);

        for(int i=0; i < RANDOM_ITERATIONS; i++){
            double randPos = rng.nextDouble(0, 60);
            assertEquals(100, test1.elevationAt(randPos));
        }

        //[n0, NaN, n0] where n0 is a real positive number
        PointCh A2 = new PointCh(SwissBounds.MIN_E + 00, SwissBounds.MIN_N + 1000);
        PointCh B2 = new PointCh(SwissBounds.MIN_E + 20, SwissBounds.MIN_N + 1000);
        PointCh C2 = new PointCh(SwissBounds.MIN_E + 40, SwissBounds.MIN_N + 1000);
        PointCh D2 = new PointCh(SwissBounds.MIN_E + 60, SwissBounds.MIN_N + 1000);

        Edge edge20 = new Edge(0, 1, A2, B2, 20, (x)->100);
        Edge edge21 = new Edge(0, 1, B2, C2, 20, (x)->Double.NaN);
        Edge edge22 = new Edge(0, 1, C2, D2, 20, (x)->100);

        Route route2 = new SingleRoute(List.of(edge20, edge21, edge22));
        ElevationProfile test2 = ElevationProfileComputer.elevationProfile(route2, 1);

        for(int i=0; i < RANDOM_ITERATIONS; i++){
            double randPos = rng.nextDouble(0, 60);
            assertEquals(100, test2.elevationAt(randPos));
        }



        //[n0, NaN, n1] where n0, n1 are distinct real positive numbers
        PointCh A3 = new PointCh(SwissBounds.MIN_E + 00, SwissBounds.MIN_N + 1000);
        PointCh B3 = new PointCh(SwissBounds.MIN_E + 40, SwissBounds.MIN_N + 1000);
        PointCh C3 = new PointCh(SwissBounds.MIN_E + 60, SwissBounds.MIN_N + 1000);
        PointCh D3 = new PointCh(SwissBounds.MIN_E + 100, SwissBounds.MIN_N + 1000);

        Edge edge30 = new Edge(0, 1, A3, B3, 40, (x)->100);
        Edge edge31 = new Edge(0, 1, B3, C3, 20, (x)->Double.NaN);
        Edge edge32 = new Edge(0, 1, C3, D3, 40, (x)->200);

        Route route3 = new SingleRoute(List.of(edge30, edge31, edge32));
        ElevationProfile test3 = ElevationProfileComputer.elevationProfile(route3, 1);

        for(int i=0; i < RANDOM_ITERATIONS; i++){
            double randPos = rng.nextDouble(0, 100);
            if(randPos >= 60 && randPos < 100)
                assertEquals(200, test3.elevationAt(randPos));
            else if (randPos > 0 && randPos <= 40)
                assertEquals(100, test3.elevationAt(randPos));
            else
                assertEquals(5*randPos-100 , test3.elevationAt(randPos), 0.0001);
        }
    }
}