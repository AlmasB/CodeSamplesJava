package com.almasb.sandbox.groupnet;

import java.util.Random;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;

public class TestMain extends Application {

    private int W = 800, H = 600;

    private Group circles = new Group();
    private Random random = new Random();

    private Parent createContent() {
        Pane root = new Pane();
        root.setPrefSize(W, H);




        root.getChildren().add(circles);
        return root;
    }

    private void addCircle(double radius) {
        System.out.println("Add circle");

        ObservableList<Node> children = circles.getChildren();
        if (children.size() == 0) {
            Node circle = createCircle(radius);
            circle.setTranslateX(W / 2);
            circle.setTranslateY(H / 2);
            children.add(circle);
        }
        else {
            Node circle = createCircle(radius);
            children.add(circle);
            position(circle, children);
        }
    }

    private void position(Node circle, ObservableList<Node> children) {
        for (int y = 100; y < H; y++) {
            for (int x = 200; x < W; x++) {
                circle.setTranslateX(x);
                circle.setTranslateY(y);
                if (intersects(circle, children))
                    return;
            }
        }
    }

    private boolean intersects(Node circle, ObservableList<Node> children) {
        for (Node n : children) {
            if (n == circle) continue;

            if (!n.getBoundsInParent().intersects(circle.getBoundsInParent())) {
                return false;
            }

            Bounds bounds = Shape.intersect((Shape)n, (Shape)circle).getBoundsInParent();

            System.out.println(bounds.getWidth() + " " + bounds.getHeight());


            if (bounds.getWidth() < 20 || bounds.getWidth() > 80
                    || bounds.getHeight() < 20 || bounds.getHeight() > 80) {
                return false;
            }
        }

        return true;
    }

    private Node createCircle(double radius) {
        Rectangle circle = new Rectangle(radius, radius);
        circle.setFill(Color.TRANSPARENT);
        circle.setStroke(Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
        return circle;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Scene scene = new Scene(createContent());
        scene.setOnKeyTyped(event -> addCircle(100));
        primaryStage.setTitle("Circle Test");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
