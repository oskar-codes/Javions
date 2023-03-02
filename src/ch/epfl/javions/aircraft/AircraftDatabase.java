package ch.epfl.javions.aircraft;

import java.io.*;
import java.net.URLDecoder;
import java.util.zip.ZipFile;
import static java.nio.charset.StandardCharsets.UTF_8;

public class AircraftDatabase {
    private final String fileName;
    public AircraftDatabase(String fileName) {
        this.fileName = fileName;
    }

    public AircraftData get(IcaoAddress address) throws IOException {
        int query = Integer.parseInt(address.string(), 16);
        try (ZipFile z = new ZipFile(URLDecoder.decode(fileName, UTF_8))) {
            InputStream s = z.getInputStream(z.getEntry(address.string().substring(4) + ".csv"));
            BufferedReader b = new BufferedReader(new InputStreamReader(s, UTF_8));
            String line;
            String current = "";
            while ((line = b.readLine()) != null) {
                if (Integer.parseInt(line.substring(0, 6), 16) > query) break;
                current = line;
            }
            String[] data = current.split(",", -1);
            if (current.startsWith(address.string())) return new AircraftData(
                    new AircraftRegistration(data[1]),
                    new AircraftTypeDesignator(data[2]),
                    data[3],
                    new AircraftDescription(data[4]),
                    WakeTurbulenceCategory.of(data[5])
            );
        }
        return null;
    }
}
