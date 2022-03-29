package ch.epfl.javelo;

import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;
import ch.epfl.javelo.routing.Edge;
import ch.epfl.javelo.routing.SingleRoute;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class Test {
    public static void main(String[] args) {
        List<Integer> list1 = new ArrayList<>(List.of(0,1));
        List<Integer> list2 = new ArrayList<>(List.of(2,3));
        List<Integer> list3 = new ArrayList<>(List.of(4,5));

        List<Integer> jointList = new ArrayList<>();
        Stream.of(list1, list2, list3).forEach(jointList::addAll);

        //for (int i : jointList) System.out.println(i);
    }
}

class A {



}
