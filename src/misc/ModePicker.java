package com.almasb.java.ui.fx;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

public class ModePicker<T> extends HBox {

    private ObservableList<T> list;
    private SimpleIntegerProperty index = new SimpleIntegerProperty();

    @SafeVarargs
    public ModePicker(T... elements) {
        super(15);
        list = FXCollections.observableArrayList(elements);

        if (list.size() == 0)
            throw new IllegalArgumentException("At least 1 argument is required");

        setAlignment(Pos.BASELINE_CENTER);

        Text text = new Text(list.get(0).toString());

        Button btnPrev = new Button("<");
        btnPrev.disableProperty().bind(index.isEqualTo(0));
        btnPrev.setOnAction(event -> {
            index.set(index.get() - 1);
        });

        Button btnNext = new Button(">");
        btnNext.disableProperty().bind(index.isEqualTo(list.size()-1));
        btnNext.setOnAction(event -> {
            index.set(index.get() + 1);
        });

        index.addListener((obs, old, newValue) -> text.setText(list.get(newValue.intValue()).toString()));

        getChildren().addAll(btnPrev, text, btnNext);
    }

    public T getSelectedItem() {
        return list.get(index.get());
    }
}
