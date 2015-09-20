package com.almasb.java.ui.fx;

import com.almasb.java.util.RuntimeProperties;

import javafx.scene.Parent;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

/**
 * A simple bar that shows a proportion of memory used by the app
 * to the memory used by jvm
 *
 * @author Almas Baimagambetov (ab607@uni.brighton.ac.uk)
 * @version 1.0
 *
 */
public class MemoryUsageBar extends Parent {

    public MemoryUsageBar() {
        HBox hbox = new HBox(10);

        ProgressBar memoryUsageBar = new ProgressBar();
        memoryUsageBar.progressProperty().bind(RuntimeProperties.usedMemoryProperty().divide(RuntimeProperties.totalJVMMemoryProperty()));
        memoryUsageBar.progressProperty().addListener((obs, old, newValue) -> {
            int r = (int)(255*newValue.doubleValue());
            if (r > 255) r = 255;
            int g = (int)(255 - r);
            memoryUsageBar.setStyle(String.format("-fx-accent: rgb(%d, %d, 25)", r, g));
        });

        Text memoryText = new Text();
        memoryText.textProperty().bind(RuntimeProperties.usedMemoryProperty().asString("%.0f")
                .concat(" / ").concat(RuntimeProperties.totalJVMMemoryProperty().asString("%.0f").concat(" MB")));

        hbox.getChildren().addAll(new Text("Memory Usage: "), memoryUsageBar, memoryText);
        getChildren().add(hbox);
    }
}
