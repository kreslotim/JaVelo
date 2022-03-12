package ch.epfl.javelo.data;

import org.junit.jupiter.api.Test;
import org.w3c.dom.Attr;

import static ch.epfl.javelo.data.Attribute.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 *
 */
class AttributeSetTest {

    @Test
    void bits() {
        assertThrows(IllegalArgumentException.class, () -> {
            AttributeSet test = new AttributeSet(0b0111111111111111111111111111111111000000000000000000000000000000L);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            AttributeSet test = new AttributeSet(0b1111111111111111111111111111111111000000000000000000000000000000L);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            AttributeSet test = new AttributeSet(0b1011111111111111111111111111111111000000000000000000000000000000L);
        });
    }

    @Test
    void containsWorksOnKnownValue() {
        AttributeSet test = AttributeSet.of(TRACKTYPE_GRADE1, HIGHWAY_TRACK);
        assertEquals(true, test.contains(HIGHWAY_TRACK));
        assertEquals(false, test.contains(TRACKTYPE_GRADE3));
    }

    @Test
    void toStringWorksOnKnownValue() {
        AttributeSet set = AttributeSet.of(TRACKTYPE_GRADE1, HIGHWAY_TRACK);
        assertEquals("{highway=track,tracktype=grade1}", set.toString());
    }

    @Test
    void intersectWorksOnKnownValue() {
        AttributeSet set1 = AttributeSet.of(TRACKTYPE_GRADE1, HIGHWAY_TRACK);
        AttributeSet set2 = AttributeSet.of(TRACKTYPE_GRADE1, HIGHWAY_LIVING_STREET, HIGHWAY_TRUNK);
        AttributeSet set3 = AttributeSet.of(HIGHWAY_ROAD);
        assertEquals(true, set1.intersects(set2));
        assertEquals(false, set1.intersects(set3));
    }

    @Test
    void testConstructorFunction() {
        //should pass
        new AttributeSet(1000000000000000000L);
        AttributeSet.of(Attribute.LCN_YES);

        //binary representation length is 63, bits at index 62 is 0 -> it is okay
        new AttributeSet(0b001000000000000000000000100000000000000000001010000000000000000L);

        //binary representation length is 64, bits at index 62 and 63 are 0 -> it is okay
        new AttributeSet(0b0001000000000000000000000100000000000000000001010000000000000000L);

        //binary representation length is 62, bits at index 61 is 1 -> it is okay
        assertThrows(IllegalArgumentException.class, () -> {
            new AttributeSet(0b111000000000000000000000100000000000000000001010000000000000000L);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            //binary representation length is 64, bits at index 63 is 1 -> should fail
            new AttributeSet(0b1001000000000000000000000100000000000000000001010000000000000000L);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            //binary representation length is 63, bits at index 62 is 1 -> should fail.
            new AttributeSet(0b101000000000000000000000100000000000000000001010000000000000000L);
        });
    }

    @Test
    void testToStringFunction() {
        AttributeSet set = AttributeSet.of(Attribute.TRACKTYPE_GRADE1, Attribute.HIGHWAY_TRACK);
        assertEquals("{highway=track,tracktype=grade1}", set.toString());

        AttributeSet set2 = AttributeSet.of(Attribute.HIGHWAY_UNCLASSIFIED, Attribute.HIGHWAY_TRACK, Attribute.HIGHWAY_CYCLEWAY, Attribute.LCN_YES);
        assertEquals("{highway=track,highway=unclassified,highway=cycleway,lcn=yes}", set2.toString());

        AttributeSet set3 = AttributeSet.of(Attribute.MOTORROAD_YES, Attribute.TRACKTYPE_GRADE2, Attribute.RCN_YES, Attribute.ONEWAY_YES);
        assertEquals("{motorroad=yes,tracktype=grade2,oneway=yes,rcn=yes}", set3.toString());
    }

    @Test
    void testContainsFunction() {
        AttributeSet set = AttributeSet.of(Attribute.TRACKTYPE_GRADE2, Attribute.HIGHWAY_TRACK);
        //testing the before and after bit
        assertEquals(false, set.contains(Attribute.TRACKTYPE_GRADE1));
        assertEquals(false, set.contains(Attribute.TRACKTYPE_GRADE3));
        assertEquals(true, set.contains(Attribute.TRACKTYPE_GRADE2));

        //testing the before and after bit
        assertEquals(false, set.contains(Attribute.HIGHWAY_CYCLEWAY));
        assertEquals(false, set.contains(Attribute.HIGHWAY_SERVICE));
        assertEquals(true, set.contains(Attribute.HIGHWAY_TRACK));
    }

    @Test
    void testAttributes() {
        AttributeSet set = AttributeSet.of(TRACKTYPE_GRADE1, HIGHWAY_TRACK);
        assertEquals("{highway=track,tracktype=grade1}", set.toString());
    }

    @Test
    void testIntersectFunction() {
        AttributeSet set1 = AttributeSet.of(Attribute.TRACKTYPE_GRADE1, Attribute.HIGHWAY_TRACK);
        AttributeSet set2 = AttributeSet.of(Attribute.TRACKTYPE_GRADE2, Attribute.HIGHWAY_CYCLEWAY);
        AttributeSet set3 = AttributeSet.of(Attribute.TRACKTYPE_GRADE1, Attribute.HIGHWAY_CYCLEWAY);

        assertEquals(false, set1.intersects(set2));
        assertEquals(true, set2.intersects(set3));
        assertEquals(true, set1.intersects(set3));
    }

}