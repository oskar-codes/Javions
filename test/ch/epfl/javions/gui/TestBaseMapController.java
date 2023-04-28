package ch.epfl.javions.gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.nio.file.Path;

public final class TestBaseMapController extends Application {
    public static void main(String[] args) { launch(args); }

    private Stage primaryStage;
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        Path tileCache = Path.of("tile-cache");
        TileManager tm =
                new TileManager(tileCache, "tile.openstreetmap.org");
        MapParameters mp =
                new MapParameters(17, 17_389_327, 11_867_430);
        BaseMapController bmc = new BaseMapController(tm, mp);
        BorderPane root = new BorderPane(bmc.pane());

        mp.zoomProperty().addListener((p, o, n) -> updateTitle(mp));
        mp.xMinProperty().addListener((p, o, n) -> updateTitle(mp));
        mp.yMinProperty().addListener((p, o, n) -> updateTitle(mp));
        updateTitle(mp);
        
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
    private void updateTitle(MapParameters mp) {
        primaryStage.setTitle("Javions - " + "[" + mp.zoomProperty().get() + ", " + mp.xMinProperty().get() + ", " + mp.yMinProperty().get() + "]");
    }
}