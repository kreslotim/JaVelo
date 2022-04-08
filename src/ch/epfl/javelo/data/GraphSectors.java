package ch.epfl.javelo.data;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Recorded class representing the array of the 16'384 sectors (covering Switzerland) in JaVelo.
 *
 * @author Timofey Kreslo (310686)
 * @author Wei-En Hsieh (341271)
 */

public record GraphSectors(ByteBuffer buffer) {

    /* Sector's attributes are distributed over 48 bits in total = 4 Bytes + 2 Bytes */
    private final static int SECTOR_PITCH = Integer.BYTES + Short.BYTES;
    private final static int OFFSET_SECTORS_PER_LINE = 128;
    private final static double sectorX = SwissBounds.WIDTH / 128.; // Horizontal component of a sector's dimensions
    private final static double sectorY = SwissBounds.HEIGHT / 128.; // Vertical component of a sector's dimensions

    /**
     * Returns a list of sectors that intersect with the square centered at a given Swiss point (PointCh).
     *
     * @param center (PointCh) of the square (zone)
     * @param distance distance equals half of the square. (Can be interpreted as a radius of the zone)
     * @return list of all sectors having an intersection with the square centered at the given point (PointCh).
     */
    public List<Sector> sectorsInArea(PointCh center, double distance) {

        ArrayList<Sector> sectorsInZone = new ArrayList<>();

        /*
          Bounds of the square centered on the given point,
          set to the bounds of Switzerland (maximum square's dimensions)
         */
        double minE = Math2.clamp(SwissBounds.MIN_E, center.e() - distance, SwissBounds.MAX_E);
        double maxE = Math2.clamp(SwissBounds.MIN_E, center.e() + distance, SwissBounds.MAX_E);
        double minN = Math2.clamp(SwissBounds.MIN_N, center.n() - distance, SwissBounds.MAX_N);
        double maxN = Math2.clamp(SwissBounds.MIN_N, center.n() + distance, SwissBounds.MAX_N);

        /*
          Bounds of the given GraphSector buffer,
          set between 0 and 127 (number of sectors per line/column -1, for reaching ID)
         */
        // distances in meters
        int xMin = Math2.clamp(0, (int) Math.floor((minE - SwissBounds.MIN_E) / sectorX), 127);
        int xMax = Math2.clamp(0, (int) Math.floor((maxE - SwissBounds.MIN_E) / sectorX), 127);
        int yMin = Math2.clamp(0, (int) Math.floor((minN - SwissBounds.MIN_N) / sectorY), 127);
        int yMax = Math2.clamp(0, (int) Math.floor((maxN - SwissBounds.MIN_N) / sectorY), 127);

        for (int y = yMin; y <= yMax; y++) {
            for (int x = xMin; x <= xMax; x++) {

                int sectorId = y * OFFSET_SECTORS_PER_LINE + x;

                int firstNodeId = buffer.getInt(sectorId * SECTOR_PITCH);
                int numberNodes = Short.toUnsignedInt(buffer.getShort(sectorId * SECTOR_PITCH + Integer.BYTES));
                int lastNodeId = firstNodeId + numberNodes;

                Sector sector = new Sector(firstNodeId, lastNodeId);

                sectorsInZone.add(sector);
            }
        }
        return sectorsInZone;
    }

    /**
     * Recorded class representing a sector, built with starting nodeID and ending nodeID
     */
    public record Sector(int startNodeId, int endNodeId) {}
}