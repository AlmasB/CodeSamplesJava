package test;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

public class DialogTest extends Application {

    private Parent createContent() {
        Button btn = new Button("Show dialog");
        btn.setOnAction(event -> {
            ImageEffectInputDialog dialog = new ImageEffectInputDialog("Title");
            dialog.showAndWait().ifPresent(result -> {
                System.out.println(result.offsetX + " " + result.offsetY);
            });

        });

        return new VBox(btn);
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
