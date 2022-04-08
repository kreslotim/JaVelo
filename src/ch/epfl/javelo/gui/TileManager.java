package ch.epfl.javelo.gui;

import javafx.scene.image.Image;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * TileId, a record representing the identity of an OSM tile.
 * <p>
 * OSM Tiles Manager -
 * Gets the tiles from a tile server and store them in a memory cache and in a disk cache
 *
 * @author Tim Kreslo (310686)
 * @author Wei-En Hsieh (341271)
 */
public final class TileManager {
    private final Map<TileId, Image> cacheMemory = new LinkedHashMap<>(100);
    private final String tileServerName;
    private final Path pathDisk;

    /**
     * Default TileManager constructor
     *
     * @param pathDisk       path to the directory containing the disk cache (SSD Disk)
     * @param tileServerName Server's name of the tile
     */
    public TileManager(Path pathDisk, String tileServerName) {
        this.tileServerName = tileServerName;
        this.pathDisk = pathDisk;
    }


    /**
     * Returns the Image corresponding to the given tile identity.
     *
     * @param tileId tile's identity
     * @return image corresponding to the tile's identity.
     */
    public Image imageForTileAt(TileId tileId) throws IOException {
        if (!cacheMemory.containsKey(tileId)) {
            if (!Files.exists(Path.of(pathDisk + "/" + tileId.tileZoomLevel + "/" + tileId.tileX + "/" + tileId.tileY + ".png"))) {
                //use String.format()
                URL u = new URL("https://" + tileServerName + "/" + tileId.tileZoomLevel + "/" + tileId.tileX + "/" + tileId.tileY + ".png");
                URLConnection c = u.openConnection();
                c.setRequestProperty("User-Agent", "JaVelo");
                try (InputStream i = c.getInputStream();
                     FileOutputStream fileOutputStream = new FileOutputStream(
                             pathDisk + "/" + tileId.tileZoomLevel + "/" + tileId.tileX + "/" + tileId.tileY + ".png")) {
                    Files.createDirectories(Path.of(pathDisk + "/" + tileId.tileZoomLevel + "/" + tileId.tileX));

                    if (cacheMemory.size() > 99) {
                        Iterator<TileId> iterator = cacheMemory.keySet().iterator();
                        iterator.remove();
                    }
                    cacheMemory.put(tileId, new Image(i));
                    i.transferTo(fileOutputStream);
                    //stocking memory and disk
                }
            }
        }

        try (FileInputStream fileInputStream = new FileInputStream(String.valueOf(tileId))) {
            return new Image(fileInputStream);
        }
    }

    /**
     * TileId, a record representing OSM tileId.
     */
    public record TileId(int tileZoomLevel, int tileX, int tileY) {

        /**
         * Returns true if & only if the given arguments form a valid tile identity
         *
         * @param tileZoomLevel Zoom level of the Tile
         * @param tileX         index X of the Tile
         * @param tileY         index Y of the Tile
         * @return true iff the given Tile's id is valid.
         */
        public static boolean isValid(int tileZoomLevel, int tileX, int tileY) {
            return tileX >= 0 && tileY >= 0 && tileX <= 1 << tileZoomLevel && tileY <= 1 << tileZoomLevel;
        }
    }
}