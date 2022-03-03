package ch.epfl.javelo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Q28_4Test {
    public static final double DELTA = 1e-7;

    @Test
    void ofIntThrowsOnInvalidInteger() {
        assertThrows(IllegalArgumentException.class, () -> {
            Q28_4.ofInt(-134217729);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            Q28_4.ofInt(134217728);
        });
    }

    @Test
    void ofIntWorksOnKnownValues() {
        var actual1 = Q28_4.ofInt(0b10011100); // 156
        var expected1 = 0b100111000000; // 2496
        assertEquals(expected1, actual1);

        var actual2 = Q28_4.ofInt(134217727); // biggest Integer in Q28.4
        var expected2 = 0b01111111111111111111111111110000;
        assertEquals(expected2, actual2);

        var actual3 = Q28_4.ofInt(-134217727);
        var expected3 = 0b10000000000000000000000000010000;
        assertEquals(expected3, actual3);


        var actual4 = Q28_4.ofInt(-134217728); // smallest Integer in Q28.4
        var expected4 = Integer.MIN_VALUE;
        assertEquals(expected4, actual4);

    }

    @Test
    void asDoubleWorksOnKnownValue() {
        var actual1 = Q28_4.asDouble(1);
        var expected1 = 0.0625;
        assertEquals(expected1, actual1, DELTA);

        double actual2 = Q28_4.asDouble(0b01111111111111111111111111110000); // biggest Integer in Q28.4
        double expected2 = 134217727.0;
        assertEquals(expected2, actual2, DELTA);
    }

    @Test
    void asFloatWorksOnKnownValue() {
        var actual1 = Q28_4.asDouble(1);
        var expected1 = 0.0625;
        assertEquals(expected1, actual1, DELTA);
    }


}