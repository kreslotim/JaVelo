package ch.epfl.javelo.data;

import ch.epfl.javelo.Preconditions;

import java.util.StringJoiner;

/**
 * AttributeSet, a record class that provides a collection of OpenStreetMap attributes.
 *
 * @author Tim Kreslo (310686)
 * @author Wei-En Hsieh (341271)
 */
public record AttributeSet(long bits) {

    /**
     * Compact AttributeSet constructor
     *
     * @param bits represents the content of the set of values, using one bit per possible value
     * @throws IllegalArgumentException if the two bits at the end of the 64-bit (long) value carry some information
     */
    public AttributeSet {
        // The collection has 62 elements.
        // Therefore, there cannot exist any values, corresponding to the two bits at the end of the 64-bit (long) value
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
     * @param attribute a given attribute of Attribute
     * @return true iff the receiver set (this, which is AttributeSet) contains the given attribute
     */
    public boolean contains(Attribute attribute) {
        long l = 1L << attribute.ordinal();
        return (l & bits) != 0L;
    }


    /**
     * Checks if the intersection of the set of attributes in AttributeSet (this),
     * with another AttributeSet (that) is not empty
     *
     * @param that (type : AttributSet)
     * @return true iff the intersection of the receiver set (this, of type AttritbuteSet),
     * with the one passed as argument (that, of type AttritbuteSet) is not equal to 0.
     */
    public boolean intersects(AttributeSet that) {
        return (that.bits & this.bits) != 0L;
    }

    /**
     * Returns a string that consists of the textual representation of the elements of the set,
     * such that the elements appear in the order in which they are declared in the Attribute enumerated type,
     * enclosed in braces ({}) and separated by commas.
     *
     * @return a string that consists of the textual representation of the elements of the set,
     * enclosed in braces ({}) and separated by commas.
     */
    @Override
    public String toString() {
        StringJoiner j = new StringJoiner(",", "{", "}");
        for (Attribute attribute : Attribute.ALL) {
            // check if attributeSet contains the i-th attribute :
            if (this.contains(attribute))
                // if this condition is true, then we add this key and the value of the attribute in the string
                j.add(attribute.key() + "=" + attribute.value());
        }
        return j.toString();
    }
}