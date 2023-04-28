package ch.epfl.javions.aircraft;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.zip.ZipFile;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Instantiable class that represents a database of aircrafts.
 *
 * @author Eddy Rashed (360667)
 * @author Oskar Zanota (361595)
 */
public class AircraftDatabase {
    // The absolute path of the zip file containing the database
    private final String fileName;
    private final static String SEPARATOR = ",";

    /**
     * Constructs an AircraftDatabase object with the given file name.
     *
     * @param fileName the absolute path of the zip file containing the database
     */
    public AircraftDatabase(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Returns the AircraftData corresponding to the given IcaoAddress.
     *
     * @param address the IcaoAddress of the aircraft
     * @return the AircraftData corresponding to the given IcaoAddress, found in the database. Returns null if the address is not found.
     * @throws IOException if the file cannot be read
     */
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
            String[] data = current.split(SEPARATOR, -1);
            if (current.startsWith(address.string()))
                return new AircraftData(
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
