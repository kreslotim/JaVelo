package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.Math2;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.function.DoubleUnaryOperator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SingleRouteTestAmbroise {


    //Ambroise

    @Test
    void indexOfSegmentAt() {
        assertThrows(IllegalArgumentException.class, () ->{
            SingleRoute single = new SingleRoute(new ArrayList<>());
        });
    }

    @Test
    void length() {
        float[] yeet = {30f, 31f, 30.5f, 34f, 29f, 30f, 35f, 29.5f, 33f, 31f,60f};
        PointCh fromPoint= new PointCh(SwissBounds.MIN_E+500, SwissBounds.MIN_N+200);
        PointCh toPoint= new PointCh(SwissBounds.MIN_E+510, SwissBounds.MIN_N+205);
        double length = Math2.norm((toPoint.n()-fromPoint.n()),(toPoint.e()-fromPoint.e()));
        DoubleUnaryOperator yo = Functions.sampled(yeet,length);
        Edge edge1 = new Edge(0,50,fromPoint,toPoint, length, yo);

        float[] yeet2 = {28f, 30f, 30.5f, 34f, 26f, 30f, 37f, 29.5f, 32f, 31f,80f};
        PointCh fromPoint2= new PointCh(SwissBounds.MIN_E+510, SwissBounds.MIN_N+205);
        PointCh toPoint2= new PointCh(SwissBounds.MIN_E+522, SwissBounds.MIN_N+211);
        double length2 = Math2.norm((toPoint2.n()-fromPoint2.n()),(toPoint2.e()-fromPoint2.e()));
        DoubleUnaryOperator yo2 = Functions.sampled(yeet2,length2);
        Edge edge2 = new Edge(51,62,fromPoint2,toPoint2, length2, yo2);

        ArrayList<Edge> list = new ArrayList<>();
        list.add(edge1);
        list.add(edge2);
        SingleRoute single = new SingleRoute(list);
        assertEquals(length+length2, single.length(), 10E-2);
    }

    @Test
    void edges() {
        float[] yeet = {30f, 31f, 30.5f, 34f, 29f, 30f, 35f, 29.5f, 33f, 31f,60f};
        PointCh fromPoint= new PointCh(SwissBounds.MIN_E+500, SwissBounds.MIN_N+200);
        PointCh toPoint= new PointCh(SwissBounds.MIN_E+510, SwissBounds.MIN_N+205);
        double length = Math2.norm((toPoint.n()-fromPoint.n()),(toPoint.e()-fromPoint.e()));
        DoubleUnaryOperator yo = Functions.sampled(yeet,length);
        Edge edge1 = new Edge(0,50,fromPoint,toPoint, length, yo);

        float[] yeet2 = {28f, 30f, 30.5f, 34f, 26f, 30f, 37f, 29.5f, 32f, 31f,80f};
        PointCh fromPoint2= new PointCh(SwissBounds.MIN_E+510, SwissBounds.MIN_N+205);
        PointCh toPoint2= new PointCh(SwissBounds.MIN_E+522, SwissBounds.MIN_N+211);
        double length2 = Math2.norm((toPoint2.n()-fromPoint2.n()),(toPoint2.e()-fromPoint2.e()));
        DoubleUnaryOperator yo2 = Functions.sampled(yeet2,length2);
        Edge edge2 = new Edge(51,62,fromPoint2,toPoint2, length2, yo2);

        ArrayList<Edge> list = new ArrayList<>();
        list.add(edge1);
        list.add(edge2);
        SingleRoute single = new SingleRoute(list);
        assertEquals(list, single.edges());
        assertThrows(UnsupportedOperationException.class, () ->{
            single.edges().add(edge1);
            int b=0;
        });
    }

    @Test
    void points() {
        float[] yeet = {30f, 31f, 30.5f, 34f, 29f, 30f, 35f, 29.5f, 33f, 31f,60f};
        PointCh fromPoint= new PointCh(SwissBounds.MIN_E+500, SwissBounds.MIN_N+200);
        PointCh toPoint= new PointCh(SwissBounds.MIN_E+510, SwissBounds.MIN_N+205);
        double length = Math2.norm((toPoint.n()-fromPoint.n()),(toPoint.e()-fromPoint.e()));
        DoubleUnaryOperator yo = Functions.sampled(yeet,length);
        Edge edge1 = new Edge(0,50,fromPoint,toPoint, length, yo);

        float[] yeet2 = {28f, 30f, 30.5f, 34f, 26f, 30f, 37f, 29.5f, 32f, 31f,80f};
        PointCh fromPoint2= new PointCh(SwissBounds.MIN_E+510, SwissBounds.MIN_N+205);
        PointCh toPoint2= new PointCh(SwissBounds.MIN_E+522, SwissBounds.MIN_N+211);
        double length2 = Math2.norm((toPoint2.n()-fromPoint2.n()),(toPoint2.e()-fromPoint2.e()));
        DoubleUnaryOperator yo2 = Functions.sampled(yeet2,length2);
        Edge edge2 = new Edge(51,62,fromPoint2,toPoint2, length2, yo2);

        ArrayList<PointCh> points = new ArrayList<>();
        points.add(fromPoint);
        points.add(fromPoint2);
        points.add(toPoint2);

        ArrayList<Edge> list = new ArrayList<>();
        list.add(edge1);
        list.add(edge2);
        SingleRoute single = new SingleRoute(list);

        assertEquals(points, single.points());
        /*assertThrows(UnsupportedOperationException.class, () ->{
            List<PointCh> ps = single.points();
        });*/

    }

    @Test
    void pointAt() {
        float[] yeet = {30f, 31f, 30.5f, 34f, 29f, 30f, 35f, 29.5f, 33f, 31f,60f};
        PointCh fromPoint= new PointCh(SwissBounds.MIN_E+500, SwissBounds.MIN_N+200);
        PointCh toPoint= new PointCh(SwissBounds.MIN_E+510, SwissBounds.MIN_N+205);
        double length = Math2.norm((toPoint.n()-fromPoint.n()),(toPoint.e()-fromPoint.e()));
        DoubleUnaryOperator yo = Functions.sampled(yeet,length);
        Edge edge1 = new Edge(0,50,fromPoint,toPoint, length, yo);

        float[] yeet2 = {28f, 30f, 30.5f, 34f, 26f, 30f, 37f, 29.5f, 32f, 31f,80f};
        PointCh fromPoint2= new PointCh(SwissBounds.MIN_E+510, SwissBounds.MIN_N+205);
        PointCh toPoint2= new PointCh(SwissBounds.MIN_E+522, SwissBounds.MIN_N+211);
        double length2 = Math2.norm((toPoint2.n()-fromPoint2.n()),(toPoint2.e()-fromPoint2.e()));
        DoubleUnaryOperator yo2 = Functions.sampled(yeet2,length2);
        Edge edge2 = new Edge(51,62,fromPoint2,toPoint2, length2, yo2);


        ArrayList<Edge> list = new ArrayList<>();
        list.add(edge1);
        list.add(edge2);
        SingleRoute single = new SingleRoute(list);

        assertEquals(edge1.pointAt(5.5), single.pointAt(5.5));
        assertEquals(edge2.pointAt(2), single.pointAt(length+2));
        assertEquals(edge2.pointAt(0), single.pointAt(length));
        assertEquals(edge2.pointAt(length2), single.pointAt(length+length2));
        assertEquals(edge1.pointAt(0), single.pointAt(0));
        assertEquals(edge1.pointAt(length), single.pointAt(length));
        //assertEquals(fromPoint, single.pointAt(-10));
        single.pointAt(length+length2+1);
        assertEquals(edge2.pointAt(length2), single.pointAt(length*40));
        assertEquals(edge1.pointAt(0), single.pointAt(-10));
    }

    @Test
    void nodeClosestTo() {
        float[] yeet = {30f, 31f, 30.5f, 34f, 29f, 30f, 35f, 29.5f, 33f, 31f,60f};
        PointCh fromPoint= new PointCh(SwissBounds.MIN_E+500, SwissBounds.MIN_N+200);
        PointCh toPoint= new PointCh(SwissBounds.MIN_E+510, SwissBounds.MIN_N+205);
        double length = Math2.norm((toPoint.n()-fromPoint.n()),(toPoint.e()-fromPoint.e()));
        DoubleUnaryOperator yo = Functions.sampled(yeet,length);
        Edge edge1 = new Edge(0,50,fromPoint,toPoint, length, yo);

        float[] yeet2 = {28f, 30f, 30.5f, 34f, 26f, 30f, 37f, 29.5f, 32f, 31f,80f};
        PointCh fromPoint2= new PointCh(SwissBounds.MIN_E+510, SwissBounds.MIN_N+205);
        PointCh toPoint2= new PointCh(SwissBounds.MIN_E+522, SwissBounds.MIN_N+211);
        double length2 = Math2.norm((toPoint2.n()-fromPoint2.n()),(toPoint2.e()-fromPoint2.e()));
        DoubleUnaryOperator yo2 = Functions.sampled(yeet2,length2);
        Edge edge2 = new Edge(50,62,fromPoint2,toPoint2, length2, yo2);


        ArrayList<Edge> list = new ArrayList<>();
        list.add(edge1);
        list.add(edge2);
        SingleRoute single = new SingleRoute(list);

        assertEquals(0, single.nodeClosestTo(1.5));
        assertEquals(50, single.nodeClosestTo(length));
        assertEquals(50, single.nodeClosestTo(length-1));
        assertEquals(50, single.nodeClosestTo(length+1));
        assertEquals(62, single.nodeClosestTo(length+length2-1));
        assertEquals(0, single.nodeClosestTo(-10));
        assertEquals(62, single.nodeClosestTo(5000));
        assertEquals(0, single.nodeClosestTo(length/2)); //TODO 0 chez Ambroise
        single.nodeClosestTo(length+length2);
        assertEquals(62, single.nodeClosestTo(length+length2));
        assertEquals(0, single.nodeClosestTo(-100));
        //single.nodeClosestTo(length+length2);
        assertEquals(62, single.nodeClosestTo(length+length2+6));
    }

    @Test
    void elevationAt() {

        float[] yeet = {30f, 31f, 30.5f, 34f, 29f, 30f, 35f, 29.5f, 33f, 31f,60f};
        PointCh fromPoint= new PointCh(SwissBounds.MIN_E+500, SwissBounds.MIN_N+200);
        PointCh toPoint= new PointCh(SwissBounds.MIN_E+510, SwissBounds.MIN_N+205);
        double length = Math2.norm((toPoint.n()-fromPoint.n()),(toPoint.e()-fromPoint.e()));
        DoubleUnaryOperator yo = Functions.sampled(yeet,length);
        Edge edge1 = new Edge(0,50,fromPoint,toPoint, length, yo);

        float[] yeet2 = {28f, 30f, 30.5f, 34f, 26f, 30f, 37f, 29.5f, 32f, 31f,80f};
        PointCh fromPoint2= new PointCh(SwissBounds.MIN_E+510, SwissBounds.MIN_N+205);
        PointCh toPoint2= new PointCh(SwissBounds.MIN_E+522, SwissBounds.MIN_N+211);
        double length2 = Math2.norm((toPoint2.n()-fromPoint2.n()),(toPoint2.e()-fromPoint2.e()));
        DoubleUnaryOperator yo2 = Functions.sampled(yeet2,length2);
        Edge edge2 = new Edge(50,62,fromPoint2,toPoint2, length2, yo2);


        ArrayList<Edge> list = new ArrayList<>();
        list.add(edge1);
        list.add(edge2);
        SingleRoute single = new SingleRoute(list);

        assertEquals(edge1.elevationAt(1), single.elevationAt(1));
        assertEquals(edge1.elevationAt(length-1), single.elevationAt(length-1));
        assertEquals(edge2.elevationAt(1), single.elevationAt(length+1));
        assertEquals(edge2.elevationAt(0), single.elevationAt(length));
        assertEquals(edge2.elevationAt(length2-1), single.elevationAt(length+length2-1));
        assertEquals(edge1.elevationAt(-1), single.elevationAt(-10));
        assertEquals(edge2.elevationAt(60000), single.elevationAt(5000));
        assertEquals(edge1.elevationAt(length/2), single.elevationAt(length/2));
        assertEquals(30f, single.elevationAt(0));
        single.elevationAt(length+length2);
        single.elevationAt(length+length2);
        assertEquals(80f, single.elevationAt(length+length2));
        assertEquals(80f, single.elevationAt(length+length2+6));
        assertEquals(30f, single.elevationAt(-100));
    }

}
