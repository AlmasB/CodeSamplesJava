package com.almasb.java.framework.demo;

import javafx.animation.*;
import javafx.application.Application;
import javafx.beans.property.*;
import javafx.event.*;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.scene.effect.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

public class FXAnimationDemo extends Application {

    class ExploadableImageView extends ImageView {
        private final Rectangle2D[] cellClips;
        private int numCells;
        private final Duration FRAME_TIME = Duration.seconds(0.22);

        public ExploadableImageView(Image explosionImage, int numCells) {
            this.numCells = numCells;

            double cellWidth  = explosionImage.getWidth() / numCells;
            double cellHeight = explosionImage.getHeight();

            cellClips = new Rectangle2D[numCells];
            for (int i = 0; i < numCells; i++) {
                cellClips[i] = new Rectangle2D(
                        i * cellWidth, 0,
                        cellWidth, cellHeight
                        );
            }

            setImage(explosionImage);
            setViewport(cellClips[0]);
        }

        public void explode(EventHandler<ActionEvent> onFinished) {
            final IntegerProperty frameCounter = new SimpleIntegerProperty(0);
            Timeline kaboom = new Timeline(
                    new KeyFrame(FRAME_TIME, event -> {
                        frameCounter.set((frameCounter.get() + 1) % numCells);
                        setViewport(cellClips[frameCounter.get()]);
                    })
                    );
            kaboom.setCycleCount(numCells);
            kaboom.setOnFinished(onFinished);
            kaboom.play();
        }
    }

    class ExplodableItem extends StackPane {
        public ExplodableItem(Image objectImage, Image explosionImage, int numCells) {
            ImageView objectView = new ImageView(objectImage);
            ExploadableImageView explosionView = new ExploadableImageView(
                    explosionImage, numCells
                    );

            setMinSize(
                    Math.max(
                            objectImage.getWidth(),
                            explosionView.getViewport().getWidth()
                            ),
                            Math.max(
                                    objectImage.getHeight(),
                                    explosionView.getViewport().getHeight()
                                    )
                    );

            objectView.setPickOnBounds(false);
            objectView.setOnMouseClicked(event -> {
                getChildren().setAll(explosionView);
                explosionView.explode(complete -> getChildren().setAll(objectView));
            });

            DropShadow drop = new DropShadow(10, Color.GOLD);
            drop.setInput(new Glow());
            objectView.setOnMouseEntered(event -> objectView.setEffect(drop));
            objectView.setOnMouseExited(event -> objectView.setEffect(null));

            getChildren().setAll(objectView);
        }
    }
    public static void main(String[] args) {
        launch(args);
    }

    private static final int NUM_CELLS_PER_EXPLOSION = 6;

    @Override
    public void start(Stage stage) {
        Image objectImage    = new Image("http://icons.iconarchive.com/icons/iconka/meow/96/cat-box-icon.png");  // cat icon linkware: backlink to http://www.iconka.com required
        Image explosionImage = new Image("http://i.stack.imgur.com/QMqbQ.png");

        TilePane tiles = new TilePane();
        tiles.setPrefColumns(4);
        for (int i = 0; i <16; i++) {
            tiles.getChildren().add(
                    new ExplodableItem(objectImage, explosionImage, NUM_CELLS_PER_EXPLOSION)
                    );
        }
        tiles.setMinSize(Control.USE_PREF_SIZE, Control.USE_PREF_SIZE);

        stage.setTitle("Cat Whack - Click a cat to whack it!");
        stage.setScene(new Scene(tiles));
        stage.show();
    }
}
