package test;

import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;

public class ImageEffectInputDialog extends Dialog<Result> {

    private ButtonType apply = new ButtonType("Apply", ButtonBar.ButtonData.OK_DONE);
    private ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

    public ImageEffectInputDialog(String title) {
        setTitle(title);
        setHeaderText(null);

        GridPane dPane = new GridPane();
        Label offsetX = new Label("Offset X: ");
        Label offsetY = new Label("Offset Y: ");
        Label color = new Label("Shadow Color: ");
        TextField offsetXText = new TextField();
        TextField offsetYText = new TextField();
        ChoiceBox<String> shadowColors = new ChoiceBox<>();
        shadowColors.getItems().add(0, "Black");
        shadowColors.getItems().add(1, "White");
        dPane.setHgap(7D);
        dPane.setVgap(8D);

        GridPane.setConstraints(offsetX, 0, 0);
        GridPane.setConstraints(offsetY, 0, 1);
        GridPane.setConstraints(offsetXText, 1, 0);
        GridPane.setConstraints(offsetYText, 1, 1);
        GridPane.setConstraints(color, 0, 2);
        GridPane.setConstraints(shadowColors, 1, 2);

        dPane.getChildren().addAll(offsetX, offsetY, color, offsetXText, offsetYText, shadowColors);
        getDialogPane().getButtonTypes().addAll(apply, cancel);
        getDialogPane().setContent(dPane);

        setResultConverter(button -> {
            Result result = new Result();
            result.offsetX = offsetXText.getText();
            result.offsetY = offsetYText.getText();
            return result;
        });
    }
}