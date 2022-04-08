package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointCh;

/**
 * Waypoint, a record representing a waypoint.
 * <p>
 * Waypoint does not have any public methods other than those defined automatically by Java for records.
 */
public record Waypoint(PointCh pointCh, int nearestNodeId) {
}