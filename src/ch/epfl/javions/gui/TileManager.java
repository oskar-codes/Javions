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

    // TODO: check if the least recently used tile is removed from the cache
    private final LinkedHashMap<TileId, Image> cache = new LinkedHashMap<>(100, 1, true);

    /**
     * Constructs a {@code TileManager} with the given disk path and server domain.
     * @param diskPath - the path to the cache on the disk
     * @param serverDomain - the domain of the server to download the tiles from
     */
    public TileManager(Path diskPath, String serverDomain) {
        this.diskPath = diskPath;
        this.serverDomain = serverDomain;
    }

    /**
     * A record representing the id of a tile.
     * @param zoom - the zoom level
     * @param x - the x coordinate
     * @param y - the y coordinate
     */
    record TileId(int zoom, int x, int y) {
        public static boolean isValid(int zoom, int x, int y) {
            return zoom >= 6 && zoom <= 19 && x >= 0 && x < Math.pow(4, zoom) && y >= 0 && y < Math.pow(4, zoom);
        }
    }

    /**
     * Returns the image for the tile at the given tile id. If the image is not in the cache, it is downloaded from the server.
     * @param tileId - the tile id
     * @return the image for the tile at the given tile id
     */
    public Image imageForTileAt(TileId tileId) {
        if (!TileId.isValid(tileId.zoom, tileId.x, tileId.y)) {
            throw new IllegalArgumentException("Invalid tile id");
        }
        // Retreive the image from the cache if it is present
        if (cache.containsKey(tileId)) {
            return cache.get(tileId);
        }

        // Retreive the image from the disk if it is present
        Path path = Path.of(diskPath.toString(), String.valueOf(tileId.zoom()), String.valueOf(tileId.x()));
        Path fullPath = Path.of(path.toString(), tileId.y() + ".png");
        if (Files.exists(fullPath)) {
            Image image = new Image(fullPath.toUri().toString());
            cache.put(tileId, image);
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
                cache.put(tileId, image);
                return image;
            }
        } catch (IOException e) {
            return null;
        }
    }
}