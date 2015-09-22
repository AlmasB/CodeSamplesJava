package com.almasb.sandbox.groupnet;

import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

public class Edge extends Line {
    public Vertex v0, v1;

    public Edge(Vertex v0, Vertex v1) {
        this.v0 = v0;
        this.v1 = v1;

        //        this.setStartX(v0.getLayoutX());
        //        this.setStartY(v0.circle.getTranslateY());
        //        this.setEndX(v1.circle.getTranslateX());
        //        this.setEndY(v1.circle.getTranslateY());

        this.setStroke(Color.BLUE);


    }
}
