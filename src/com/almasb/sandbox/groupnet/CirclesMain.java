package com.almasb.sandbox.groupnet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.application.Application;
import javafx.geometry.Bounds;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class CirclesMain extends Application {

    List<Vertex> vertices = new ArrayList<Vertex>();
    List<Edge> edges = new ArrayList<Edge>();
    List<NetGroup> groups = new ArrayList<NetGroup>();

    private Parent createContent() {
        Pane root = new Pane();
        root.setPrefSize(800, 600);

        vertices.addAll(Arrays.asList(new Vertex("A"),
                new Vertex("B"),
                new Vertex("C"),
                new Vertex("D"),
                new Vertex("E"),
                new Vertex("F"),
                new Vertex("G"),
                new Vertex("H"),
                new Vertex("I"),
                new Vertex("J"),
                new Vertex("K")));

        groups.add(new NetGroup(vertices.get(0), vertices.get(2)));
        groups.add(new NetGroup(vertices.get(3), vertices.get(4)));
        groups.add(new NetGroup(vertices.get(4), vertices.get(5), vertices.get(6)));

        for (Vertex v : vertices) {
            if (v.groups.size() > 1) {
                NetGroup g = v.groups.get(0);
                // TODO: generalize
                g.addIntersection(v.groups.get(1));
            }
        }

        root.getChildren().addAll(groups);
        return root;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Scene scene = new Scene(createContent());
        primaryStage.setScene(scene);
        primaryStage.show();

        NetGroup g0 = groups.get(0);
        NetGroup g1 = groups.get(1);
        NetGroup g2 = groups.get(2);

        g1.setTranslateX(400);
        g1.setTranslateY(400);
        g2.setTranslateY(400);

        Bounds bounds = g1.getBoundsInParent();

        double x = bounds.getMaxX() - 30 - 10;

        g2.setTranslateX(x);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
