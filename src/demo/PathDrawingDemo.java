package demo;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import javafx.animation.Animation;
import javafx.animation.PathTransition;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.stage.Stage;
import javafx.util.Duration;


public class PathDrawingDemo extends Application
{

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        final Pane root = new Pane();
        final Path path = new Path();
        root.getChildren().addAll(path);
        primaryStage.setScene(new Scene(root, 400, 260));
        primaryStage.show();

        path.getElements().addAll(
                new MoveTo(20, 20),
                new CubicCurveTo(380, 0, 380, 120, 200, 120),
                new CubicCurveTo(0, 120, 0, 240, 380, 240)
        );
        path.setFill(null);
        path.setStroke(Color.RED);
        path.setStrokeWidth(2);

        final Animation path_animation = clipAnimation(path);
        path_animation.play();
    }

    private Animation clipAnimation(Path path)
    {
        final Pane clip = new Pane();
        path.clipProperty().set(clip);

        final Circle pen = new Circle(0, 0, 2);

        ChangeListener pen_Listener = new ChangeListener()
        {
            @Override
            public void changed(ObservableValue observableValue, Object o1, Object o2)
            {
                Circle clip_eraser = new Circle(pen.getTranslateX(), pen.getTranslateY(), pen.getRadius());
                clip.getChildren().add(clip_eraser);
            }
        };

        pen.translateXProperty().addListener(pen_Listener);
        pen.translateYProperty().addListener(pen_Listener);
        pen.rotateProperty().addListener(pen_Listener);

        PathTransition pathTransition = new PathTransition(Duration.seconds(15), path, pen);
        pathTransition.setOnFinished(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent t)
            {
                path.setClip(null);
                clip.getChildren().clear();
            }
        });

        return pathTransition;
    }

    public static void main(String[] args)
    {
        launch(args);
    }
}
