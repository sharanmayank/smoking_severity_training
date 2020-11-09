/**
 * Created by mayank on 22/05/16.
 */


import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.util.prefs.Preferences;

public class UI extends Application {

    private Scene scene;
    private Stage primaryStage;

    public enum screen {
        LANDING, TEST, CALIBRATION, ABOUT, EXIT
    }

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        this.primaryStage = primaryStage;

        this.primaryStage.setTitle("Eye Test");
        scene = new Scene(getLandingScene(), 1280, 800);
        this.primaryStage.setScene(scene);


        this.primaryStage.show();
    }

    /*
        name : getLandingScreen()
        input : void
        output : BorderPane
        function : returns borderpane for the landing screen of Application
     */

    public BorderPane getLandingScene() {
        GridPane landingHeader = getheader("EYE TEST", false);

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Button btn = getButton("NEW TEST", screen.TEST);
        grid.add(btn, 0, 0);

        Button btn2 = getButton("CALIBRATE", screen.CALIBRATION);
        grid.add(btn2, 0, 1);

        Button btn5 = getButton("ABOUT", screen.ABOUT);
        grid.add(btn5, 0, 2);

        Button btn6 = getButton("EXIT", screen.EXIT);
        grid.add(btn6, 0, 3);

        BorderPane border = new BorderPane();
        border.setTop(landingHeader);
        border.setCenter(grid);
        border.setStyle("-fx-background-color: #000000");

        return border;
    }

      /*
        name : getheader()
        input : String title, boolean loading
        output : Gridpane
        function : returns Gridpane for heading on all screens
     */

    public GridPane getheader(String title,boolean loading){

        GridPane header = new GridPane();
        header.setPadding(new Insets(5));
        header.setHgap(10);
        header.setVgap(10);

        final Text titleField = new Text(title);
        titleField.setStyle("-fx-fill: #B4B0AB");
        titleField.setTranslateY(10);
        titleField.setTranslateX(10);
        final HBox pictureRegion = new HBox();

        pictureRegion.getChildren().addAll(titleField);
        header.add(pictureRegion, 1, 1);
        return header;
    }

    public Button getButton(String text,screen sc){
        Button btn = new Button(text);
        btn.setMinWidth(250);
        btn.setTextFill(Color.BLACK);
        btn.setStyle("-fx-base: #B4B0AB;");
        btn.setDefaultButton(false);

        btn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {

                if(sc==screen.TEST){
                    System.out.println("l1");
                }
                if(sc==screen.CALIBRATION){
                    System.out.println("l2");
                }
                if(sc==screen.LANDING){
                    System.out.println("l3");
                    scene.setRoot(getLandingScene());
                }
                if(sc == screen.ABOUT) {
                    System.out.println("l4");
                }
                if(sc == screen.EXIT){
                    System.out.println("l5");
                    Platform.exit();
                }
            }
        });
        return btn;
    }

}