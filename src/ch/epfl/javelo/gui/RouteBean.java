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
    private final static int MAX_ENTRIES = 100;
    private final static int MAX_STEP_LENGTH = 5;
    private final RouteComputer routeComputer;
    private final List<Route> segments = new ArrayList<>();
    private final Map<String, Route> cacheRoute = new LinkedHashMap<>(MAX_ENTRIES);
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

                if (cacheRoute.containsKey(pairNode.toString())) {       //if the segment has already been computed
                    Route segment = cacheRoute.get(pairNode.toString()); //get it from the cache
                    segments.add(segment);
                }
                else {                                             //otherwise compute segment if it exists
                                                                   //                     between two nodes
                    Route segment = null;                          //The segment between two nodes does not exists
                    if (!pairNode.getKey().equals(pairNode.getValue())) {          // if these two nodes are equal
                        segment = routeComputer.bestRouteBetween(pairNode.getKey(), pairNode.getValue());
                    }

                    if (segment == null) {                         //if at least one segment composing the route
                        segmentIsNull = true;                      //does not exist  -> the route must not exist
                        break;                                     //                -> break the loop
                    }

                    else {
                        segments.add(segment);                        //otherwise continue building the route
                        cacheRoute.put(pairNode.toString(), segment); //and save the computed segment into the cache
                    }
                }
            }

            if (segments.isEmpty() || segmentIsNull) {            //if the route does not exist,
                elevationProfileProperty.set(null);               //or an empty segment has been found
                routeProperty.set(null);                          // -> appropriate properties contain null object
            }
            else {
                MultiRoute multiRoute = new MultiRoute(segments); //otherwise build the route
                                                                  //composed of existing segments
                elevationProfileProperty.set(ElevationProfileComputer.elevationProfile(multiRoute, MAX_STEP_LENGTH));
                routeProperty.set(multiRoute);
            }
        }
        else {                                                    //if there are less than 2 waypoints on the map
            routeProperty.set(null);                              // -> no route can exist
            elevationProfileProperty.set(null);                   // -> no elevation profile can exist
        }
    }


    /**
     * Public getter of the waypoints list
     *
     * @return observable list of all waypoints
     */
    public ObservableList<Waypoint> getWaypoints() {
        return waypoints;
    }

    /**
     * Public getter of highlighted position
     *
     * @return double value of the highlighted position
     */
    public double highlightedPosition() {
        return highlightedPositionProperty.get();
    }

    /**
     * Public getter of the highlighted position's property
     *
     * @return highlighted position's property itself
     */
    public DoubleProperty highlightedPositionProperty() {
        return highlightedPositionProperty;
    }

    /**
     * Public setter for highlighted position
     *
     * @param highlightedPositionValue double value of the highlighted position
     */
    public void setHighlightedPositionProperty(double highlightedPositionValue) {
        highlightedPositionProperty.set(highlightedPositionValue);
    }

    /**
     * Public getter of the route's property
     *
     * @return route's property itself in read only mode
     */
    public ReadOnlyObjectProperty<Route> routeProperty() {
        return routeProperty;
    }

    /**
     * Public getter of the elevation profile's property
     *
     * @return elevation profile's property itself in read only mode
     */
    public ReadOnlyObjectProperty<ElevationProfile> elevationProfileProperty() {
        return elevationProfileProperty;
    }


    /**
     * Returns the index of the segment containing the given position along the route,
     * ignoring empty segments
     *
     * @param  position (double) position along the route
     * @return index of the segment on the route
     */
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