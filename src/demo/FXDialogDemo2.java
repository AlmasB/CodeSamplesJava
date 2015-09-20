package com.almasb.java.framework.demo;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class FXDialogDemo2 extends Application {

    private Parent createContent() {
        VBox vbox = new VBox();
        vbox.setPrefSize(300, 300);

        vbox.getChildren().add(new Text("Dialog Test"));

        return vbox;
    }

    private void showDialog() {
        TextInputDialog textInput = new TextInputDialog("");
        textInput.setTitle("Text Input Dialog");
        textInput.getDialogPane().setContentText("First Name:");
        textInput.showAndWait()
        .ifPresent(response -> {
            if (response.isEmpty()) {
                System.out.println("No name was inserted");
            }
            else {
                System.out.println("The first name is: " + response);
            }
        });
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setScene(new Scene(createContent()));
        primaryStage.show();
        showDialog();
    }

    public static void main(String[] args) {
        launch(args);
    }
}