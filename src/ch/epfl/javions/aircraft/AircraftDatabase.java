package ch.epfl.javions.aircraft;

import java.io.*;
import java.util.zip.ZipFile;
import static java.nio.charset.StandardCharsets.UTF_8;

public class AircraftDatabase {
    private final String fileName;
    public AircraftDatabase(String fileName) {
        this.fileName = getClass().getResource(fileName).getFile();
    }

    public AircraftData get(IcaoAddress address) throws IOException {
        int query = Integer.parseInt(address.str(), 16);
        try (ZipFile z = new ZipFile(fileName)) {
            InputStream s = z.getInputStream(z.getEntry(address.str().substring(4) + ".csv"));
            BufferedReader b = new BufferedReader(new InputStreamReader(s, UTF_8));
            String line = "";
            String current = "";
            while ((line = b.readLine()) != null) {
                if (Integer.parseInt(line.substring(0, 6), 16) > query) break;
                current = line;
            }
            String[] data = current.split(",");
            if (current.startsWith(address.str())) return new AircraftData(
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
