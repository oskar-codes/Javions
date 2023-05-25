package ch.epfl.javions.gui;

import ch.epfl.javions.adsb.AircraftStateManager;
import ch.epfl.javions.adsb.Message;
import ch.epfl.javions.adsb.MessageParser;
import ch.epfl.javions.adsb.RawMessage;
import ch.epfl.javions.aircraft.AircraftDatabase;
import ch.epfl.javions.demodulation.AdsbDemodulator;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.nio.file.Path;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Main class of the Javions application.
 * @author Oskar Zanota (361595)
 * @author Eddy Rashed (360667)
 */
public class Main extends Application {
    private final ConcurrentLinkedQueue<RawMessage> messageQueue = new ConcurrentLinkedQueue<>();

    /**
     * Main method of the Javions application.
     * @param args the command line arguments. The first argument is the path to a file containing messages.
     *             If omitted, messages are read from stdin.
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Start method of the JavaFX application.
     * @param stage the primary stage for this application, onto which
     * the application scene can be set.
     * @throws Exception if an error occurs during the execution of the application.
     */
    @Override
    public void start(Stage stage) throws Exception {

        // Retrieve the aircraft database
        URL u = getClass().getResource("/aircraft.zip");
        assert u != null;
        Path p = Path.of(u.toURI());
        AircraftDatabase db = new AircraftDatabase(p.toString());

        // Set up the map
        Path tileCache = Path.of("tile-cache");
        TileManager tm =
                new TileManager(tileCache, "tile.openstreetmap.org");
        MapParameters mp =
                new MapParameters(8, 33530, 23070);
        BaseMapController bmc = new BaseMapController(tm, mp);

        ObjectProperty<ObservableAircraftState> sap =
                new SimpleObjectProperty<>();
        AircraftStateManager asm = new AircraftStateManager(db);
        AircraftController ac =
                new AircraftController(mp, asm.states(), sap);
        StackPane map = new StackPane(bmc.pane(), ac.pane());

        AircraftTableController atc = new AircraftTableController(asm.states(), sap);
        StatusLineController slc = new StatusLineController();

        slc.aircraftCountProperty().bind(Bindings.size(asm.states()));

        atc.setOnDoubleClick(e -> {
            bmc.centerOn(e.getPosition());
            sap.set(e);
        });

        BorderPane data = new BorderPane();
        data.setCenter(atc.pane());
        data.setTop(slc.pane());

        SplitPane root = new SplitPane(map, data);
        root.setOrientation(Orientation.VERTICAL);

        // Set up the stage
        stage.setMinWidth(800);
        stage.setMinHeight(600);
        stage.setTitle("Javions");
        stage.setScene(new Scene(root));
        stage.show();

        boolean isFile = !getParameters().getRaw().isEmpty();

        // Decoder thread for reading messages from a file or stdin (radio) concurrently
        Thread decoder = new Thread(() -> {
            // Read from file
            if (isFile) {
                String fileName = getParameters().getRaw().get(0);
                try (DataInputStream s = new DataInputStream(new BufferedInputStream(new FileInputStream(fileName)))) {
                    byte[] bytes = new byte[RawMessage.LENGTH];
                    long lastTimeStampNs = 0;
                    while (true) {
                        long timeStampNs = s.readLong();
                        int bytesRead = s.readNBytes(bytes, 0, bytes.length);
                        assert bytesRead == RawMessage.LENGTH;
                        RawMessage raw = RawMessage.of(timeStampNs, bytes);
                        if (raw == null) continue;
                        int timeToWait = (int) ((timeStampNs - lastTimeStampNs) / 1e6);
                        if (timeToWait > 0) Thread.sleep(timeToWait);
                        messageQueue.add(raw);
                        lastTimeStampNs = timeStampNs;
                    }
                } catch (IOException | InterruptedException ignored) {}
            } else {
                // Read from stdin
                try (DataInputStream s = new DataInputStream(new BufferedInputStream(System.in))) {
                    AdsbDemodulator demodulator = new AdsbDemodulator(s);
                    RawMessage r;
                    while ((r = demodulator.nextMessage()) != null) {
                        messageQueue.add(r);
                    }
                } catch (IOException ignored) {}
            }
        });
        decoder.setDaemon(true);
        decoder.start();

        // Animation timer for updating the aircraft states
        new AnimationTimer() {
            private double previous = Double.NEGATIVE_INFINITY;

            @Override
            public void handle(long nowNs) {
                try {
                    if (messageQueue.isEmpty()) return;
                    Message m = MessageParser.parse(messageQueue.poll());
                    if (m == null) return;
                    slc.messageCountProperty().set(slc.messageCountProperty().get() + 1);
                    asm.updateWithMessage(m);

                    // Purge the aircraft states every second
                    if (nowNs / 1e9 - previous / 1e9 >= 1) {
                        previous = nowNs;
                        asm.purge(m);
                    }
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }
        }.start();
    }
}