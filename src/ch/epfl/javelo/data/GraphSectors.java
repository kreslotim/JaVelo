package ch.epfl.javelo.data;

import ch.epfl.javelo.projection.PointCh;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * Recorded class intended to be used by the Graph class representing the JaVelo graph, which will be carried out in step 4
 *
 * @author Timofey Kreslo (310686)
 * @author Wei-En Hsieh (341271)
 */

// buffer = un tableau contenant la valeur des attributs de tous les secteurs (§2.2.3)
public record GraphSectors(ByteBuffer buffer) {


    /* Sector is a nested record class of GraphSectors */

    /**
     * represents a sector
     */
    private record Sector(int startNodeId, int endNodeId) {


        /**
         * @param center   (PointCh)
         * @param distance (distance)
         * @return the list of all sectors having an intersection with the square centered at the given point and with
         * a side equal to twice (!) the given distance
         */
        public List<Sector> sectorsInArea(PointCh center, double distance) {
            Short.toUnsignedInt();
        }
    }


}
