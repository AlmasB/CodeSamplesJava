package com.almasb.java.framework.demo;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class DialogsDemo extends Application {

    private Parent createContent() {
        VBox vbox = new VBox();
        vbox.setPrefSize(300, 300);

        Button btn = new Button("Click me");
        btn.setOnAction(event -> showDialog());
        vbox.getChildren().add(btn);

        return vbox;
    }

    private void showDialog() {
        Alert alert = new Alert(AlertType.INFORMATION, "This is an Alert");
        alert.getDialogPane().setContentText("Content text");
        alert.getDialogPane().setHeaderText("Header text");
        alert.showAndWait()
            .filter(response -> response == ButtonType.OK)
            .ifPresent(response -> System.out.println(response));
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
