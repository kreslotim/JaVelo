package ch.epfl.javelo;

import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;

public class Test {
    public static void main(String[] args) {
        PointCh p = new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N);
        System.out.println(p);
    }
}

class A {
    public static void aVoid() {
        System.out.println("f");
    }
}
