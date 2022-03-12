package ch.epfl.javelo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BitsTest {

    @Test
    void extractSignedOnAllowedValues(){
        var actual2 = Bits.extractSigned(744, 5, 4);
        var expected2 = 7;
        assertEquals(expected2, actual2);
    }

    @Test
    void extractSignedThrowsOnInvalidIndexes() {
        assertThrows(IllegalArgumentException.class, () -> {
            Bits.extractSigned(0b11001010111111101011101010111110, -1, 4);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            Bits.extractSigned(0b11001010111111101011101010111110, -10, -1);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            Bits.extractSigned(0b11001010111111101011101010111110, 32, 1);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            Bits.extractSigned(0b11001010111111101011101010111110, 31, 1);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            Bits.extractSigned(0b11001010111111101011101010111110, 8, -4); // must ask teacher!
        });
    }

    @Test
    void extractUnsignedThrowsOnInvalidIndexes() {
        assertThrows(IllegalArgumentException.class, () -> {
            Bits.extractSigned(0b11001010111111101011101010111110, -1, 4);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            Bits.extractSigned(0b11001010111111101011101010111110, -10, -1);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            Bits.extractSigned(0b11001010111111101011101010111110, 32, 1);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            Bits.extractSigned(0b11001010111111101011101010111110, 31, 1);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            Bits.extractSigned(0b11001010111111101011101010111110, 8, -4); // must ask teacher!
        });
    }

    @Test
    void extractSignedWorksOnKnownValue() {
        var actual1 = Bits.extractSigned(0b11001010111111101011101010111110, 8, 4);
        var expected1 = 0b11111111111111111111111111111010;
        assertEquals(expected1, actual1);

        var actual2 = Bits.extractSigned(744, 5, 4);
        var expected2 = 7;
        assertEquals(expected2, actual2);

        var actual3 = Bits.extractSigned(682, 4, 4);
        var expected3 = -6;
        assertEquals(expected3, actual3);

        var actual4 = Bits.extractSigned(-744, 5, 4);
        var expected4 = -8;
        assertEquals(expected4, actual4);
    }

    @Test
    void extractUnsigned() {
        var actual1 = Bits.extractUnsigned(0b11001010111111101011101010111110, 8, 4);
        var expected1 = 0b00000000000000000000000000001010;
        assertEquals(expected1, actual1);

        var actual2 = Bits.extractUnsigned(744, 5, 4);
        var expected2 = 7;
        assertEquals(expected2, actual2);

        var actual3 = Bits.extractUnsigned(682, 4, 4);
        var expected3 = 10;
        assertEquals(expected3, actual3);

        var actual4 = Bits.extractUnsigned(-889275714, 8, 4);
        var expected4 = 10;
        assertEquals(expected4, actual4);

        var actual5 = Bits.extractUnsigned(-744, 5, 4);
        var expected5 = 8;
        assertEquals(expected5, actual5);

    }
}