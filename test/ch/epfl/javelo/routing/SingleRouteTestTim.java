package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleUnaryOperator;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test of SingleRoute
 * @author Tim Kreslo (310686)
 */
class SingleRouteTestTim {

    private static final double DELTA = 1e-2;
    private static final SingleRoute HORIZONTAL_PATH = HorizontalPath(); // Constant north coordinate
    private static final SingleRoute VERTICAL_PATH = VerticalPath(); // Constant east coordinate
    private static final SingleRoute DIAGONAL_PATH = DiagonalPath(); //
    private static final SingleRoute ZEBRA_PATH = ZebraPath(); // Zebra path

    public static SingleRoute HorizontalPath() {
        PointCh point0 = new PointCh(2_532_700, 1_152_348);
        PointCh point1 = new PointCh(2_532_700 + 5800, 1_152_348);
        PointCh point2 = new PointCh(2_532_700 + 8100, 1_152_348);
        PointCh point3 = new PointCh(2_532_700 + 9200, 1_152_348);
        PointCh point4 = new PointCh(2_532_700 + 11400, 1_152_348);
        PointCh point5 = new PointCh(2_532_700 + 13100, 1_152_348);

        Edge edge0 = new Edge(0,1, point0, point1, 5800, DoubleUnaryOperator.identity());
        Edge edge1 = new Edge(1,2, point1, point2, 2300, DoubleUnaryOperator.identity());
        Edge edge2 = new Edge(2,3, point2, point3, 1100, DoubleUnaryOperator.identity());
        Edge edge3 = new Edge(3,4, point3, point4, 2200, DoubleUnaryOperator.identity());
        Edge edge4 = new Edge(4,5, point4, point5, 1700, DoubleUnaryOperator.identity());

        List<Edge> edgeList = new ArrayList<>(List.of(edge0, edge1, edge2, edge3, edge4));
        return new SingleRoute(edgeList);
    }

    public static SingleRoute VerticalPath() {
        PointCh point0 = new PointCh(2_532_705, 1_152_300);
        PointCh point1 = new PointCh(2_532_705, 1_152_300 + 5800);
        PointCh point2 = new PointCh(2_532_705, 1_152_300 + 8100);
        PointCh point3 = new PointCh(2_532_705, 1_152_300 + 9200);
        PointCh point4 = new PointCh(2_532_705, 1_152_300 + 11400);
        PointCh point5 = new PointCh(2_532_705, 1_152_300 + 13100);

        Edge edge0 = new Edge(0,1, point0, point1, 5800, DoubleUnaryOperator.identity());
        Edge edge1 = new Edge(1,2, point1, point2, 2300, DoubleUnaryOperator.identity());
        Edge edge2 = new Edge(2,3, point2, point3, 1100, DoubleUnaryOperator.identity());
        Edge edge3 = new Edge(3,4, point3, point4, 2200, DoubleUnaryOperator.identity());
        Edge edge4 = new Edge(4,5, point4, point5, 1700, DoubleUnaryOperator.identity());

        List<Edge> edgeList = new ArrayList<>(List.of(edge0, edge1, edge2, edge3, edge4));
        return new SingleRoute(edgeList);
    }

    public static SingleRoute DiagonalPath() {
        PointCh point0 = new PointCh(2_532_705, 1_152_348);
        PointCh point1 = new PointCh(2_532_715, 1_152_358);
        PointCh point2 = new PointCh(2_532_725, 1_152_368);
        PointCh point3 = new PointCh(2_532_735, 1_152_378);
        PointCh point4 = new PointCh(2_532_745, 1_152_388);
        PointCh point5 = new PointCh(2_532_755, 1_152_398);

        Edge edge0 = new Edge(0,1, point0, point1, Math.sqrt(200), DoubleUnaryOperator.identity());
        Edge edge1 = new Edge(1,2, point1, point2, Math.sqrt(200), DoubleUnaryOperator.identity());
        Edge edge2 = new Edge(2,3, point2, point3, Math.sqrt(200), DoubleUnaryOperator.identity());
        Edge edge3 = new Edge(3,4, point3, point4, Math.sqrt(200), DoubleUnaryOperator.identity());
        Edge edge4 = new Edge(4,5, point4, point5, Math.sqrt(200), DoubleUnaryOperator.identity());

        List<Edge> edgeList = new ArrayList<>(List.of(edge0, edge1, edge2, edge3, edge4));
        return new SingleRoute(edgeList);
    }


