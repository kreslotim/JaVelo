package ch.epfl.javelo.data;

import ch.epfl.javelo.Preconditions;
import java.util.StringJoiner;

/**
 * Recorded class providing a collection of OpenStreetMap attributes
 *
 * @author Tim Kreslo (310686)
 * @author Wei-En Hsieh (341271)
 */
public record AttributeSet(long bits) {

    /**
     * Compact AttributeSet constructor
     *
     * @param bits represents content of a set of values, using one bit per possible value
     * @throws IllegalArgumentException if last 2 bits carry some information
     */
    public AttributeSet {
        // The collection has 62 elements.
        // Therefore, there cannot exist any values, corresponding to the last 2 bits
        Preconditions.checkArgument(bits >> 62 == 0b0);
    }


    /**
     * Returns a set of elements, given as argument
     *
     * @param attributes a list of attributes, from Attribute
     * @return a set of elements containing only the attributes given as argument
     */
    public static AttributeSet of(Attribute... attributes) {
        long l = 0; // 64 zeros
        for (Attribute attribute : attributes) {
            l = (1L << attribute.ordinal()) | l;
        }
        return new AttributeSet(l);
    }


    /**
     * Checks if AttributeSet (this) contains the attribute given as argument
     *
     * @param attribute from Attribute
     * @return true iff the receiver set (this, aka AttributeSet) contains the given attribute
     */
    public boolean contains(Attribute attribute) {
        long l = 1L << attribute.ordinal();
        return (l & bits) != 0L;
    }


    /**
     * Checks if the intersection of the set of attributes in AttributeSet (this),
     * with an extern AttributeSet (that) is not empty
     *
     * @param that (type : AttributSet)
     * @return true iff the intersection of the receiver set (this type AttritbuteSet),
     * with the one passed as argument (that type AttritbuteSet) is not equal to 0.
     */
    public boolean intersects(AttributeSet that) {
        return (that.bits & this.bits) != 0L;
    }

    /**
     * @return a string consisting of the textual representation,
     * of the elements of the set enclosed in braces ({}) and separated by commas.
     */
    @Override
    public String toString() {
        StringJoiner j = new StringJoiner(",", "{", "}");
        for (Attribute attribute : Attribute.ALL) {
            if (this.contains(attribute))
                // must check if attributeSet contains the i-th attribute.
                // If this condition is true, than we add this key and the value of the attribute in the string,
                // with the syntax they ask us.
                j.add(attribute.key() + "=" + attribute.value());
        }
        return j.toString();
    }

}
