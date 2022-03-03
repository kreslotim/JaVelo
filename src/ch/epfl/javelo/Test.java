package ch.epfl.javelo;

import ch.epfl.javelo.projection.SwissBounds;

public class Test {
    public static void main(String[] args) {
        System.out.println(A.ofInt(0b11111000000000000000000000000000));

    }
}

class A {

    public static int ofInt(int i) {
        // i must be bigger than or equal to :-134217728 and smaller than or equal to :134217727 // must check !
        Preconditions.checkArgument( i >= 0b11111000000000000000000000000000 && i <= 0b00000111111111111111111111111111);

        if (i < 0) return (i << 4) | 0b10000000000000000000000000000000;
        return i << 4;
    }
}

