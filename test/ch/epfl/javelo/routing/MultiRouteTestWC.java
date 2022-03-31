package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.projection.PointCh;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

import static org.junit.jupiter.api.Assertions.*;
/**
 * @author Clement Josso (341733)
 * @author William Jallot (341540)
 */
class MultiRouteTestWC {
    PointCh point1 = new PointCh(2595000,1200000);
    PointCh point2 = new PointCh(2600000,1204000);
    PointCh point3 = new PointCh(2603000,1202000);
    PointCh point4 = new PointCh(2605000,1203000);
    PointCh point5 = new PointCh(2609000,1199000);
    PointCh point6 = new PointCh(2609500, 1198500);

    float[] samples1 = new float[]{200, 240, 230, 360,380};
    DoubleUnaryOperator profile1 = Functions.sampled(samples1, point1.distanceTo(point2) );
    Edge edge1 = new Edge(0, 3, point1, point2, point1.distanceTo(point2), profile1);
    float[] samples2 = new float[]{380,360, 340, 350,320,350};
    DoubleUnaryOperator profile2 = Functions.sampled(samples2, point2.distanceTo(point3));
    Edge edge2 = new Edge(3, 8,point2 ,point3,point2.distanceTo(point3), profile2);
    float[] samples3 = new float[]{350, 320, 300, 280,270,250};
    DoubleUnaryOperator profile3 = Functions.sampled(samples3,point3.distanceTo(point4));
    Edge edge3 = new Edge(8,10,point3, point4,point3.distanceTo(point4),profile3);
    float[] samples4 = new float[]{250, 300, 330, 360,380,400};
    DoubleUnaryOperator profile4 = Functions.sampled(samples4,point4.distanceTo(point5));
    Edge edge4 = new Edge(10, 18,point4 ,point5, point4.distanceTo(point5),profile4);
    // Edge edge5 = new Edge(18, 5,point5 ,point6, point5.distanceTo(point6),x ->Float.NaN);

    float[] samplesReverse4 = new float[]{400, 380, 360, 330,300,250};
    DoubleUnaryOperator profileReverse4 = Functions.sampled(samplesReverse4,point4.distanceTo(point5));
    Edge edgeReverse4 = new Edge(18, 10,point5 ,point4, point4.distanceTo(point5),profileReverse4);
    float[] samplesReverse3 = new float[]{250, 270, 280, 300,320,350};
    DoubleUnaryOperator profileReverse3 = Functions.sampled(samplesReverse3,point3.distanceTo(point4));
    Edge edgeReverse3 = new Edge(10,8,point4, point3,point3.distanceTo(point4),profileReverse3);
    float[] samplesReverse2 = new float[]{350,320, 350, 340, 360,380};
    DoubleUnaryOperator profileReverse2 = Functions.sampled(samplesReverse2, point2.distanceTo(point3));
    Edge edgeReverse2 = new Edge(8, 3,point3 ,point2,point2.distanceTo(point3), profileReverse2);
    float[] samplesReverse1 = new float[]{380, 360, 230, 240,200};
    DoubleUnaryOperator profileReverse1 = Functions.sampled(samplesReverse1, point1.distanceTo(point2) );
    Edge edgeReverse1 = new Edge(3, 0, point2, point1, point1.distanceTo(point2), profileReverse1);

    List<Edge> edges1 = new ArrayList<>();
    List<Edge> edges2 = new ArrayList<>();
    List<Edge> edges3 = new ArrayList<>();
    List<Edge> edges4 = new ArrayList<>();

