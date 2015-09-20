package com.almasb.java.framework.demo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.almasb.common.graphics.Vector2D;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.effect.Light.Spot;
import javafx.scene.effect.Lighting;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

public class LightningDemo extends Application {

    Group group = new Group();

    private Parent createContent() {
        Pane root = new Pane();
        root.setPrefSize(800, 600);

        Spot light = new Spot();

        light.setZ(80);
        light.setPointsAtX(0);
        light.setPointsAtY(0);
        light.setPointsAtZ(0);
        light.setSpecularExponent(2);

        Lighting lighting = new Lighting();
        lighting.setLight(light);
        lighting.setSurfaceScale(5.0);

        Rectangle rect1 = new Rectangle(100, 100);
        rect1.setTranslateX(200);
        rect1.setTranslateY(200);
        rect1.setEffect(lighting);

        Rectangle rect2 = new Rectangle(150, 150);
        rect2.setTranslateX(400);
        rect2.setTranslateY(200);
        rect2.setEffect(lighting);

        Rectangle bg = new Rectangle(800, 600);
        root.setOnMouseClicked(event -> {
            group.getChildren().clear();

            light.setX(event.getSceneX());
            light.setY(event.getSceneY());

            List<Line> lines = createBolt(new Vector2D(0, 0), new Vector2D((float)event.getSceneX(), (float)event.getSceneY()), 1.5f);

            for (Line l : lines) {
                l.setStroke(Color.AQUA);

                DropShadow shadow = new DropShadow(20, Color.BLUE);
                shadow.setInput(new Glow(0.7));

                l.setEffect(shadow);
                group.getChildren().add(l);
            }

            FadeTransition ft = new FadeTransition(Duration.seconds(0.5), group);
            ft.setFromValue(0);
            ft.setToValue(1);
            ft.setAutoReverse(true);
            ft.setCycleCount(2);
            ft.setOnFinished(evt -> {
                rect1.setEffect(null);
                rect1.setFill(Color.BLACK);
            });
            ft.play();

            rect1.setFill(Color.STEELBLUE);
            rect1.setEffect(lighting);
        });





        root.getChildren().addAll(bg, rect1, group);
        return root;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Scene scene = new Scene(createContent());
        primaryStage.setTitle("Lightning Demo");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private List<Line> createBolt(Vector2D src, Vector2D dst, float thickness) {
        ArrayList<Line> results = new ArrayList<Line>();

        Vector2D tangent = Vector2D.subtract(dst, src);
        Vector2D normal = Vector2D.normalize(new Vector2D(tangent.getY(), -tangent.getX()));

        float length = tangent.getMagnitude();

        ArrayList<Float> positions = new ArrayList<Float>();
        positions.add(0.0f);

        for (int i = 0; i < length / 4; i++)
            positions.add((float)Math.random());

        Collections.sort(positions);

        float sway = 80;
        float jaggedness = 1 / sway;

        Vector2D prevPoint = src;
        float prevDisplacement = 0;
        for (int i = 1; i < positions.size(); i++) {
            float pos = positions.get(i);

            // used to prevent sharp angles by ensuring very close positions also have small perpendicular variation.
            float scale = (length * jaggedness) * (pos - positions.get(i - 1));

            // defines an envelope. Points near the middle of the bolt can be further from the central line.
            float envelope = pos > 0.95f ? 20 * (1 - pos) : 1;

            float displacement = (float)(sway * (Math.random() * 2 - 1));
            displacement -= (displacement - prevDisplacement) * (1 - scale);
            displacement *= envelope;

            Vector2D point = Vector2D.add(Vector2D.add(src, Vector2D.multiplyByScalar(tangent, pos)), Vector2D.multiplyByScalar(normal, displacement));

            Line line = new Line(prevPoint.getX(), prevPoint.getY(), point.getX(), point.getY());
            line.setStrokeWidth(thickness);

            results.add(line);
            prevPoint = point;
            prevDisplacement = displacement;
        }

        Line line = new Line(prevPoint.getX(), prevPoint.getY(), dst.getX(), dst.getY());
        line.setStrokeWidth(thickness);
        results.add(line);

        return results;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
