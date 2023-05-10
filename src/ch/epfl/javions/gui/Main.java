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
import java.util.Date;
import java.util.concurrent.ConcurrentLinkedQueue;
public class Main extends Application {
    private final ConcurrentLinkedQueue<RawMessage> messageQueue = new ConcurrentLinkedQueue<>();
    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage stage) throws Exception {

        URL u = getClass().getResource("/aircraft.zip");
        assert u != null;
        Path p = Path.of(u.toURI());
        AircraftDatabase db = new AircraftDatabase(p.toString());

        Path tileCache = Path.of("tile-cache");
        TileManager tm =
                new TileManager(tileCache, "tile.openstreetmap.org");
        MapParameters mp =
                new MapParameters(8, 33530, 23070);
        BaseMapController bmc = new BaseMapController(tm, mp);


        AircraftStateManager asm = new AircraftStateManager(db);
        ObjectProperty<ObservableAircraftState> sap =
                new SimpleObjectProperty<>();
        AircraftController ac =
                new AircraftController(mp, asm.states(), sap);
        StackPane map = new StackPane(bmc.pane(), ac.pane());

        AircraftTableController atc = new AircraftTableController(asm.states(), sap);
        StatusLineController slc = new StatusLineController();

        slc.aircraftCountProperty().bind(Bindings.size(asm.states()));

        atc.setOnDoubleClick(e -> {
            bmc.centerOn(e.getPosition());
            sap.set(e);
            bmc.pane().requestFocus();
        });

        BorderPane data = new BorderPane();
        data.setCenter(atc.pane());
        data.setTop(slc.pane());

        SplitPane root = new SplitPane(map, data);
        root.setOrientation(Orientation.VERTICAL);

        stage.setMinWidth(800);
        stage.setMinHeight(600);
//        stage.setFullScreenExitHint("");
//        stage.setFullScreen(true);
        stage.setTitle("Javions");
        stage.setScene(new Scene(root));
        stage.show();

        boolean isFile = !getParameters().getRaw().isEmpty();

        Thread decoder = new Thread(() -> {
            if (isFile) {
                String fileName = getParameters().getRaw().get(0);
                try (DataInputStream s = new DataInputStream(new BufferedInputStream(new FileInputStream(fileName)))) {
                    byte[] bytes = new byte[RawMessage.LENGTH];
                    while (true) {
                        long timeStampNs = s.readLong();
                        int bytesRead = s.readNBytes(bytes, 0, bytes.length);
                        assert bytesRead == RawMessage.LENGTH;
                        RawMessage raw = RawMessage.of(timeStampNs, bytes);
                        if (raw == null) continue;
                        Date now = new Date();
                        // TODO: fix this
                        int timeToWait = (int) (timeStampNs - now.getTime() * 1e6);
                        if (timeToWait > 0) Thread.sleep(timeToWait / 1000000, timeToWait % 1000000);
                        messageQueue.add(raw);
                    }
                } catch (IOException | InterruptedException ignored) {}
            } else {
                try (DataInputStream s = new DataInputStream(new BufferedInputStream(System.in))) {
                    AdsbDemodulator demodulator = new AdsbDemodulator(s);
                    RawMessage r;
                    while ((r = demodulator.nextMessage()) != null) {
                        messageQueue.add(r);
                    }
                } catch (IOException ignored) {}
            }
        });
        decoder.start();

        new AnimationTimer() {
            @Override
            public void handle(long now) {
                try {
                    for (int i = 0; i < 10; i += 1) {
                        if (messageQueue.isEmpty()) return;
                        Message m = MessageParser.parse(messageQueue.poll());
                        if (m == null) continue;
                        slc.messageCountProperty().set(slc.messageCountProperty().get() + 1);
                        asm.updateWithMessage(m);
                    }
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }
        }.start();
    }
}