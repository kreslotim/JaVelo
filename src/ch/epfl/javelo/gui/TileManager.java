package ch.epfl.javelo.gui;

import javafx.scene.image.Image;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * OSM Tiles Manager -
 * Gets the tiles from a tile server and store them in a memory cache and in a disk cache
 *
 * @author Tim Kreslo (310686)
 * @author Wei-En Hsieh (341271)
 */
public final class TileManager {
    private final int MAX_ENTRIES = 100;
    private final Map<TileId, Image> cacheMemory = new LinkedHashMap<>(MAX_ENTRIES);
    private final String tileServerName;
    private final Path pathToDisk;

    /**
     * Default TileManager constructor
     *
     * @param pathToDisk       path to the directory containing the disk cache (SSD Disk)
     * @param tileServerName Server's name of the tile
     */
    public TileManager(Path pathToDisk, String tileServerName) {
        this.tileServerName = tileServerName;
        this.pathToDisk = pathToDisk;
    }


    /**
     * Returns the Image corresponding to the given tile identity.
     *
     * @param tileId tile's identity
     * @return image corresponding to the tile's identity.
     */
    public Image imageForTileAt(TileId tileId) throws IOException {

        String zoomXY_PNG = String.format("%d/%d/%d.png",tileId.tileZoomLevel, tileId.tileX, tileId.tileY);
        String url = String.format("https://%s/%s",tileServerName, zoomXY_PNG);
        Path pathToImageDirectory = Path.of(String.format("%s/%d/%d",pathToDisk, tileId.tileZoomLevel, tileId.tileX));

        if (!cacheMemory.containsKey(tileId)) {
            if (!Files.exists(Path.of(zoomXY_PNG))) {
                transferFromServerToDisk(zoomXY_PNG, url, pathToImageDirectory);
            }
            transferFromDiskToCache(zoomXY_PNG, tileId);
        }
        return cacheMemory.get(tileId);
    }


    /**
     * Auxiliary (private) method, that transfers data from server (with given URL)
     *
     * @param zoomXY_PNG (String) path to PNG file
     * @param url (String) server's domain
     * @param pathToImageDirectory (Path) path to directory containing the PNG file
     * @throws IOException if the provided URL is not valid
     */
    private void transferFromServerToDisk(String zoomXY_PNG, String url, Path pathToImageDirectory) throws IOException {
        URL u = new URL(url);
        URLConnection c = u.openConnection();
        c.setRequestProperty("User-Agent", "JaVelo");
        Files.createDirectories(pathToImageDirectory);

        //stock to SSD disk
        try (InputStream i = c.getInputStream();
             FileOutputStream fileOutputStream = new FileOutputStream(zoomXY_PNG)) {
            i.transferTo(fileOutputStream);
        }
    }


    /**
     * Auxiliary (private) method, that transfers data from server (with given URL)
     *
     * @param zoomXY_PNG (String) path to PNG file
     * @param tileId (TileId) given tile's identity
     * @throws IOException if the provided path to directory is not valid
     */
    private void transferFromDiskToCache(String zoomXY_PNG, TileId tileId) throws IOException {
        try (FileInputStream fileInputStream = new FileInputStream(zoomXY_PNG)) {

            //stock to memory cache
            if (cacheMemory.size() >= MAX_ENTRIES) {  // memory cache must contain a maximum of 100 images
                Iterator<TileId> iterator = cacheMemory.keySet().iterator();
                cacheMemory.remove(iterator.next());
            }

            cacheMemory.put(tileId, new Image(fileInputStream));
        }
    }


    /**
     * TileId, a record representing OSM tileId.
     */
    public record TileId(int tileZoomLevel, int tileX, int tileY) {

        /**
         * Compact TileId constructor that checks if the given tileId's arguments are valid
         *
         * @param tileZoomLevel Zoom level for a given tileId
         * @param tileX Index X of the given tileId
         * @param tileY index Y of the given tileId
         */
        public TileId {
            if (!isValid(tileZoomLevel, tileX, tileY)) throw new IllegalArgumentException();
        }

        /**
         * Returns true if & only if the given arguments form a valid tile identity
         *
         * @param tileZoomLevel Zoom level of the Tile
         * @param tileX         index X of the Tile
         * @param tileY         index Y of the Tile
         * @return true iff the given Tile's id is valid.
         */
        public static boolean isValid(int tileZoomLevel, int tileX, int tileY) {
            return  (0 <= tileX && tileX <= (1 << tileZoomLevel)) &&
                    (0 <= tileY && tileY <= (1 << tileZoomLevel)); // 2^tileZoomLevel
        }
    }
}