package com.almasb.java.framework.demo;

import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * An example of a menu bar. The example includes use of the system bar, if the
 * current platform supports a system bar.
 */
public class FXMenuDemo extends Application {

    private final Label sysMenuLabel = new Label("Using System Menu");

    public Parent createContent() {

        final String os = System.getProperty("os.name");
        VBox vbox = new VBox(20);
        vbox.setPrefSize(300, 100);
        final Label outputLabel = new Label();
        final MenuBar menuBar = new MenuBar();

        //Sub menus for Options->Submenu 1
        MenuItem menu111 = new MenuItem("blah");
        final MenuItem menu112 = new MenuItem("foo");
        final CheckMenuItem menu113 = new CheckMenuItem("Show \"foo\" item");
        menu113.setSelected(true);
        menu113.selectedProperty().addListener((Observable valueModel) -> {
            menu112.setVisible(menu113.isSelected());
            System.err.println("MenuItem \"foo\" is now " + (menu112.isVisible() ? "" : "not") + " visible.");
        });
        // Options->Submenu 1 submenu
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
            outputLabel.setText(((MenuItem) t.getTarget()).getText() + " - action called");
        });

        // Options menu
        Menu menu1 = new Menu("Options");
        menu1.getItems().addAll(menu11, menu12, menu13);
        menuBar.getMenus().addAll(menu1);

        // system menu in mac
        //        if (os != null && os.startsWith("Mac")) {
        //            Menu systemMenuBarMenu = new Menu("MenuBar Options");
        //
        //            final CheckMenuItem useSystemMenuBarCB = new CheckMenuItem("Use System Menu Bar (works only when MenuApp is run outside of Ensemble)");
        //            useSystemMenuBarCB.setSelected(true);
        //            menuBar.useSystemMenuBarProperty().bind(useSystemMenuBarCB.selectedProperty());
        //            systemMenuBarMenu.getItems().add(useSystemMenuBarCB);
        //
        //            menuBar.getMenus().add(systemMenuBarMenu);
        //
        //            HBox hbox = new HBox();
        //            hbox.setAlignment(Pos.CENTER);
        //            sysMenuLabel.setStyle("-fx-font-size: 24");
        //            hbox.getChildren().add(sysMenuLabel);
        //            vbox.getChildren().add(hbox);
        //            sysMenuLabel.setVisible((menuBar.getHeight() == 0));
        //            menuBar.heightProperty().addListener((ObservableValue<? extends Number> ov, Number t, Number t1) -> {
        //                sysMenuLabel.setVisible((menuBar.getHeight() == 0));
        //            });
        //        }

        vbox.getChildren().addAll(menuBar);
        return vbox;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setScene(new Scene(createContent()));
        primaryStage.show();
    }

    /**
     * Java main for when running without JavaFX launcher
     */
    public static void main(String[] args) {
        launch(args);
    }
}
