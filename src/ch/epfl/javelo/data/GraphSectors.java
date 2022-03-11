package ch.epfl.javelo.data;

import ch.epfl.javelo.projection.PointCh;

import java.nio.ByteBuffer;
import java.util.ArrayList;


/**
 * Recorded class intended to be used by the Graph class representing the JaVelo graph, which will be carried out in step 4
 *
 * @author Timofey Kreslo (310686)
 * @author Wei-En Hsieh (341271)
 */

// buffer = un tableau contenant la valeur des attributs de tous les secteurs (ยง2.2.3)
public record GraphSectors(ByteBuffer buffer) {

    private static int OFFSET_SECTOR = Short.BYTES + Integer.BYTES;


    /**
     * @param center   (PointCh)
     * @param distance (distance)
     * @return the list of all sectors having an intersection with the square centered at the given point and with
     * a side equal to twice (!) the given distance
     */
    public ArrayList<Sector> sectorsInArea(PointCh center, double distance) {
        ArrayList<Sector> sectorsInZone = new ArrayList();

        PointCh PointChDownLeft = new PointCh(center.e() - (distance / 2), center.n() - (distance / 2));
        PointCh PointChUpLeft = new PointCh(center.e() + (distance / 2), center.n() + (distance / 2));

        int xMin = (int) Math.floor(PointChDownLeft.e() / 2.73); // distances in metres
        int xMax = (int) Math.floor(PointChDownLeft.n() / 2.73);
        int yMin = (int) Math.floor(PointChUpLeft.e() / 1.73);
        int yMax = (int) Math.floor(PointChUpLeft.n() / 1.73);

        for (int x = xMin; x <= xMax; x++) {
            for (int y = yMin; y <= yMax; y++) {

                int sectorId = y * 128 + x;

                int firstNodeId = buffer().getInt(sectorId * OFFSET_SECTOR);
                int numberNodes = Short.toUnsignedInt(buffer().getShort(sectorId * OFFSET_SECTOR + Integer.BYTES));
                int lastNodeId = firstNodeId + numberNodes - 1;

                Sector sector = new Sector(firstNodeId, Short.toUnsignedInt(buffer.getShort(sectorId * OFFSET_SECTOR + Integer.BYTES)) + firstNodeId);

                Sector s = new Sector(firstNodeId, lastNodeId);


                sectorsInZone.add(s);
            }
        }
        return sectorsInZone;
    }

    /**
     * represents a sector
     */
    public record Sector(int startNodeId, int endNodeId) {
    }

}