    @Test
    public void indexOfSegmentAtNestedMultiRoutes(){
        edges1.add(edge1);
        edges1.add(edge2);
        SingleRoute route1 = new SingleRoute(edges1);

        edges2.add(edge3);
        edges2.add(edge4);
        SingleRoute route2 = new SingleRoute(edges2);

        edges3.add(edgeReverse4);
        edges3.add(edgeReverse3);
        SingleRoute route3 = new SingleRoute(edges3);

        edges4.add(edgeReverse2);
        edges4.add(edgeReverse1);
        SingleRoute route4 = new SingleRoute(edges4);


        List<Route> routes1 = new ArrayList<>();
        routes1.add(route1);
        routes1.add(route2);
        List<Route> routes2 = new ArrayList<>();
        routes2.add(route3);
        routes2.add(route4);

        MultiRoute multiRoute1 = new MultiRoute(routes1);
        MultiRoute multiRoute2 = new MultiRoute(routes2);

        List<Route> multiRoutes = new ArrayList<>();
        multiRoutes.add(multiRoute1);
        multiRoutes.add(multiRoute2);

        MultiRoute multiRouteSupreme = new MultiRoute(multiRoutes);
        //La méthode fonctionne-t-elle pour les cas limites ?
        assertEquals(3,multiRouteSupreme.indexOfSegmentAt(multiRouteSupreme.length()));
        assertEquals(0,multiRouteSupreme.indexOfSegmentAt(0));
        assertEquals(3,multiRouteSupreme.indexOfSegmentAt(30795));
        //Fonctionne-elle pour des valeurs pratiques ?
        assertEquals(0,multiRouteSupreme.indexOfSegmentAt(5000));
        assertEquals(2,multiRouteSupreme.indexOfSegmentAt(20902));
        //Voyons si vous avez ramené la position entre 0 et la longueur totale.
        assertEquals(0,multiRouteSupreme.indexOfSegmentAt(-4));
        assertEquals(3,multiRouteSupreme.indexOfSegmentAt(multiRouteSupreme.length()+42));

    }
    @Test
    public void indexOfSegmentAtOnlySingleRoutes(){
        edges1.add(edge1);
        edges1.add(edge2);
        SingleRoute route1 = new SingleRoute(edges1);

        edges2.add(edge3);
        edges2.add(edge4);
        SingleRoute route2 = new SingleRoute(edges2);

        edges3.add(edgeReverse4);
        edges3.add(edgeReverse3);
        SingleRoute route3 = new SingleRoute(edges3);
        List<Route> routes = new ArrayList<>();
        routes.add(route1);
        routes.add(route2);
        routes.add(route3);

        MultiRoute bRoute = new MultiRoute(routes);

        assertEquals(1, bRoute.indexOfSegmentAt(13465));
        assertEquals(0, bRoute.indexOfSegmentAt(0));
        assertEquals(2, bRoute.indexOfSegmentAt(21402));
        assertEquals(2, bRoute.indexOfSegmentAt(bRoute.length()));
    }
    @Test
    public void indexOfSegmentAtInceptionMultiRoutes(){
        edges1.add(edge1);
        edges1.add(edge2);
        SingleRoute route1 = new SingleRoute(edges1);

        edges2.add(edge3);
        edges2.add(edge4);
        SingleRoute route2 = new SingleRoute(edges2);

        edges3.add(edgeReverse4);
        edges3.add(edgeReverse3);
        SingleRoute route3 = new SingleRoute(edges3);

        edges4.add(edgeReverse2);
        edges4.add(edgeReverse1);
        SingleRoute route4 = new SingleRoute(edges4);

        List<Route> routes1 = new ArrayList<>();

        routes1.add(route1);
        routes1.add(route2);

        List<Route> routes2 = new ArrayList<>();

        routes2.add(route3);
        routes2.add(route4);

        MultiRoute lucidRoute1 = new MultiRoute(routes1);
        MultiRoute lucidRoute2 = new MultiRoute(routes2);

        List<Route> multiRoutes = new ArrayList<>();
        multiRoutes.add(lucidRoute1);
        multiRoutes.add(lucidRoute2);

        MultiRoute paradoxicalSleep = new MultiRoute(multiRoutes);

        List<Route> multisRoutes = new ArrayList<>();
        multisRoutes.add(paradoxicalSleep);

        MultiRoute inceptionRoute = new MultiRoute(multisRoutes);

        assertEquals(0,inceptionRoute.indexOfSegmentAt(0));
        assertEquals(3,inceptionRoute.indexOfSegmentAt(30795));
        assertEquals(3,inceptionRoute.indexOfSegmentAt(inceptionRoute.length()));
        assertEquals(0,inceptionRoute.indexOfSegmentAt(5000));
        assertEquals(0,inceptionRoute.indexOfSegmentAt(-4));
        assertEquals(3,inceptionRoute.indexOfSegmentAt(inceptionRoute.length()+42));
        assertEquals(2,inceptionRoute.indexOfSegmentAt(20902));
    }
    @Test
    public void indexOfSegmentAtMixAndTwist(){
        edges1.add(edge1);
        edges1.add(edge2);
        SingleRoute route1 = new SingleRoute(edges1);

        edges2.add(edge3);
        edges2.add(edge4);
        SingleRoute route2 = new SingleRoute(edges2);

        edges3.add(edgeReverse4);
        edges3.add(edgeReverse3);
        SingleRoute route3 = new SingleRoute(edges3);

        edges4.add(edgeReverse2);
        edges4.add(edgeReverse1);
        SingleRoute route4 = new SingleRoute(edges4);

        List<Route> routes1 = new ArrayList<>();

        routes1.add(route1);
        routes1.add(route2);

        MultiRoute mixRoute = new MultiRoute(routes1);

        List<Route> routes2 = new ArrayList<>();
        routes2.add(mixRoute);
        routes2.add(route3);
        routes2.add(route4);

        MultiRoute twistRoute = new MultiRoute(routes2);

        assertEquals(0,twistRoute.indexOfSegmentAt(0));
        assertEquals(3,twistRoute.indexOfSegmentAt(30795));
        assertEquals(3,twistRoute.indexOfSegmentAt(twistRoute.length()));
        assertEquals(0,twistRoute.indexOfSegmentAt(5000));
        assertEquals(0,twistRoute.indexOfSegmentAt(-4));
        assertEquals(3,twistRoute.indexOfSegmentAt(twistRoute.length()+42));
        assertEquals(2,twistRoute.indexOfSegmentAt(20902));
    }
    @Test
    public void lengthTest(){
        edges1.add(edge1);
        edges1.add(edge2);
        SingleRoute route1 = new SingleRoute(edges1);

        edges2.add(edge3);
        edges2.add(edge4);
        SingleRoute route2 = new SingleRoute(edges2);

        edges3.add(edgeReverse4);
        edges3.add(edgeReverse3);
        SingleRoute route3 = new SingleRoute(edges3);

        edges4.add(edgeReverse2);
        edges4.add(edgeReverse1);
        SingleRoute route4 = new SingleRoute(edges4);


        List<Route> routes1 = new ArrayList<>();
        routes1.add(route1);
        routes1.add(route2);
        List<Route> routes2 = new ArrayList<>();
        routes2.add(route3);
        routes2.add(route4);

        MultiRoute multiRoute1 = new MultiRoute(routes1);
        MultiRoute multiRoute2 = new MultiRoute(routes2);

        List<Route> multiRoutes = new ArrayList<>();
        multiRoutes.add(multiRoute1);
        multiRoutes.add(multiRoute2);

        MultiRoute multiRouteSupreme = new MultiRoute(multiRoutes);

        assertEquals(route1.length()+route2.length()+route3.length()+route4.length(),multiRouteSupreme.length());
    }
    @Test
    public void edgesTest(){
        edges1.add(edge1);
        edges1.add(edge2);
        SingleRoute route1 = new SingleRoute(edges1);

        edges2.add(edge3);
        edges2.add(edge4);
        SingleRoute route2 = new SingleRoute(edges2);

        edges3.add(edgeReverse4);
        edges3.add(edgeReverse3);
        SingleRoute route3 = new SingleRoute(edges3);

        edges4.add(edgeReverse2);
        edges4.add(edgeReverse1);
        SingleRoute route4 = new SingleRoute(edges4);


        List<Route> routes1 = new ArrayList<>();
        routes1.add(route1);
        routes1.add(route2);
        List<Route> routes2 = new ArrayList<>();
        routes2.add(route3);
        routes2.add(route4);

        MultiRoute multiRoute1 = new MultiRoute(routes1);
        MultiRoute multiRoute2 = new MultiRoute(routes2);

        List<Route> multiRoutes = new ArrayList<>();
        multiRoutes.add(multiRoute1);
        multiRoutes.add(multiRoute2);

        MultiRoute multiRouteSupreme = new MultiRoute(multiRoutes);
        List<Edge> edges = List.of(edge1,edge2,edge3,edge4,edgeReverse4,edgeReverse3,edgeReverse2,edgeReverse1);
        assertEquals(edges,multiRouteSupreme.edges());
    }
    @Test
    public void pointsTest(){
        edges1.add(edge1);
        edges1.add(edge2);
        SingleRoute route1 = new SingleRoute(edges1);

        edges2.add(edge3);
        edges2.add(edge4);
        SingleRoute route2 = new SingleRoute(edges2);

        edges3.add(edgeReverse4);
        edges3.add(edgeReverse3);
        SingleRoute route3 = new SingleRoute(edges3);

        edges4.add(edgeReverse2);
        edges4.add(edgeReverse1);
        SingleRoute route4 = new SingleRoute(edges4);


        List<Route> routes1 = new ArrayList<>();
        routes1.add(route1);
        routes1.add(route2);
        List<Route> routes2 = new ArrayList<>();
        routes2.add(route3);
        routes2.add(route4);

        MultiRoute multiRoute1 = new MultiRoute(routes1);
        MultiRoute multiRoute2 = new MultiRoute(routes2);

        List<Route> multiRoutes = new ArrayList<>();
        multiRoutes.add(multiRoute1);
        multiRoutes.add(multiRoute2);

        MultiRoute multiRouteSupreme = new MultiRoute(multiRoutes);

        List<PointCh> points = List.of(point1,point2,point3,point4,point5,point4,point3,point2,point1);

        assertEquals(points,multiRouteSupreme.points());
    }
    @Test
    public void elevationAtTest(){
        edges1.add(edge1);
        edges1.add(edge2);
        SingleRoute route1 = new SingleRoute(edges1);

        edges2.add(edge3);
        edges2.add(edge4);
        SingleRoute route2 = new SingleRoute(edges2);

        edges3.add(edgeReverse4);
        edges3.add(edgeReverse3);
        SingleRoute route3 = new SingleRoute(edges3);

        edges4.add(edgeReverse2);
        edges4.add(edgeReverse1);
        SingleRoute route4 = new SingleRoute(edges4);


        List<Route> routes1 = new ArrayList<>();
        routes1.add(route1);
        routes1.add(route2);
        List<Route> routes2 = new ArrayList<>();
        routes2.add(route3);
        routes2.add(route4);

        MultiRoute multiRoute1 = new MultiRoute(routes1);
        MultiRoute multiRoute2 = new MultiRoute(routes2);

        List<Route> multiRoutes = new ArrayList<>();
        multiRoutes.add(multiRoute1);
        multiRoutes.add(multiRoute2);

        MultiRoute multiRouteSupreme = new MultiRoute(multiRoutes);

        assertEquals(route3.elevationAt(18302-route1.length()-route2.length()), multiRouteSupreme.elevationAt(18302),1e-6);
        assertEquals(route1.elevationAt(0), multiRouteSupreme.elevationAt(0));
        assertEquals(route4.elevationAt(route4.length()), multiRouteSupreme.elevationAt(multiRouteSupreme.length()));
        assertEquals(route3.elevationAt(route3.length()), multiRouteSupreme.elevationAt(route1.length()+route2.length()+route3.length()));
        assertEquals(route1.elevationAt(route1.length()), multiRouteSupreme.elevationAt(route1.length()));
    }
    @Test
    public void pointAtTest(){
        edges1.add(edge1);
        edges1.add(edge2);
        SingleRoute route1 = new SingleRoute(edges1);

        edges2.add(edge3);
        edges2.add(edge4);
        SingleRoute route2 = new SingleRoute(edges2);

        edges3.add(edgeReverse4);
        edges3.add(edgeReverse3);
        SingleRoute route3 = new SingleRoute(edges3);

        edges4.add(edgeReverse2);
        edges4.add(edgeReverse1);
        SingleRoute route4 = new SingleRoute(edges4);


        List<Route> routes1 = new ArrayList<>();
        routes1.add(route1);
        routes1.add(route2);
        List<Route> routes2 = new ArrayList<>();
        routes2.add(route3);
        routes2.add(route4);

        MultiRoute multiRoute1 = new MultiRoute(routes1);
        MultiRoute multiRoute2 = new MultiRoute(routes2);

        List<Route> multiRoutes = new ArrayList<>();
        multiRoutes.add(multiRoute1);
        multiRoutes.add(multiRoute2);

        MultiRoute multiRouteSupreme = new MultiRoute(multiRoutes);

        assertEquals(route3.pointAt(18302-route1.length()-route2.length()), multiRouteSupreme.pointAt(18302));
        assertEquals(route1.pointAt(0), multiRouteSupreme.pointAt(0));
        assertEquals(route4.pointAt(route4.length()), multiRouteSupreme.pointAt(multiRouteSupreme.length()));
    }
    @Test
    public void nodeClosestToTest(){
        edges1.add(edge1);
        edges1.add(edge2);
        SingleRoute route1 = new SingleRoute(edges1);

        edges2.add(edge3);
        edges2.add(edge4);
        SingleRoute route2 = new SingleRoute(edges2);

        edges3.add(edgeReverse4);
        edges3.add(edgeReverse3);
        SingleRoute route3 = new SingleRoute(edges3);

        edges4.add(edgeReverse2);
        edges4.add(edgeReverse1);
        SingleRoute route4 = new SingleRoute(edges4);


        List<Route> routes1 = new ArrayList<>();
        routes1.add(route1);
        routes1.add(route2);
        List<Route> routes2 = new ArrayList<>();
        routes2.add(route3);
        routes2.add(route4);

        MultiRoute multiRoute1 = new MultiRoute(routes1);
        MultiRoute multiRoute2 = new MultiRoute(routes2);

        List<Route> multiRoutes = new ArrayList<>();
        multiRoutes.add(multiRoute1);
        multiRoutes.add(multiRoute2);

        MultiRoute multiRouteSupreme = new MultiRoute(multiRoutes);

        assertEquals(18, multiRouteSupreme.nodeClosestTo(18302));
        assertEquals(8, multiRouteSupreme.nodeClosestTo(25000));
        assertEquals(0, multiRouteSupreme.nodeClosestTo(0));
        assertEquals(0, multiRouteSupreme.nodeClosestTo(multiRouteSupreme.length()));
    }
    @Test
    public void pointClosestToTest(){

        edges1.add(edge1);
        edges1.add(edge2);
        SingleRoute route1 = new SingleRoute(edges1);

        edges2.add(edge3);
        edges2.add(edge4);
        SingleRoute route2 = new SingleRoute(edges2);

        edges3.add(edgeReverse4);
        edges3.add(edgeReverse3);
        SingleRoute route3 = new SingleRoute(edges3);

        edges4.add(edgeReverse2);
        edges4.add(edgeReverse1);
        SingleRoute route4 = new SingleRoute(edges4);


        List<Route> routes1 = new ArrayList<>();
        routes1.add(route1);
        routes1.add(route2);
        List<Route> routes2 = new ArrayList<>();
        routes2.add(route3);
        routes2.add(route4);

        MultiRoute multiRoute1 = new MultiRoute(routes1);
        MultiRoute multiRoute2 = new MultiRoute(routes2);

        List<Route> multiRoutes = new ArrayList<>();
        multiRoutes.add(multiRoute1);
        multiRoutes.add(multiRoute2);

        MultiRoute multiRouteSupreme = new MultiRoute(multiRoutes);


        RoutePoint routePoint1 = new RoutePoint(point5,route1.length()+route2.length(),0);
        assertEquals(routePoint1, multiRouteSupreme.pointClosestTo(point5));

        PointCh expectedPoint = new PointCh(2609500,1199500);
        RoutePoint routePoint2 = new RoutePoint(point5,route1.length()+route2.length(),expectedPoint.distanceTo(point5));
        assertEquals(routePoint2,multiRouteSupreme.pointClosestTo(expectedPoint));

        //c'est pas toi c'est moi
        //envoyez moi des colis c'est le PointCh de ma maison (Clement sans accent parce que c'est William qui ecrit)
        PointCh exPoint = new PointCh(2534471, 1154885);
        RoutePoint routePoint3 = new RoutePoint(point1, 0,exPoint.distanceTo(point1));
        assertEquals(routePoint3, multiRouteSupreme.pointClosestTo(exPoint));


        List<Route> singleRoutes = new ArrayList<>();
        SingleRoute singleRoute1 = new SingleRoute(List.of(edge1));
        SingleRoute singleRoute2 = new SingleRoute(List.of(edge2));
        SingleRoute singleRoute3 = new SingleRoute(List.of(edge3));
        SingleRoute singleRoute4 = new SingleRoute(List.of(edge4));
        singleRoutes.add(singleRoute1);
        singleRoutes.add(singleRoute2);
        singleRoutes.add(singleRoute3);
        singleRoutes.add(singleRoute4);
        MultiRoute cRoute = new MultiRoute(singleRoutes);

        //Si celui là marche vous êtes chauds (Oui il y a des accents donc c'est l'autre loustic)
        PointCh middlePoint = new PointCh(2604723, 1193147);
        RoutePoint routePoint4 = new RoutePoint(point5, route1.length()+route2.length(),middlePoint.distanceTo(point5));
        //Si le test ne fonctionne pas pour vous, décommenter ces prints pourrait vous aider.
        //System.out.println(middlePoint.distanceTo(point1));
        //System.out.println(middlePoint.distanceTo(point2));
        //System.out.println(middlePoint.distanceTo(point3));
        //System.out.println(middlePoint.distanceTo(point4));
        //System.out.println(middlePoint.distanceTo(point5));
        assertEquals(routePoint4, cRoute.pointClosestTo(middlePoint));
    }
}