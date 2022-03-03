package ch.epfl.javelo.projection;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WebMercatorTest {
    public static final double DELTA = 1e-7;


    @Test
    void x() {
        var expected = 0.518275214444;
        var actual = WebMercator.x(Math.toRadians(6.5790772));
        assertEquals(expected, actual, DELTA);
    }

    @Test
    void y() {
        var expected = 0.353664894749;
        var actual = WebMercator.y(Math.toRadians(46.5218976));
        assertEquals(expected, actual, DELTA);
    }

    @Test
    void lon() {
        var expected = 6.5790772;
        var actual = Math.toDegrees(WebMercator.lon(0.518275214444));
        assertEquals(expected, actual, DELTA);
    }

    @Test
    void lat() {
        var expected = 46.5218976;
        var actual = Math.toDegrees(WebMercator.lat(0.353664894749));
        assertEquals(expected, actual, DELTA);
    }
}
