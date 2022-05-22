package ch.epfl.javelo.gui;

import ch.epfl.javelo.routing.*;
import javafx.beans.Observable;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Route Bean assembling properties relating to waypoints and the corresponding route
 *
 * @author Tim Kreslo (310686)
 * @author Wei-En Hsieh (341271)
 */
public final class RouteBean {
    private final RouteComputer routeComputer;
    private final List<Route> segments = new ArrayList<>();
    private final Map<String, Route> cacheRoute = new LinkedHashMap<>(100);
    private final ObjectProperty<Route> routeProperty = new SimpleObjectProperty<>();
    private final ObservableList<Waypoint> waypoints = FXCollections.observableArrayList();
    private final DoubleProperty highlightedPositionProperty = new SimpleDoubleProperty();
    private final ObjectProperty<ElevationProfile> elevationProfileProperty = new SimpleObjectProperty<>();

    /**
     * Default RouteBean constructor
     *
     * @param routeComputer used to determine the best route between two waypoints.
     */
    public RouteBean(RouteComputer routeComputer) {
        this.routeComputer = routeComputer;
        waypoints.addListener((Observable o) -> computeRoute());
    }

    /**
     * Auxiliary (private) method computing a new (best) route passing through all waypoints
     */
    private void computeRoute() {
        segments.clear();

        if (waypoints.size() > 1) {

            boolean segmentIsNull = false;
            for (int i = 1; i < waypoints.size(); i++) {

                Pair<Integer, Integer> pairNode = new Pair<>(waypoints.get(i - 1).nearestNodeId(),
                                                             waypoints.get(i).nearestNodeId());

                if (cacheRoute.containsKey(pairNode.toString())) {
                    Route segment = cacheRoute.get(pairNode.toString());
                    segments.add(segment);
                }
                else {

                    Route segment = null;
                    if (!pairNode.getKey().equals(pairNode.getValue())) {
                        segment = routeComputer.bestRouteBetween(pairNode.getKey(), pairNode.getValue());
                    }

                    if (segment == null) {
                        segmentIsNull = true;
                        break;
                    }

                    else {
                        segments.add(segment);
                        cacheRoute.put(pairNode.toString(), segment);
                    }
                }
            }

            if (segments.isEmpty() || segmentIsNull) {
                elevationProfileProperty.set(null);
                routeProperty.set(null);
            }
            else {
                MultiRoute multiRoute = new MultiRoute(segments);
                int MAX_STEP_LENGTH = 5;
                elevationProfileProperty.set(ElevationProfileComputer.elevationProfile(multiRoute, MAX_STEP_LENGTH));
                routeProperty.set(multiRoute);
            }
        }
        else {
            routeProperty.set(null);
            elevationProfileProperty.set(null);
        }
    }


    /**
     * public getter of the waypoints list
     *
     * @return observable list of all waypoints
     */
    public ObservableList<Waypoint> getWaypoints() {
        return waypoints;
    }

    /**
     * public getter of highlighted position
     *
     * @return double value of the highlighted position
     */
    public double highlightedPosition() {
        return highlightedPositionProperty.get();
    }

    /**
     * public getter of the highlighted position's property
     *
     * @return highlighted position's property itself
     */
    public DoubleProperty highlightedPositionProperty() {
        return highlightedPositionProperty;
    }

    /**
     * public setter for highlighted position
     *
     * @param highlightedPositionValue double value of the highlighted position
     */
    public void setHighlightedPositionProperty(double highlightedPositionValue) {
        highlightedPositionProperty.set(highlightedPositionValue);
    }

    /**
     * public getter of the route's property
     *
     * @return route's property itself in read only mode
     */
    public ReadOnlyObjectProperty<Route> routeProperty() {
        return routeProperty;
    }

    /**
     * public getter of the elevation profile's property
     *
     * @return elevation profile's property itself in read only mode
     */
    public ReadOnlyObjectProperty<ElevationProfile> elevationProfileProperty() {
        return elevationProfileProperty;
    }


    public int indexOfNonEmptySegmentAt(double position) {
        int index = routeProperty().get().indexOfSegmentAt(position);
        for (int i = 0; i <= index; i += 1) {
            int n1 = waypoints.get(i).nearestNodeId();
            int n2 = waypoints.get(i + 1).nearestNodeId();
            if (n1 == n2) index += 1;
        }
        return index;
    }
}