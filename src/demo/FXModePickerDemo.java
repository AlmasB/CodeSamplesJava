package com.almasb.java.framework.demo;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import com.almasb.java.ui.fx.ModePicker;

public class FXModePickerDemo extends Application {

    public enum Quality {
        LOW, MEDIUM, HIGH
    }

    private Parent createContent() {
        Pane root = new Pane();
        root.setPrefSize(1280, 720);

        ModePicker<Quality> modePicker = new ModePicker<Quality>(Quality.values());
        root.getChildren().add(modePicker);

        return root;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setScene(new Scene(createContent()));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
