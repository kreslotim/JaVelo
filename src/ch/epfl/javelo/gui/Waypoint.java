package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointCh;

/**
 * Waypoint on the map
 *
 * @author Tim Kreslo (310686)
 * @author Wei-En Hsieh (341271)
 */
public record Waypoint(PointCh positionWaypoint, int nearestNodeId) {}
