package ch.epfl.javelo.gui;


import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import ch.epfl.javelo.routing.Route;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Route manager manages the route display
 *
 * @author Tim Kreslo (310686)
 * @author Wei-En Hsieh (341271)
 */
public final class RouteManager {
    private final RouteBean routeBean;
    private final ReadOnlyObjectProperty<MapViewParameters> mapViewParametersProperty;
    private final Pane pane;
    private final ObservableList<Waypoint> waypointList;
    private final List<Double> pointsXY = new ArrayList<>();
    private final Polyline polyline;
    private final Circle circle;
    private final int HIGHLIGHT_OFFSET = 1;
    private final int DISK_RADIUS = 5;

    /**
     * Default RouteManager constructor
     *
     * @param routeBean                 Route's Bean (JavaFX)
     * @param mapViewParametersProperty Object property containing a MapViewParameters
     * @param errorConsumer             object for reporting errors
     */
    public RouteManager(RouteBean routeBean, ReadOnlyObjectProperty<MapViewParameters> mapViewParametersProperty,
                        Consumer<String> errorConsumer) {
        this.routeBean = routeBean;
        this.mapViewParametersProperty = mapViewParametersProperty;

        pane = new Pane();
        circle = new Circle();
        polyline = new Polyline();
        waypointList = routeBean.getWaypoints();

        polyline.setId("route");
        circle.setId("highlight");
        circle.setRadius(DISK_RADIUS);

        pane.setPickOnBounds(false);
        pane.getChildren().setAll(polyline, circle);

        circle.setOnMouseClicked(e -> {

            Route route = routeBean.route().get();
            MapViewParameters mvp = mapViewParametersProperty.get();

            int highlightIndex = route.indexOfSegmentAt(routeBean.highlightedPosition()) + HIGHLIGHT_OFFSET;

            Point2D cursor = circle.localToParent(e.getX(), e.getY());

            PointCh highlightPoint = mvp.pointAt(cursor.getX(), cursor.getY()).toPointCh();

            int closestNodeToHighlight = route.nodeClosestTo(routeBean.highlightedPosition());
            boolean alreadyExists = false;

            for (Waypoint waypoint : routeBean.getWaypoints()) {
                if (closestNodeToHighlight == waypoint.nearestNodeId()) {
                    errorConsumer.accept("A waypoint is already present at this location !");
                    alreadyExists = true;
                    break;
                }
            }

            if (!alreadyExists) {
                waypointList.add(highlightIndex, new Waypoint(highlightPoint, closestNodeToHighlight));
            }


        });


        this.waypointList.addListener((InvalidationListener) o -> {});

        this.routeBean.route().addListener((p, o, n) -> drawPolyLine());

        this.mapViewParametersProperty.addListener((p,o,n) -> {

            Route route = routeBean.route().get();

                if (o.zoomLevel() == n.zoomLevel() && route != null) {
                    repositionPolyLine(n);
                    repositionHighlight(route, n);
                }
                else drawPolyLine(); // redraw the full PolyLine

        });

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
        pointsXY.clear();
        polyline.setVisible(false);

        Route route = routeBean.route().get();
        MapViewParameters mapViewParameters = mapViewParametersProperty.get();

        if (route!=null) {
            for (PointCh pointCh : route.points()) {
                pointsXY.add(PointWebMercator.ofPointCh(pointCh).xAtZoomLevel(mapViewParameters.zoomLevel()));
                pointsXY.add(PointWebMercator.ofPointCh(pointCh).yAtZoomLevel(mapViewParameters.zoomLevel()));
            }
            repositionPolyLine(mapViewParameters);
            repositionHighlight(route, mapViewParameters);
            polyline.getPoints().setAll(pointsXY);
            polyline.setVisible(true);
            circle.setVisible(true);
        }
        else {
            polyline.setVisible(false);
            circle.setVisible(false);
        }


    }

    /**
     * Auxiliary (private) repositioning the highlight Disk
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
     * Auxiliary (private) repositioning the route, respresented by a JavaFX polyline
     *
     * @param mvp parameters of the map displayed (MapViewParameters)
     */
    private void repositionPolyLine(MapViewParameters mvp) {
        polyline.setLayoutX(-mvp.mapTopLeftPositionX());
        polyline.setLayoutY(-mvp.mapTopLeftPositionY());
    }
}
