package com.almasb.sandbox.groupnet;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.Parent;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

public class Vertex extends Parent {

    public Circle circle = new Circle(10);
    public List<NetGroup> groups = new ArrayList<NetGroup>();

    public Vertex(String name) {
        Text label = new Text(name);

        label.setTranslateY(10*2);

        getChildren().addAll(circle, label);
    }
}
