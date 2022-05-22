package ch.epfl.javelo.gui;


import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.shape.SVGPath;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * WaypointsManager manages display and interaction with waypoints
 *
 * @author Tim Kreslo (310686)
 * @author Wei-En Hsieh (341271)
 */
public final class WaypointsManager {
    private final Graph graph;
    private final ObservableList<Waypoint> waypoints;
    private final ObjectProperty<MapViewParameters> mapViewParametersProperty;
    private final ObjectProperty<Point2D> point2DProperty = new SimpleObjectProperty<>();
    private final Consumer<String> errorConsumer;

    private final Pane pane;
    private final List<Node> markersList = new ArrayList<>();

    private final static int SEARCH_DISTANCE = 1000/2;

    /**
     * Default WaypointsManager constructor
     *
     * @param graph                     Route network graph
     * @param mapViewParametersProperty JavaFX property containing the parameters of the map displayed
     * @param waypoints                 Observable list of all waypoints
     * @param errorConsumer             Object for reporting errors
     */
    public WaypointsManager(Graph graph, ObjectProperty<MapViewParameters> mapViewParametersProperty,
                            ObservableList<Waypoint> waypoints, Consumer<String> errorConsumer) {

        this.graph = graph;
        this.waypoints = waypoints; //TODO must be immutable
        this.mapViewParametersProperty = mapViewParametersProperty;
        this.errorConsumer = errorConsumer;

        pane = new Pane();
        pane.setPickOnBounds(false);

        setUpListeners();

        drawWaypoints();
    }

    /**
     * Returns JavaFX panel containing waypoints
     *
     * @return JavaFX panel
     */
    public Pane pane() {
        return pane;
    }

    /**
     * Auxiliary (private) method drawing all the waypoints on the map
     */
    private void drawWaypoints() {
        MapViewParameters mapViewParameters = mapViewParametersProperty.get();
        markersList.clear();

        for (int i = 0; i < waypoints.size(); i++) {

            Group marker = createMarker();
            marker.getStyleClass().add("pin");

            marker.setOnMousePressed(e -> {
                Point2D pressedPoint = new Point2D(e.getX(), e.getY());
                point2DProperty.setValue(pressedPoint);
            });

            marker.setOnMouseDragged(e -> {

                Point2D previousPoint = point2DProperty.get();

                marker.setLayoutX(e.getSceneX()); //TODO Ok to use getScene ?
                marker.setLayoutY(e.getSceneY());

            });

            int indexI = i; // syntax lambda
            marker.setOnMouseReleased(e -> {

                Point2D previousPoint = point2DProperty.get();

                if (e.isStillSincePress()) waypoints.remove(indexI);
                else {

                    PointCh newPointCh = mapViewParametersProperty.get().pointAt(
                            e.getSceneX(),
                            e.getSceneY())
                            .toPointCh();

                    if (newPointCh != null) {
                        int newNodeId = graph.nodeClosestTo(newPointCh, SEARCH_DISTANCE);
                        if (newNodeId != -1) waypoints.set(indexI, new Waypoint(newPointCh, newNodeId));
                        else {
                            drawWaypoints();
                            errorConsumer.accept("No route nearby !");
                        }
                    }
                    else drawWaypoints();
                }
            });

            Waypoint currentWaypoint = waypoints.get(i);
            PointWebMercator waypointWebMercator = PointWebMercator.ofPointCh(currentWaypoint.waypointCh());
            marker.setLayoutX(mapViewParameters.viewX(waypointWebMercator));
            marker.setLayoutY(mapViewParameters.viewY(waypointWebMercator));

            if (waypoints.indexOf(currentWaypoint) == 0) marker.getStyleClass().add("first");
            else if (waypoints.indexOf(currentWaypoint) == waypoints.size()-1) marker.getStyleClass().add("last");
            else marker.getStyleClass().add("middle");

            markersList.add(marker);
        }

        pane.getChildren().setAll(markersList);
    }

    /**
     * Auxiliary (private) method creating a marker
     *
     * @return Group of Nodes composing a marker
     */
    private Group createMarker() {
        SVGPath contour = new SVGPath();
        contour.setContent("M-8-20C-5-14-2-7 0 0 2-7 5-14 8-20 20-40-20-40-8-20");
        SVGPath disk = new SVGPath();
        disk.setContent("M0-23A1 1 0 000-29 1 1 0 000-23");

        contour.getStyleClass().add("pin_outside");
        disk.getStyleClass().add("pin_inside");
        return new Group(contour, disk);
    }


    /**
     * Auxiliary (private) method adding a Waypoint on the map
     *
     * @param x coordinate X of a point, used for adding a Waypoint
     * @param y coordinate Y of a point, used for adding a Waypoint
     */
    public void addWaypoint(double x, double y) {

        MapViewParameters mapViewParameters = mapViewParametersProperty.get();

        PointWebMercator p = mapViewParameters.pointAt(x, y);

        PointCh pointCh = p.toPointCh();

        if (pointCh != null) {
            int nearestNodeId = graph.nodeClosestTo(pointCh, SEARCH_DISTANCE);

            if (nearestNodeId == -1) { // if there's no nearestNodes
                errorConsumer.accept("No route nearby !");
            } else { // if the nearestNode exists
                //System.out.println("Adding a waypoint");
                waypoints.add(new Waypoint(pointCh, nearestNodeId));
            }
        }
    }

    private void setUpListeners() {

        mapViewParametersProperty.addListener((p,o,n) -> {

            for (Node marker : pane.getChildren()) {

                PointWebMercator pointWebMercator = o.pointAt(marker.getLayoutX(), marker.getLayoutY());

                marker.setLayoutX(n.viewX(pointWebMercator));
                marker.setLayoutY(n.viewY(pointWebMercator));
            }
        });

        waypoints.addListener((ListChangeListener<Waypoint>) o -> drawWaypoints());
    }
}
