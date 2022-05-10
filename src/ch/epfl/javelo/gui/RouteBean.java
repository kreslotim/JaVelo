package ch.epfl.javelo.gui;

import ch.epfl.javelo.routing.*;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Route Bean assembling properties relating to waypoints and the corresponding route.
 *
 * @author Tim Kreslo (310686)
 * @author Wei-En Hsieh (341271)
 */
public final class RouteBean {
    private final RouteComputer routeComputer; //TODO Must be final ?
    private final ObjectProperty<Route> route = new SimpleObjectProperty<>();
    private final DoubleProperty highlightedPosition = new SimpleDoubleProperty(); // modifiable from outside
    private final ObservableList<Waypoint> waypoints = FXCollections.observableArrayList(); // modifiable from outside
    private final ObjectProperty<ElevationProfile> elevationProfile = new SimpleObjectProperty<>();
    private final Map<String, Route> cacheRoute = new LinkedHashMap<>(100);
    private final List<Route> segments = new ArrayList<>();

    /**
     * Default RouteBean constructor
     *
     * @param routeComputer used to determine the best route between two waypoints.
     */
    public RouteBean(RouteComputer routeComputer) {
        this.routeComputer = routeComputer;
        waypoints.addListener((ListChangeListener<Waypoint>) o -> computeRoute());
    }

    /**
     * Auxiliary (private) method computing a new (best) route passing through all waypoints
     */
    private void computeRoute() {
        segments.clear();
        route.setValue(null);
        elevationProfile.setValue(null);
        boolean routeIsNull = false;

        if (waypoints.size() > 1) {

            for (int i = 1; i < waypoints.size(); i++) {

                Pair<Integer, Integer> pairNode = new Pair<>(waypoints.get(i - 1).nearestNodeId(), waypoints.get(i).nearestNodeId());

                if (cacheRoute.containsKey(pairNode.toString())) {
                    Route segment = cacheRoute.get(pairNode.toString());
                    segments.add(segment);
                } else {
                    Route segment = routeComputer.bestRouteBetween(pairNode.getKey(), pairNode.getValue());
                    if (segment == null) {
                        routeIsNull = true;
                        route.setValue(null);
                        break;
                    }
                    segments.add(segment);
                    cacheRoute.put(pairNode.toString(), segment);
                }
            }

            if (routeIsNull || segments.isEmpty()) {
                //highlightedPosition.setValue(Double.NaN);
                elevationProfile.setValue(null);
                route.setValue(null);
            }
            else {
                MultiRoute multiRoute = new MultiRoute(segments);
                elevationProfile.setValue(ElevationProfileComputer.elevationProfile(multiRoute, 5));
                route.setValue(multiRoute);
            }
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
        return highlightedPosition.doubleValue();
    }

    /**
     * public getter of the highlighted position's property
     *
     * @return highlighted position's property itself
     */
    public DoubleProperty highlightedPositionProperty() {
        return highlightedPosition;
    }

    /**
     * public setter for highlighted position
     *
     * @param highlightedPositionValue double value of the highlighted position
     */
    public void setHighlightedPosition(double highlightedPositionValue) {
        highlightedPosition.setValue(highlightedPositionValue);
    }

    /**
     * public getter of the route's property
     *
     * @return route's property itself in read only mode
     */
    public ReadOnlyObjectProperty<Route> route() {
        return route;
    }

    /**
     * public getter of the elevation profile's property
     *
     * @return elevation profile's property itself in read only mode
     */
    public ReadOnlyObjectProperty<ElevationProfile> elevationProfile() {
        return elevationProfile;
    }
}