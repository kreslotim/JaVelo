package ch.epfl.javelo.data;


import ch.epfl.javelo.Preconditions;

import java.util.StringJoiner;


/**
 *
 */
public record AttributeSet(long bits) {


    public AttributeSet { // compact constructor
        Preconditions.checkArgument(bits >> 62 == 0b0);
    }


    //methods

    /**
     * § 3.7.1
     *
     * @param attributes
     * @return a set containing only the attributes given as argument
     */
    public static AttributeSet of(Attribute... attributes) { // ... \equiv une listet statique de type Attribute
        long l = 0; // \equiv 64 zeros \equiv long 00000000...000 (64 0s)
        for (Attribute attribute : attributes) {
            l = (1L << attribute.ordinal()) | l; // la valeur de type long dont tous les bits valent 0 sauf celui dont l'index correspond à l'attribut
            // 1L \equiv 1 de type long \equiv 0000000...0001 avec 63 0s devant 1
        }
        return new AttributeSet(l);
    }

    // initialiser une var de type AttributeSet ?
    // on a long l = 0 = 00000000000...0000 (64 0s) inittialement
    // 1. long mask = 1L avec mask <- l
    // 2. decale 1 en utilisant  << attribute.ordinal()
    // 3. long l decale ok
    // 4. combine en utilisant | l
    // i.e. fais long mask = 1L << attribute.ordinal() | l ; 62 fois
    // 5. retourne un objet de type AttributSet : new AttributeSet(l) (instanciation car ces objets sont immuables)

    /**
     * @param attribute
     * @return true iff the receiver set (this, aka AttritbuteSet) contains the given attribute
     */
    public boolean contains(Attribute attribute) {
        // deja initialise bits
        long l = 1L << attribute.ordinal();
        return (l & bits) != 0L; // 0L = 0 de type long
    }
    // 1. on a deja initialise bits (dans of(...), en plus c'est l'attribut de la classe haha.)
    // 2. on fait le mask (\equiv l (notation)) comme dans la methode of(...)
    //ATTENTION : ce mask (ou bien l comme tu veux) ne contient qu'un seul "1" et swift en utilisant ordinal, les autres bits valent 0
    // 3. verifier si l != 0 avec l'operateur & pour comparer si la combinaison des deux(i.e. l et bits) longs ne vaut pas 0


// pour contains, il faut contenir un elem precis en commun; pour intersects, il faut juste verifier si un attribut en commun

    /**
     * @param that (type : AttributSet)
     * @return true iff the intersection of the receiver set (this type AttritbuteSet) with the one passed as argument (that type AttritbuteSet) is not equal to 0.
     */
    public boolean intersects(AttributeSet that) {
        return (that.bits & this.bits) != 0L;
        // that.bits syntaxe := o.attribut avec that = attributeSet en argument de la meth intersects
        // this.bits syntaxe := o.attribut avec this = attributeSet MOI de base
    }

    /**
     * @return a string consisting of the textual representation of the elements of the set enclosed in braces ({}) and separated by commas.
     */
    @Override
    public String toString() {
        StringJoiner j = new StringJoiner(",", "{", "}");
        for (Attribute attribute : Attribute.ALL) {
            if (this.contains(attribute))
                j.add(attribute.key() + "=" + attribute.value()); // must check if attributSet contains the i-th attribute. If this condition is true, than we add this key and the value of the attribute in the string with the syntaxe they ask us.
        }
        return j.toString(); // to return the string of an object, must use syntaxe : o.toString();
    }

}
