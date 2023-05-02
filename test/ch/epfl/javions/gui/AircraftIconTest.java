package ch.epfl.javions.gui;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;


public class AircraftIconTest extends Application {
     public static void main(String[] args) {
         launch(args);
     }

     @Override
    public void start(Stage primaryStage) {


         SVGPath path = new SVGPath();
         path.contentProperty().set(AircraftIcon.values()[0].svgPath());

         Group group = new Group(path);

         group.setLayoutX(100);
         group.setLayoutY(200);


//         int x = 100;
//         for (AircraftIcon icon : AircraftIcon.values()) {
//              ctx.beginPath();
//              ctx.setFill(Color.RED);
//              ctx.setStroke(Color.BLUE);
//              ctx.appendSVGPath("M %d %d".formatted(x, 100));
//              ctx.appendSVGPath(icon.svgPath());
//              ctx.fill();
//              ctx.stroke();
//              ctx.closePath();
//              x += 50;
//         }

//         Button button = new Button("Test");
//         button.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, e -> {
//             System.out.println("Test");
//         });

//         TextField field = new TextField();
//         field.setOnAction(e -> {
//             System.out.println(field.getText());
//         });

//         pane.getChildren().add(field);
//         pane.getChildren().add(button);


         Scene scene = new Scene(group, 400, 600);
         primaryStage.setScene(scene);
         primaryStage.show();

     }
}