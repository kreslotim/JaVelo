package ch.epfl.javelo.projection;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PointWebMercatorTest {
    public static final double DELTA = 2;

    @Test
    void ofWorksOnKnownValues() {
        var actual1 = PointWebMercator.of(19,0.518275214444, 0.353664894749).x();
        var expected1 = 3.861451256603003E-9;
        assertEquals(expected1, actual1);

        var actual2 = PointWebMercator.of(19,0.518275214444, 0.353664894749).y();
        var expected2 = 2.635008802630007E-9;
        assertEquals(expected2, actual2);

    }

    @Test
    void pointWebMercatorOfHanaAndLuna() {
        var p = new PointWebMercator(3.662109375E-5, 6.103515625E-5);
        assertEquals(p, PointWebMercator.of(5, 0.3, 0.5));
    }


    @Test
    void ofPointChTest(){
        PointCh a = new PointCh(2695000,1175000);
        assertTrue(new PointWebMercator((WebMercator.x(Ch1903.lon(2695000, 1175000))), WebMercator.y(Ch1903.lat(2695000, 1175000))).equals(PointWebMercator.ofPointCh(a)));
    }


    @Test
    void xAtZoomLevelWorksOnKnownValues() {
        PointWebMercator test = new PointWebMercator(0.518275214444, 0.353664894749);
        var actual1 = test.xAtZoomLevel(19);
        var expected1 = 69561722;
        assertEquals(expected1, actual1, DELTA);
    }

    @Test
    void yAtZoomLevelWorksOnKnownValues() {
        PointWebMercator test = new PointWebMercator(0.518275214444, 0.353664894749);
        var actual1 = test.yAtZoomLevel(19);
        var expected1 = 47468099;
        assertEquals(expected1, actual1, DELTA);
    }

    @Test
    void lonWorksOnKnownValues() {
        PointWebMercator test = new PointWebMercator(0.518275214444, 0.353664894749);
        var actual1 = test.lon();
        var expected1 = Math.toRadians(6.5790772);
        assertEquals(expected1, actual1, DELTA);
    }

    @Test
    void latWorksOnKnownValues() {
        PointWebMercator test = new PointWebMercator(0.518275214444, 0.353664894749);
        var actual1 = test.lat();
        var expected1 = Math.toRadians(46.5218976);
        assertEquals(expected1, actual1, DELTA);
    }


    @Test
    void cannotCreateWithoutGoodCoordinates() {
        assertThrows(IllegalArgumentException.class, () -> {
            PointWebMercator lolilol = new PointWebMercator(2, 4);
        });
    }


    // x_{19} = 69561722, y_{19} = 47468099
    @Test
    void xAtZoomLevel() {
        PointWebMercator test = new PointWebMercator(0.518275214444,0.353664894749);
        var expected = 69561722;
        var actual = Math.round(test.xAtZoomLevel(19));
        assertEquals(expected, actual, DELTA);
    }

    /**
    @Test
    void pointChVsPointWebMercatorOwnTransformTest(){
        // Accuracy test who will always fail.
        assertEquals(new PointCh(2600000,1200000), PointWebMercator.ofPointCh(new PointCh(2600000,1200000)).toPointCh());
    }
     */

    @Test
    void toPointCh() {
        PointWebMercator test = new PointWebMercator(0.518275214444,0.353664894749);
        PointCh converted = test.toPointCh();
        PointCh expected = new PointCh(Ch1903.e((test.lon()), (test.lat())), Ch1903.n((test.lon()), (test.lat())));

        assertEquals(expected, converted);
    }
}