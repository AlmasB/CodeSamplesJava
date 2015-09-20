package com.almasb.java.framework.demo;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.*;
import javafx.scene.Parent;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.Mesh;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;

public class CGADualDemo extends Application {

    private MeshView meshView;
    Polygon p;

    private Parent createContent() {


        Cube c = new Cube(1, Color.BLUE);
        c.ry.setAngle(45);
        c.setDrawMode(DrawMode.LINE);



        TriangleMesh mesh = new TriangleMesh();


        float sideLength = 1.0f;
        float halfSideLength = sideLength / 2.0f;

        float h = (float) (Math.sqrt(3) / 2 * sideLength);
        float H = (float) (Math.sqrt(6) / 3 * sideLength);


        int faceSmoothingGroups[] = {
                0, 0, 0, 0
        };

        float[] points = {
                -halfSideLength, 0, h/3,
                0, 0, -2*h/3,
                halfSideLength, 0, h/3,
                0, H, 0
        };

        float texCoords[] = {0, 0, 0, 0};

        int faces[] = {
                0, 0, 1, 0, 3, 0,
                3, 0, 2, 0, 1, 0,
                3, 0, 0, 0, 2, 0,
                0, 0, 1, 0, 2, 0,
        };





        //        float hw = 1 / 2f;
        //        float hh = 1 / 2f;
        //        float hd = 1 / 2f;
        //
        //        float points[] = {
        //                -hw, -hh, -hd,
        //                hw, -hh, -hd,
        //                hw,  hh, -hd,
        //                -hw,  hh, -hd,
        //                -hw, -hh,  hd,
        //                hw, -hh,  hd,
        //                hw,  hh,  hd,
        //                -hw,  hh,  hd};
        //
        //        float texCoords[] = {0, 0, 1, 0, 1, 1, 0, 1};
        //
        //        // Specifies hard edges.
        //        int faceSmoothingGroups[] = {
        //                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
        //        };
        //
        //        int faces[] = {
        //                0, 0, 2, 2, 1, 1,
        //                2, 2, 0, 0, 3, 3,
        //                1, 0, 6, 2, 5, 1,
        //                6, 2, 1, 0, 2, 3,
        //                5, 0, 7, 2, 4, 1,
        //                7, 2, 5, 0, 6, 3,
        //                4, 0, 3, 2, 0, 1,
        //                3, 2, 4, 0, 7, 3,
        //                3, 0, 6, 2, 2, 1,
        //                6, 2, 3, 0, 7, 3,
        //                4, 0, 1, 2, 5, 1,
        //                1, 2, 4, 0, 0, 3,
        //        };

        mesh.getPoints().setAll(points);
        mesh.getTexCoords().setAll(texCoords);
        mesh.getFaces().setAll(faces);
        mesh.getFaceSmoothingGroups().setAll(faceSmoothingGroups);



        meshView = new MeshView(mesh);
        meshView.setMaterial(new PhongMaterial(Color.BLUEVIOLET));
        meshView.setCullFace(CullFace.NONE);
        //meshView.setDrawMode(DrawMode.LINE);
        meshView.getTransforms().add(rX);
        meshView.getTransforms().add(r);


        p = new Polygon(
                -15, 0,
                0, -24,
                15, 0);
        p.setTranslateX(100);
        p.setTranslateY(100);
        p.setFill(null);
        p.setStroke(Color.BLUE);

        PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.getTransforms().add(new Translate(0, 0, -5));

        Group root = new Group();
        root.getChildren().addAll(p);

        SubScene subScene = new SubScene(root, 640, 480, false, SceneAntialiasing.BALANCED);
        //subScene.setCamera(camera);


        return root;
    }

    private Rotate r = new Rotate(0, Rotate.Y_AXIS);
    private Rotate rX = new Rotate(0, Rotate.X_AXIS);

    @Override
    public void start(Stage primaryStage) throws Exception {

        Scene scene = new Scene(createContent());
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.RIGHT) {
                //r.setAngle(r.getAngle() + 5);
                p.setRotate(p.getRotate() + 5);
            }


            if (event.getCode() == KeyCode.UP) {
                rX.setAngle(rX.getAngle() + 5);

            }
        });

        primaryStage.setScene(scene);

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private class Cube extends Box {

        final Rotate rx = new Rotate(0, Rotate.X_AXIS);
        final Rotate ry = new Rotate(0, Rotate.Y_AXIS);
        final Rotate rz = new Rotate(0, Rotate.Z_AXIS);

        public Cube(double size, Color color) {
            super(size, size, size);
            setMaterial(new PhongMaterial(color));
            getTransforms().addAll(rz, ry, rx);
        }
    }
}
