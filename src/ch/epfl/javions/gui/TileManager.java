package ch.epfl.javions.gui;

import javafx.scene.image.Image;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;

public final class TileManager {
    private final Path diskPath;
    private final String serverDomain;
    private final LinkedHashMap<TileId, Image> cache = new LinkedHashMap<>(100, 1, true);
    public TileManager(Path diskPath, String serverDomain) {
        this.diskPath = diskPath;
        this.serverDomain = serverDomain;
    }

    record TileId(int zoom, int x, int y) {
        public static boolean isValid(int zoom, int x, int y) {
            return zoom >= 6 && zoom <= 19 && x >= 0 && x < Math.pow(4, zoom) && y >= 0 && y < Math.pow(4, zoom);
        }
    }

    public Image imageForTileAt(TileId tileId) {
        if (!TileId.isValid(tileId.zoom, tileId.x, tileId.y)) {
            throw new IllegalArgumentException("Invalid tile id");
        }
        if (cache.containsKey(tileId)) {
            return cache.get(tileId);
        }
        Path path = Path.of(diskPath.toString(), String.valueOf(tileId.zoom()), String.valueOf(tileId.x()));
        Path fullPath = Path.of(path.toString(), tileId.y() + ".png");
        if (Files.exists(fullPath)) {
            Image image = new Image(fullPath.toUri().toString());
            cache.put(tileId, image);
            return image;
        }
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