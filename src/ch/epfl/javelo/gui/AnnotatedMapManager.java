package ch.epfl.javelo.gui;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import ch.epfl.javelo.routing.RoutePoint;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import java.util.function.Consumer;

/**
 * Annotated Map Manager manages the “annotated” map display,
 * i.e. the base map above which the route and waypoints are superimposed
 *
 * @author Tim Kreslo (310686)
 * @author Wei-En Hsieh (341271)
 */
public final class AnnotatedMapManager {
    private static final int DISTANCE_BETWEEN_HIGHLIGHT_AND_MOUSE_IN_PIXELS = 15; // in pixels
    private final StackPane mainPane;
    private final Graph graph;
    private final TileManager tileManager;
    private final RouteBean routeBean;
    private final Consumer<String> errorConsumer;
    private final ObjectProperty<MapViewParameters> mapViewParametersProperty;
    private final DoubleProperty mousePositionOnRouteProperty = new SimpleDoubleProperty();
    private final ObjectProperty<Point2D> currentMousePositionProperty = new SimpleObjectProperty<>();

    /**
     * Default AnnotatedMapManager constructor
     *
     * @param graph         Route network graph
     * @param tileManager   Tile manager that gets the tiles of the map
     * @param routeBean     Route's Bean (JavaFX)
     * @param errorConsumer Object for reporting errors
     */
    public AnnotatedMapManager(Graph graph, TileManager tileManager,
                               RouteBean routeBean, Consumer<String> errorConsumer) {
        this.graph = graph;
        this.tileManager = tileManager;
        this.routeBean = routeBean;
        this.errorConsumer = errorConsumer;

        MapViewParameters mapViewParameters =
                new MapViewParameters(12, 543200, 370650);

        mapViewParametersProperty = new SimpleObjectProperty<>(mapViewParameters);


        mainPane = buildStackPane();
        mainPane.getStylesheets().add("map.css");

        mainPane.setOnMouseMoved(e -> {
            Point2D mousePoint = new Point2D(e.getX(), e.getY());
            currentMousePositionProperty.set(mousePoint);
        });

        mainPane.setOnMouseExited(e -> {
            currentMousePositionProperty.set(null);
        });

        mousePositionOnRouteProperty.bind(Bindings.createDoubleBinding(() -> {

            if (currentMousePositionProperty.get() == null || routeBean.routeProperty().get() == null) {
                return Double.NaN;
            }


            PointCh currentMousePoint = mapViewParametersProperty.get()
                    .pointAt(currentMousePositionProperty.get().getX(),
                             currentMousePositionProperty.get().getY()).toPointCh();

            RoutePoint nearestRoutePoint = routeBean.routeProperty().get().pointClosestTo(currentMousePoint); //TODO error if go to mountain

            PointCh mousePoint = nearestRoutePoint.point();

            PointWebMercator mouseWebMercator = PointWebMercator.ofPointCh(mousePoint);


            double xScreenDelta = currentMousePositionProperty.get().getX()
                                - mapViewParametersProperty.get().viewX(mouseWebMercator);
            double yScreenDelta = currentMousePositionProperty.get().getY()
                                - mapViewParametersProperty.get().viewY(mouseWebMercator);

            double distanceToRoute = Math2.norm(xScreenDelta, yScreenDelta);


            return (distanceToRoute <= DISTANCE_BETWEEN_HIGHLIGHT_AND_MOUSE_IN_PIXELS)
                    ? nearestRoutePoint.position()
                    : Double.NaN;


        }, mapViewParametersProperty, routeBean.routeProperty(), currentMousePositionProperty));


    }

    /**
     * Returns JavaFX main panel displaying the "annotated" map
     * i.e. the base map above which the route and waypoints are superimposed
     *
     * @return JavaFX panel
     */
    public Pane pane() {
        return mainPane;
    }


    /**
     * Returns a read-only property containing the position (double) of the point closest to the mouse pointer
     * along the route (in meters, rounded to the nearest integer),
     * or NaN if the mouse pointer is further than 15 JavaFX units (pixels)
     *
     * @return read-only property containing the position of the mouse on the profile
     */
    public ReadOnlyDoubleProperty mousePositionOnRouteProperty() {
        return mousePositionOnRouteProperty;
    }

    /**
     * Auxiliary (private) method that builds the stack pane (JavaFX StackPane) out of the three panels
     * managing the upper half of the full panel (JavaFX SplitPane)
     * i.e. the base map above which the route and waypoints are superimposed
     *
     * @return JavaFX panel (StackPane)
     */
    private StackPane buildStackPane() {

        WaypointsManager waypointsManager =
                new WaypointsManager(graph, mapViewParametersProperty, routeBean.getWaypoints(), errorConsumer);

        BaseMapManager baseMapManager = new BaseMapManager(tileManager, waypointsManager, mapViewParametersProperty);

        RouteManager routeManager = new RouteManager(routeBean, mapViewParametersProperty);

        return new StackPane(baseMapManager.pane(), waypointsManager.pane(), routeManager.pane());
    }
}
