package ch.epfl.javions.gui;

import javafx.scene.image.Image;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;

/**
 * A class that manages the tiles of the map.
 * The tiles are stored in a cache on the disk.
 * The cache is located in the directory {@code diskPath}.
 * The tiles are downloaded from the server {@code serverDomain}.
 * The tiles are stored in the cache in the following format:
 * {@code diskPath/zoom/x/y.png}.
 * The tiles are stored in a cache in memory.
 * The cache in memory is a {@code LinkedHashMap} with a maximum size of 100.
 * The tiles are stored in the cache in the format {@code (zoom, x, y) -> image}.
 *
 * @author Eddy Rashed (360667)
 * @author Oskar Zanota (361595)
 */
public final class TileManager {
    private final Path diskPath;
    private final String serverDomain;
    /**
     * The maximum size of the cache in memory.
     */
    private static final int MAX_CACHE_SIZE = 100;

    private final LinkedHashMap<TileId, Image> cache = new LinkedHashMap<>(MAX_CACHE_SIZE, 0.75f, true);

    /**
     * Constructs a {@code TileManager} with the given disk path and server domain.
     * @param diskPath the path to the cache on the disk
     * @param serverDomain the domain of the server to download the tiles from
     */
    public TileManager(Path diskPath, String serverDomain) {
        this.diskPath = diskPath;
        this.serverDomain = serverDomain;
    }

    /**
     * A record representing the id of a tile.
     * @param zoom the zoom level
     * @param x the x coordinate
     * @param y the y coordinate
     */
     record TileId(int zoom, int x, int y) {
        /**
         * Checks if the tile id is valid.
         * @param zoom the zoom level. Must be between 6 and 19 (inclusive).
         * @param x the x coordinate. Must be between 0 and 2^zoom - 1 (inclusive).
         * @param y the y coordinate. Must be between 0 and 2^zoom - 1 (inclusive).
         * @return true if the tile id is valid, false otherwise
         */
        public static boolean isValid(int zoom, int x, int y) {
            return zoom > 0 &&
                   x >= 0 && x < 1 << zoom &&
                   y >= 0 && y < 1 << zoom;
        }
    }

    /**
     * Returns the image for the tile at the given tile id. If the image is not in the cache, it is downloaded from the server.
     * @param tileId the tile id
     * @return the image for the tile at the given tile id
     */
    public Image imageForTileAt(TileId tileId) {
        if (!TileId.isValid(tileId.zoom, tileId.x, tileId.y)) {
            throw new IllegalArgumentException("Invalid tile id");
        }
        // Retrieve the image from the cache if it is present
        if (cache.containsKey(tileId)) {
            return cache.get(tileId);
        }

        // Retrieve the image from the disk if it is present
        Path path = Path.of(diskPath.toString(), String.valueOf(tileId.zoom()), String.valueOf(tileId.x()));
        Path fullPath = Path.of(path.toString(), tileId.y() + ".png");
        if (Files.exists(fullPath)) {
            Image image = new Image(fullPath.toUri().toString());
            addToCache(tileId, image);
            return image;
        }

        // Download the image from the server
        try {
            URL u = new URL("https://" + serverDomain + "/" + tileId.zoom() + "/" + tileId.x() + "/" + tileId.y() + ".png");
            URLConnection c = u.openConnection();
            c.setRequestProperty("User-Agent", "Javions");
            try (InputStream i = c.getInputStream()) {
                byte[] bytes = i.readAllBytes();
                Files.createDirectories(Paths.get(path.toString()));
                try (OutputStream writer = new FileOutputStream(fullPath.toString())) {
                    writer.write(bytes);
                }
                Image image = new Image(new ByteArrayInputStream(bytes));
                addToCache(tileId, image);
                return image;
            }
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Adds the given image to the cache. If the cache is full, the least recently used image is removed.
     * @param id the tile id
     * @param image the image
     */
    private void addToCache(TileId id, Image image) {
        cache.put(id, image);
        if (cache.size() > MAX_CACHE_SIZE) {
            cache.remove(cache.keySet().iterator().next());
        }
    }
}