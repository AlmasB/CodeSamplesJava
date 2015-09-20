package com.almasb.java.framework.demo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.almasb.java.io.ResourceManager;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;import javafx.util.Duration;

public class GameMenuDemo extends Application {

    private GameMenu gameMenu;

    private Parent createContent() throws IOException {
        Pane root = new Pane();
        root.setPrefSize(800, 600);


        //Image img = new Image(Files.newInputStream(Paths.get("res/thief_bg.jpg")));

        Image img = ResourceManager.loadFXImage("res/thief_bg.jpg");
        ImageView imageView = new ImageView(img);
        imageView.setFitWidth(800);
        imageView.setFitHeight(600);

        gameMenu = new GameMenu();
        gameMenu.setVisible(false);

        root.getChildren().addAll(imageView, gameMenu);

        return root;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Scene scene = new Scene(createContent());
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                if (!gameMenu.isVisible()) {
                    FadeTransition ft = new FadeTransition(Duration.seconds(0.5), gameMenu);
                    ft.setFromValue(0);
                    ft.setToValue(1);

                    gameMenu.setVisible(true);
                    ft.play();
                }
                else {
                    FadeTransition ft = new FadeTransition(Duration.seconds(0.5), gameMenu);
                    ft.setFromValue(1);
                    ft.setToValue(0);
                    ft.setOnFinished(evt -> gameMenu.setVisible(false));
                    ft.play();
                }
            }
        });

        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setFullScreen(true);
        primaryStage.setFullScreenExitHint("LOL");
        primaryStage.setFullScreenExitKeyCombination(KeyCombination.keyCombination("Ctrl+X"));
    }

    private class GameMenu extends Parent {
        public GameMenu() {
            Menu menu0 = new Menu();
            Menu menu1 = new Menu();

            final int offset = 400;

            menu1.vbox.setTranslateX(offset);

            MenuButton btnResume = new MenuButton("RESUME");
            btnResume.setOnMouseClicked(event -> {
                FadeTransition ft = new FadeTransition(Duration.seconds(0.5), this);
                ft.setFromValue(1);
                ft.setToValue(0);
                ft.setOnFinished(evt -> this.setVisible(false));
                ft.play();
            });

            MenuButton btnOptions = new MenuButton("OPTIONS");
            btnOptions.setOnMouseClicked(event -> {
                getChildren().add(menu1);

                TranslateTransition tt = new TranslateTransition(Duration.seconds(0.25), menu0.vbox);
                tt.setToX(menu0.vbox.getTranslateX() - offset);

                TranslateTransition tt1 = new TranslateTransition(Duration.seconds(0.5), menu1.vbox);
                tt1.setToX(menu0.vbox.getTranslateX());

                tt.play();
                tt1.play();

                tt.setOnFinished(evt -> {
                    getChildren().remove(menu0);
                });
            });

            MenuButton btnExit = new MenuButton("EXIT");
            btnExit.setOnMouseClicked(event -> {
                System.exit(0);
            });


            MenuButton btnBack = new MenuButton("BACK");
            btnBack.setOnMouseClicked(event -> {
                getChildren().add(menu0);

                TranslateTransition tt = new TranslateTransition(Duration.seconds(0.25), menu1.vbox);
                tt.setToX(menu1.vbox.getTranslateX() + offset);

                TranslateTransition tt1 = new TranslateTransition(Duration.seconds(0.5), menu0.vbox);
                tt1.setToX(menu1.vbox.getTranslateX());

                tt.play();
                tt1.play();

                tt.setOnFinished(evt -> {
                    getChildren().remove(menu1);
                });
            });

            MenuButton btnSound = new MenuButton("SOUND");
            MenuButton btnVideo = new MenuButton("VIDEO");

            menu0.addButtons(btnResume, btnOptions, btnExit);
            menu1.addButtons(btnBack, btnSound, btnVideo);

            Rectangle bg = new Rectangle(800, 600);
            bg.setFill(Color.GREY);
            bg.setOpacity(0.4);

            getChildren().addAll(bg, menu0);
        }
    }

    private class Menu extends Parent {

        private VBox vbox = new VBox(10);

        public Menu() {
            vbox.setTranslateX(100);
            vbox.setTranslateY(200);

            getChildren().addAll(vbox);
        }

        public void addButtons(MenuButton... btn) {
            vbox.getChildren().addAll(btn);
        }
    }

    private class MenuButton extends StackPane {
        private Text name;

        public MenuButton(String name) {
            this.name = new Text(name);
            this.name.setFont(this.name.getFont().font(20));

            Rectangle bg = new Rectangle(200, 30);
            bg.setOpacity(0.7);
            bg.setFill(Color.BLACK);

            this.name.setFill(Color.WHITE);

            this.setAlignment(Pos.CENTER_LEFT);
            getChildren().addAll(bg, this.name);

            setRotate(-0.5);

            DropShadow drop = new DropShadow(50, Color.WHITE);
            drop.setInput(new Glow());

            this.setOnMouseEntered(event -> {
                bg.setTranslateX(10);
                this.name.setTranslateX(10);
                bg.setFill(Color.WHITE);
                this.name.setFill(Color.BLACK);
            });

            this.setOnMouseExited(event -> {
                bg.setTranslateX(0);
                this.name.setTranslateX(0);
                bg.setFill(Color.BLACK);
                this.name.setFill(Color.WHITE);
            });

            this.setOnMousePressed(event -> {
                this.setEffect(drop);
            });

            this.setOnMouseReleased(event -> {
                this.setEffect(null);
            });
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
