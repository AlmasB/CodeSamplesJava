package com.almasb.java.framework.demo;

import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ToolBar;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import com.almasb.java.ui.FXWindow;

public class FXWindowWithMenuDemo extends FXWindow {

    @Override
    protected void createContent(Pane root) {
        VBox vbox = new VBox(0);

        final MenuBar menuBar = new MenuBar();

        //Sub menus for Options->Submenu 1

        MenuItem menu111 = new MenuItem("blah");
        final MenuItem menu112 = new MenuItem("foo");
        final CheckMenuItem menu113 = new CheckMenuItem("Show \"foo\" item");

        menu113.setSelected(true);
        menu113.selectedProperty().addListener((Observable valueModel) -> {
            menu112.setVisible(menu113.isSelected());
        });

        // Options->Submenu 1 submenu

        //Menu menu11 = new Menu("Submenu 1", new ImageView(new Image(MenuApp.class.getResourceAsStream("/ensemble/samples/shared-resources/menuInfo.png"))));
        Menu menu11 = new Menu("Submenu 1");

        menu11.getItems().addAll(menu111, menu112, menu113);

        // Options->Submenu 2 submenu

        MenuItem menu121 = new MenuItem("Item 1");
        MenuItem menu122 = new MenuItem("Item 2");

        Menu menu12 = new Menu("Submenu 2");
        menu12.getItems().addAll(menu121, menu122);



        // Options->Change Text

        final String change[] = {"Change Text", "Change Back"};

        final MenuItem menu13 = new MenuItem(change[0]);

        menu13.setAccelerator(KeyCombination.keyCombination("Shortcut+C"));
        menu13.setOnAction((ActionEvent t) -> {
            menu13.setText((menu13.getText().equals(change[0])) ? change[1] : change[0]);
        });



        // Options menu

        Menu menu1 = new Menu("Options");
        menu1.getItems().addAll(menu11, menu12, menu13);
        menuBar.getMenus().addAll(menu1);
        menuBar.prefWidthProperty().bind(root.widthProperty());


        ToolBar toolbar = new ToolBar();
        toolbar.getItems().add(new Button("Test"));

        Button btnPrint = new Button("Print");
        btnPrint.setOnAction(event -> {
            System.out.println("Pressed on toolbar!");
        });

        toolbar.getItems().add(btnPrint);


        vbox.getChildren().addAll(menuBar, toolbar);
        root.getChildren().add(vbox);
    }

    @Override
    protected void initScene(Scene scene) {
        // TODO Auto-generated method stub
    }

    @Override
    protected void initStage(Stage primaryStage) {
        primaryStage.show();
    }

    public static void main(String[] args) {
        new FXWindowWithMenuDemo().init();
    }
}
