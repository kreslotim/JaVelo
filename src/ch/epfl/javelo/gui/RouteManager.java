package ch.epfl.javelo.gui;


import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import ch.epfl.javelo.routing.Route;
import ch.epfl.javelo.routing.RoutePoint;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;


/**
 * Route Manager manages the route display
 *
 * @author Tim Kreslo (310686)
 * @author Wei-En Hsieh (341271)
 */
public final class RouteManager {
    private final RouteBean routeBean;
    private final ReadOnlyObjectProperty<MapViewParameters> mapViewParametersProperty;
    private final Pane pane;
    private final ObservableList<Waypoint> waypointList;
    private final List<Double> listPointsXY = new ArrayList<>();
    private final Polyline polyline;
    private final Circle circle;
    private final int DISK_RADIUS = 5;
    private final int HIGHLIGHT_OFFSET = 1;

    /**
     * Default RouteManager constructor
     *
     * @param routeBean                 Route's Bean (JavaFX)
     * @param mapViewParametersProperty JavaFX property containing the parameters of the map displayed
     */
    public RouteManager(RouteBean routeBean, ReadOnlyObjectProperty<MapViewParameters> mapViewParametersProperty) {
        this.routeBean = routeBean;
        this.mapViewParametersProperty = mapViewParametersProperty;


        circle = new Circle();
        polyline = new Polyline();
        pane = new Pane(polyline, circle);
        pane.setPickOnBounds(false);
        waypointList = routeBean.getWaypoints();

        polyline.setId("route");
        circle.setId("highlight");
        circle.setRadius(DISK_RADIUS);


        installListenersAndEventHandler();

        drawPolyLine(); // draw the full PolyLine once at the beginning
    }


    /**
     * Returns the JavaFX panel containing the line representing the route and the highlight disk
     *
     * @return the panel pane
     */
    public Pane pane() {
        return pane;
    }


    /**
     * Auxiliary (private) method drawing the route, represented by a JavaFX polyline, on the map,
     * and positioning the highlight disk on the route
     */
    private void drawPolyLine() {
        polyline.getPoints().clear();
        listPointsXY.clear();

        Route route = routeBean.routeProperty().get();
        MapViewParameters mapViewParameters = mapViewParametersProperty.get();

        if (route!=null) {

            for (PointCh pointCh : route.points()) {
                listPointsXY.add(PointWebMercator.ofPointCh(pointCh).xAtZoomLevel(mapViewParameters.zoomLevel()));
                listPointsXY.add(PointWebMercator.ofPointCh(pointCh).yAtZoomLevel(mapViewParameters.zoomLevel()));
            }

            repositionPolyLine(mapViewParameters);
            repositionHighlight(route, mapViewParameters);
            polyline.getPoints().setAll(listPointsXY);


            polyline.setStyle("-fx-stroke: linear-gradient(hsb(0,100%,100%,0.5) 0%," +
                    " rgba(255,154,0,0.7) 10%," +
                    " rgba(208,222,33,0.7) 20%," +
                    " rgba(79,220,74,0.7) 30%," +
                    " rgba(63,218,216,0.7) 40%," +
                    " rgba(47,201,226,0.7) 50%," +
                    " rgba(28,127,238,0.7) 60%," +
                    " rgba(95,21,242,0.7) 70%," +
                    " rgba(186,12,248,0.7) 80%," +
                    " rgba(251,7,217,0.7) 90%);");


            polyline.setVisible(true);
        }
        else {
            polyline.setVisible(false);
            circle.setVisible(false);
        }
    }


    /**
     * Auxiliary (private) method repositioning the highlight Disk
     *
     * @param route current route
     * @param mvp parameters of the map displayed (MapViewParameters)
     */
    private void repositionHighlight(Route route, MapViewParameters mvp) {

        PointCh pointOnLine = route.pointAt(routeBean.highlightedPosition());

        circle.setLayoutX(mvp.viewX(PointWebMercator.ofPointCh(pointOnLine)));
        circle.setLayoutY(mvp.viewY(PointWebMercator.ofPointCh(pointOnLine)));
    }


    /**
     * Auxiliary (private) method repositioning the route, represented by a JavaFX polyline
     *
     * @param mvp parameters of the map displayed (MapViewParameters)
     */
    private void repositionPolyLine(MapViewParameters mvp) {
        polyline.setLayoutX(-mvp.mapTopLeftPositionX());
        polyline.setLayoutY(-mvp.mapTopLeftPositionY());
    }


    /**
     * Auxiliary (private) method installing all the listeners implemented on the appropriate properties,
     * and event handler implemented on the highlight position, represented by a disk (circle), allowing to
     * add a waypoint at its location
     */
    private void installListenersAndEventHandler() {

        this.routeBean.routeProperty().addListener((p,o,n) -> drawPolyLine());

        this.routeBean.highlightedPositionProperty().addListener((p,o,n) -> {

            Route route = routeBean.routeProperty().get();
            MapViewParameters mvp = mapViewParametersProperty.get();

            circle.setVisible(!Double.isNaN(n.doubleValue()));

            if (route != null) repositionHighlight(route, mvp);
        });

        this.mapViewParametersProperty.addListener((p,o,n) -> {

            Route route = routeBean.routeProperty().get();

            if (o.zoomLevel() == n.zoomLevel() && route != null) {
                repositionPolyLine(n);
                repositionHighlight(route, n);
            }
            else drawPolyLine(); // redraw the full PolyLine
        });

        circle.setOnMouseClicked(e -> {

            Route route = routeBean.routeProperty().get();
            MapViewParameters mvp = mapViewParametersProperty.get();

            int highlightIndex = routeBean.indexOfNonEmptySegmentAt(routeBean.highlightedPosition()) + HIGHLIGHT_OFFSET;
            Point2D cursor = circle.localToParent(e.getX(), e.getY());
            PointCh highlightPoint = mvp.pointAt(cursor.getX(), cursor.getY()).toPointCh();
            int closestNodeToHighlight = route.nodeClosestTo(routeBean.highlightedPosition());
            waypointList.add(highlightIndex, new Waypoint(highlightPoint, closestNodeToHighlight));

        });
    }
}
