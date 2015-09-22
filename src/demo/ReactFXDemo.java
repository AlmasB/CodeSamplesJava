package demo;

import org.reactfx.EventStream;
import org.reactfx.EventStreams;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class ReactFXDemo extends Application {

    private Parent createContent() {
        Pane root = new Pane();
        root.setPrefSize(800, 600);

        Rectangle rect = new Rectangle(100, 50);
        rect.setTranslateX(400);
        rect.setTranslateY(300);

        EventStream<MouseEvent> clicks = EventStreams.eventsOf(rect, MouseEvent.MOUSE_ENTERED);
        clicks.subscribe(System.out::println);

        root.getChildren().add(rect);
        return root;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Scene scene = new Scene(createContent());
        primaryStage.setTitle("Tutorial");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
