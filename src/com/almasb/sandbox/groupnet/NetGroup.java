package com.almasb.sandbox.groupnet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class NetGroup extends Parent {

    public List<Vertex> elements = new ArrayList<Vertex>();
    public List<NetGroup> intersections = new ArrayList<NetGroup>();

    public NetGroup(Vertex... vertices) {
        elements.addAll(Arrays.asList(vertices));

        int i = 0;
        for (Vertex v : vertices) {
            v.groups.add(this);
            v.setTranslateX(20 + i * 40);
            v.setTranslateY(30);
            i++;
        }

        Rectangle bg = new Rectangle(elements.size() * 40,
                50);
        bg.setArcWidth(20);
        bg.setArcHeight(20);
        bg.setFill(null);
        bg.setStroke(Color.BLACK);

        //        HBox hbox = new HBox(10);
        //        hbox.setPadding(new Insets(10, 10, 10, 10));
        //        hbox.getChildren().addAll(vertices);



        getChildren().add(bg);
        getChildren().addAll(vertices);
    }

    public void addIntersection(NetGroup g) {
        intersections.add(g);
        g.intersections.add(this);
    }
}
