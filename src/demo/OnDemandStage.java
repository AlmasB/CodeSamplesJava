package demo;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class OnDemandStage extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
//        BiFunction<Integer, Integer, Integer> func = Math::addExact;
//
//        System.out.println(func.apply(1, 2));
//        System.exit(0);
//
//        Thread t = new Thread();
//        t.start();
//
//        System.out.println(t.getId()  + " " + t.getName());
//
//
//        Scene scene = new Scene(new Group());
//
//        final Stage dialog = new Stage();
//        EventHandler handler = new EventHandler<KeyEvent>()
//        {
//            @Override
//            public void handle( KeyEvent event )
//            {
//                if ( event.isAltDown() && event.getCode() == KeyCode.J )
//                {
//                    dialog.initStyle( StageStyle.UNDECORATED );
//                    // dialog.initModality(Modality.APPLICATION_MODAL);
//                    VBox dialogVbox = new VBox( 25 );
//                    dialogVbox.getChildren().add( new Text( "ABC" ) );
//
//                    Scene dialogScene = new Scene( dialogVbox, 300, 200 );
//                    dialogScene.addEventHandler(KeyEvent.KEY_RELEASED, event2 -> {
//
//                        System.out.println("called");
//                        if (event2.isAltDown() && event2.getCode() == KeyCode.J )
//                        {
//                            dialog.hide();
//                        }
//                    });
//
//
//                    dialog.setScene( dialogScene );
//                    dialog.show();
//
//                }
//            }
//        };
//
//        scene.addEventHandler( KeyEvent.KEY_PRESSED, handler );

        primaryStage.setScene(new Scene(new VBox()));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