    public static SingleRoute ZebraPath() {
        PointCh point0 = new PointCh( 2485000, 1075000); //MIN E,N SwissBounds
        PointCh point1 = new PointCh(2485100, 1075000);
        PointCh point2 = new PointCh(2485100, 1075100);
        PointCh point3 = new PointCh(2485200, 1075100);
        PointCh point4 = new PointCh(2485200, 1075200);
        PointCh point5 = new PointCh(2485300, 1075200);

        Edge edge0 = new Edge(0, 1, point0, point1, 100, DoubleUnaryOperator.identity());
        Edge edge1 = new Edge(1, 2, point1, point2, 100, DoubleUnaryOperator.identity());
        Edge edge2 = new Edge(2, 3, point2, point3, 100, DoubleUnaryOperator.identity());
        Edge edge3 = new Edge(3, 4, point3, point4, 100, DoubleUnaryOperator.identity());
        Edge edge4 = new Edge(4, 5, point4, point5, 100, DoubleUnaryOperator.identity());

        List<Edge> edgeList = new ArrayList<>(List.of(edge0, edge1, edge2, edge3, edge4));
        return new SingleRoute(edgeList);
    }

    @Test
    void SingleRouteThrowsOnEmptyList() {
        List<Edge> emptyList = new ArrayList<>();
        assertThrows(
                IllegalArgumentException.class,
                () -> { new SingleRoute(emptyList); });
    }

    @Test
    void indexOfSegmentWorks() {
        int valH = HORIZONTAL_PATH.indexOfSegmentAt(42);
        int valV = VERTICAL_PATH.indexOfSegmentAt(24);
        int valD = DIAGONAL_PATH.indexOfSegmentAt(44);
        assertEquals(0, valH);
        assertEquals(0, valV);
        assertEquals(0, valD);
    }


    @Test
    void lengthHorizontalPathWorks() {
        double valH = HORIZONTAL_PATH.length();
        double valV = VERTICAL_PATH.length();
        double valD = DIAGONAL_PATH.length();
        assertEquals(13100, valH);
        assertEquals(13100, valV);
        assertEquals(Math.sqrt(200) * 5, valD);
    }

    @Test
    void edgesWorks() {
        assertEquals(HorizontalPath().edges(), HORIZONTAL_PATH.edges());
        assertEquals(VerticalPath().edges(), VERTICAL_PATH.edges());
        assertEquals(DiagonalPath().edges(), DIAGONAL_PATH.edges());
        //kind of stupid
    }

    @Test
    void pointsWorksHorizontal() {
        PointCh point0 = new PointCh(2_532_700, 1_152_348);
        PointCh point1 = new PointCh(2_532_700 + 5800, 1_152_348);
        PointCh point2 = new PointCh(2_532_700 + 8100, 1_152_348);
        PointCh point3 = new PointCh(2_532_700 + 9200, 1_152_348);
        PointCh point4 = new PointCh(2_532_700 + 11400, 1_152_348);
        PointCh point5 = new PointCh(2_532_700 + 13100, 1_152_348);

        List<PointCh> pointsList = new ArrayList<>(List.of(point0, point1, point2, point3, point4, point5));

        assertEquals(pointsList, HORIZONTAL_PATH.points());
    }

    @Test
    void pointsWorksVertical() {
        PointCh point0 = new PointCh(2_532_705, 1_152_300);
        PointCh point1 = new PointCh(2_532_705, 1_152_300 + 5800);
        PointCh point2 = new PointCh(2_532_705, 1_152_300 + 8100);
        PointCh point3 = new PointCh(2_532_705, 1_152_300 + 9200);
        PointCh point4 = new PointCh(2_532_705, 1_152_300 + 11400);
        PointCh point5 = new PointCh(2_532_705, 1_152_300 + 13100);

        List<PointCh> pointsList = new ArrayList<>(List.of(point0, point1, point2, point3, point4, point5));

        assertEquals(pointsList, VERTICAL_PATH.points());
    }

    @Test
    void pointsWorksDiagonal() {
        PointCh point0 = new PointCh(2_532_705, 1_152_348);
        PointCh point1 = new PointCh(2_532_715, 1_152_358);
        PointCh point2 = new PointCh(2_532_725, 1_152_368);
        PointCh point3 = new PointCh(2_532_735, 1_152_378);
        PointCh point4 = new PointCh(2_532_745, 1_152_388);
        PointCh point5 = new PointCh(2_532_755, 1_152_398);

        List<PointCh> pointsList = new ArrayList<>(List.of(point0, point1, point2, point3, point4, point5));

        assertEquals(pointsList, DIAGONAL_PATH.points());
    }

    @Test
    void pointAtWorks() {
        PointCh pointOnHorizontalRoute = new PointCh(2_532_700 + 9200 + 800, 1152348.0);
        PointCh pointOnHorizontalRoute0 = new PointCh(2_532_700, 1152348.0);
        PointCh pointOnHorizontalRoute1 = new PointCh(2_532_700 + 13100, 1152348.0);
        PointCh pointOnVerticalRoute = new PointCh(2_532_705, 1_152_300 + 9200 + 800);
        PointCh pointOnDiagonalRoute = new PointCh(2_532_755, 1_152_398);
        assertEquals(pointOnHorizontalRoute, HORIZONTAL_PATH.pointAt(10000));
        assertEquals(pointOnHorizontalRoute0, HORIZONTAL_PATH.pointAt(0));
        assertEquals(pointOnHorizontalRoute1, HORIZONTAL_PATH.pointAt(13100));
        assertEquals(pointOnVerticalRoute, VERTICAL_PATH.pointAt(10000));
        assertEquals(pointOnDiagonalRoute, DIAGONAL_PATH.pointAt(10000));
    }



    @Test
    void elevationAtWorks() {
        assertEquals(800, HORIZONTAL_PATH.elevationAt(10000));
        assertEquals(800, VERTICAL_PATH.elevationAt(10000));
        //assertEquals(10, DIAGONAL_PATH.elevationAt(20));
    }

    @Test
    void nodeClosestToWorks() {
        assertEquals(3, HORIZONTAL_PATH.nodeClosestTo(10000));
        assertEquals(3, VERTICAL_PATH.nodeClosestTo(10000));
        assertEquals(5, DIAGONAL_PATH.nodeClosestTo(10000));
    }




    @Test
    void pointClosestToWorksOnKnownValues() {
        PointCh point3 = new PointCh(2_532_700 + 9200 + 2, 1_152_348);
        PointCh referencePoint3 = new PointCh(2_532_700 + 9200 + 2, 1_152_348);
        RoutePoint routePoint3 = new RoutePoint(point3, 9200 + 2, 0);

        RoutePoint actualRoutePoint = HORIZONTAL_PATH.pointClosestTo(referencePoint3);

        assertEquals(routePoint3.point(), actualRoutePoint.point());
        assertEquals(routePoint3.position(), actualRoutePoint.position());
        assertEquals(routePoint3.distanceToReference(), actualRoutePoint.distanceToReference(), DELTA);



        PointCh point5 = new PointCh(2_532_700 + 13100, 1_152_348);
        PointCh referencePoint5 = new PointCh(2_532_700 + 13100+ 2, 1_152_348);
        RoutePoint routePoint5 = new RoutePoint(point5, 13100, 2);

        RoutePoint actualRoutePoint1 = HORIZONTAL_PATH.pointClosestTo(referencePoint5);


        assertEquals(routePoint5.point(), actualRoutePoint1.point());
        assertEquals(routePoint5.position(), actualRoutePoint1.position());
        assertEquals(routePoint5.distanceToReference(), actualRoutePoint1.distanceToReference(), DELTA);




        PointCh point3V = new PointCh(2_532_705, 1_152_300 + 9200 + 2);
        PointCh referencePoint3V = new PointCh(2_532_705, 1_152_300 + 9200 + 2);
        RoutePoint routePoint3V = new RoutePoint(point3V, 9200 + 2, 0);

        RoutePoint actualRoutePoint3V = VERTICAL_PATH.pointClosestTo(referencePoint3V);

        assertEquals(routePoint3V.point(), actualRoutePoint3V.point());
        assertEquals(routePoint3V.position(), actualRoutePoint3V.position());
        assertEquals(routePoint3V.distanceToReference(), actualRoutePoint3V.distanceToReference(), DELTA);
    }

    @Test
    void testZebra() {
        PointCh pointZ = new PointCh(2485000, 1075000);
        PointCh referencePointZ = new PointCh(2485000, 1075030);
        RoutePoint routePointZ = new RoutePoint(pointZ, 0, 30);

        RoutePoint actualRoutePointZ = ZEBRA_PATH.pointClosestTo(referencePointZ);

        assertEquals(routePointZ.point(), actualRoutePointZ.point());
        assertEquals(routePointZ.position(), actualRoutePointZ.position());
        assertEquals(routePointZ.distanceToReference(), actualRoutePointZ.distanceToReference(), DELTA);
    }

}


