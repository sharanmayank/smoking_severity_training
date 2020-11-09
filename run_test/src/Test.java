import com.theeyetribe.clientsdk.data.Point2D;
import javafx.animation.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.text.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.layout.StackPane;
import javafx.scene.Group;
import javafx.scene.shape.Circle;
import javafx.scene.paint.Color;
import javafx.scene.effect.Lighting;
import javafx.animation.KeyFrame;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;

import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import static javafx.scene.text.TextAlignment.CENTER;

/**
 * Created by mayank on 14/07/16.
 */

public class Test extends Application {

    private long ti;
    private Stage window;
    private Scene scene;
    private static double devLim;
    private static Coordinates coords;
    private static FileWriter writer;
    private Point2D gazePos = new Point2D(), pgazePos = new Point2D();
    private String userid = "admin";
    private String bkgd_color = "#d3d3d3";

    // Utility function to fill values in window size variables

    private static void getWinSize() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        w = screenSize.getWidth();
        h = screenSize.getHeight();

//        System.out.println("width " + w + " height " + h);
    }

//    private static double getSpiralCoord(boolean isX, double time, int total_time) {
//
//        double c = 0,t_temp;
//        double t = time / total_time;
//        if(t > 1) t = 1;
//        double t1 = (double)10/27, t2 = (double)18/27, t3 = (double)24/27;
//
//        if(isX) {
//            c = w/2;
//
//            if(t < t1) {
//                t_temp = t * 0.25 / t1;
//                c -= Math.cos(4 * 3.14 * t_temp) * 300;
//            }
//            else if(t < t2){
//                t_temp = 0.25 + ((t - t1) * 0.25 / (t2 - t1));
//                c -= Math.cos(4 * 3.14 * t_temp) * 240 + 60;
//            }
//            else if(t < t3){
//                t_temp = 0.5 + ((t - t2) * 0.25 / (t3 - t2));
//                c -= Math.cos(4 * 3.14 * t_temp) * 180;
//            }
//            else{
//                t_temp = 0.75 + ((t - t3) * 0.25 / (1 - t3));
//                c -= Math.cos(4 * 3.14 * t_temp) * 90 + 90;
//            }
//        } else{
//            c = h/2;
//
//            if (t < t1) {
//                t_temp = t * 0.25 / t1;
//                c -= Math.sin(4 * 3.14 * t_temp) * 300;
//            } else if (t < t2) {
//                t_temp = 0.25 + ((t - t1) * 0.25 / (t2 - t1));
//                c -= Math.sin(4 * 3.14 * t_temp) * 240;
//            } else if (t < t3) {
//                t_temp = 0.5 + ((t - t2) * 0.25 / (t3 - t2));
//                c -= Math.sin(4 * 3.14 * t_temp) * 180;
//            } else {
//                t_temp = 0.75 + ((t - t3) * 0.25 / (1 - t3));
//                c -= Math.sin(4 * 3.14 * t_temp) * 90;
//            }
//        }
//
//        return c;
//    }

    private static double getSpiralCoord(boolean isX, double time, int total_time) {

        double c ,t_temp;
        double t = time / total_time;
        if(t > 1) t = 1;
        double t1 = (double)10/16;//, t2 = (double)18/24;//, t3 = (double)24/27;

        if(isX) {
            c = w/2;

            if(t < t1) {
                t_temp = t * 0.5 / t1;
                c -= Math.cos(4 * 3.14 * t_temp) * 300;
            } else{
                t_temp = 0.5 + ((t - t1) * 0.5 / (1 - t1));
                c -= Math.cos(4 * 3.14 * t_temp) * 180;
            }
        } else{
            c = h/2;

            if (t < t1) {
                t_temp = t * 0.5 / t1;
                c -= Math.sin(4 * 3.14 * t_temp) * 300;
            } else{
                t_temp = 0.5 + ((t - t1) * 0.5 / (1 - t1));
                c -= Math.sin(4 * 3.14 * t_temp) * 180;
            }
        }

        return c;
    }

    // Interpolate Y coordinate for spiral

//    public class Inter1 extends Interpolator {
//        @Override
//        protected double curve(double t) {
////            System.out.print("Y coord ");
//
//            double yc,t_temp;
//            double t1 = (double)10/27, t2 = (double)18/27, t3 = (double)24/27;
//
//            if(t < t1) {
//                t_temp = t * 0.25 / t1;
//                yc = -Math.sin(4 * 3.14 * t_temp) * 10;
//            }
//            else if(t < t2){
//                t_temp = 0.25 + ((t - t1) * 0.25 / (t2 - t1)) ;
//                yc = -Math.sin(4 * 3.14 * t_temp) * 8;
//            }
//            else if(t < t3){
//                t_temp = 0.5 + ((t - t2) * 0.25 / (t3 - t2));
//                yc = -Math.sin(4 * 3.14 * t_temp) * 6;
//            }
//            else{
//                t_temp = 0.75 + ((t - t3) * 0.25 / (1 - t3));
//                yc = -Math.sin(4 * 3.14 * t_temp) * 3;
//            }
//
////            System.out.println(yc);
//            return yc ;
//        }
//    }

    public class Inter1 extends Interpolator {
        @Override
        protected double curve(double t) {
//            System.out.print("Y coord ");

            double yc,t_temp;
            double t1 = (double)10/16;//, t2 = (double)18/24;//, t3 = (double)21/21;

            if(t < t1) {
                t_temp = t * 0.5 / t1;
                yc = -Math.sin(4 * 3.14 * t_temp) * 10;
            }
//            else if(t < t2){
//                t_temp = 0.33 + ((t - t1) * 0.33 / (t2 - t1)) ;
//                yc = -Math.sin(6 * 3.14 * t_temp) * 8;
//            }
            else{
                t_temp = 0.5 + ((t - t1) * 0.5 / (1 - t1));
                yc = -Math.sin(4 * 3.14 * t_temp) * 6;
            }
//            else{
//                t_temp = 0.75 + ((t - t3) * 0.25 / (1 - t3));
//                yc = -Math.sin(4 * 3.14 * t_temp) * 3;
//            }

//            System.out.println(yc);
            return yc ;
        }
    }

    // Interpolate X coordinate for spiral

//    public class Inter2 extends Interpolator {
//        @Override
//        protected double curve(double t) {
////            System.out.print("X coord ");
//
//            double xc, t_temp;
//            double t1 = (double)10/27, t2 = (double)18/27, t3 = (double)24/27;
//
//            if(t < t1) {
//                t_temp = t * 0.25 / t1;
//                xc = -Math.cos(4 * 3.14 * t_temp) * 10;
//            }
//            else if(t < t2){
//                t_temp = 0.25 + ((t - t1) * 0.25 / (t2 - t1));
//                xc = -Math.cos(4 * 3.14 * t_temp) * 8 + 2;
//            }
//            else if(t < t3){
//                t_temp = 0.5 + ((t - t2) * 0.25 / (t3 - t2));
//                xc = -Math.cos(4 * 3.14 * t_temp) * 6;
//            }
//            else{
//                t_temp = 0.75 + ((t - t3) * 0.25 / (1 - t3));
//                xc = -Math.cos(4 * 3.14 * t_temp) * 3 + 3;
//            }
////            System.out.println(xc);
//            return xc;
//        }
//    }

    public class Inter2 extends Interpolator {
        @Override
        protected double curve(double t) {
//            System.out.print("X coord ");

            double xc,t_temp;
            double t1 = (double)10/16, t2 = (double)18/24;//, t3 = (double)21/21;

            if(t < t1) {
                t_temp = t * 0.5 / t1;
                xc = -Math.cos(4 * 3.14 * t_temp) * 10;
            }
//            else if(t < t2){
//                t_temp = 0.33 + ((t - t1) * 0.33 / (t2 - t1)) ;
//                xc = -Math.cos(6 * 3.14 * t_temp) * 8;
//            }
            else{
                t_temp = 0.5 + ((t - t1) * 0.5 / (1 - t1));
                xc = -Math.cos(4 * 3.14 * t_temp) * 6;
            }
//            else{
//                t_temp = 0.75 + ((t - t3) * 0.25 / (1 - t3));
//                yc = -Math.sin(4 * 3.14 * t_temp) * 3;
//            }

//            System.out.println(yc);
            return xc ;
        }
    }

    private static double w = 0, h = 0;
    private int record_pd = 17;
    private int break_time = 10000;

    //Time period constants for task 1

    private int img_time_t1 = 4000;
    private int buffer_time_t1 = 2000;
    private int setb_time_t1 = 10000;

    //Time period constraints for task 2

    private int img_time_t2 = 6000;
    private int buffer_time_t2 = 2000;
    private int setb_time_t2 = 10000;

    //Time period constraints for task 3

    private int img_time_t3 = 6000;
    private int buffer_time_t3 = 2000;
    private int setb_time_t3 = 10000;

    //Time period constraints for task 4

    private int vid_time_t4 = 30000;
    private int segment_time_t4 = 5000;
    private int buffer_time_t4 = 10000;
    private int digbl_time_t4 = 1000;

    // Red cross in center

    private BorderPane getBufferScene(int nextScene_id, int duration) {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);

        File file;

        if (duration == setb_time_t1) {
            file = new File("media/green_cross.png");
        } else{
            file = new File("media/red_cross.png");
        }

        Image img = new Image(file.toURI().toString());

        ImageView imgV = new ImageView();
        imgV.setImage(img);
        imgV.setFitHeight(100);
        imgV.setFitWidth(100);

        grid.add(imgV, 0, 0);

        BorderPane border = new BorderPane();
        border.setCenter(grid);
        border.setStyle("-fx-background-color:" + bkgd_color);

        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(duration), ae -> {
            if (nextScene_id == 1) scene.setRoot(getTestScene1());
            if (nextScene_id == 2) {
                scene.setFill(Paint.valueOf(bkgd_color));
                scene.setRoot(getTestScene2());
            }
            if (nextScene_id == 3) {
                scene.setFill(Paint.valueOf(bkgd_color));
                scene.setRoot(getTestScene3());
            }
            if (nextScene_id == 4) scene.setRoot(getTestScene4());
        }));

        timeline.play();

        return border;
    }

    // Instruction Scene for tasks

    private BorderPane getInsScene(int task_id){

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);

        String t1_instr = "\t\t TASK 1 INSTRUCTIONS\n\n" +
                "1. Look away from the image.\n" +
                "2. Look at one of the crosses.\n" +
                "3. Blink only when there is a green cross.\n";

        String t2_instr = "\t\t TASK 2 INSTRUCTIONS\n\n" +
                "1. Follow the cross.\n" +
                "2. Try not looking at the image.\n" +
                "3. Blink only when there is a green cross.\n";

        String t3_instr = "\t\t TASK 3 INSTRUCTIONS\n\n" +
                "1. Focus on the cross.\n" +
                "2. Try not looking at the image.\n" +
                "3. Blink only when there is a green cross.\n";

        String t4_instr = "\t\t TASK 4 INSTRUCTIONS\n\n" +
                "1. Lookout for the numbers.\n" +
                "2. Try to remember the sequence.\n" +
                "3. Blink only when there is a green cross.\n";

        Text Instr = new Text();
        Instr.setFont(Font.font("Arial", FontWeight.BOLD, 40));
        Instr.setFill(Color.WHITE);

        if (task_id == 1) Instr.setText(t1_instr);
        if (task_id == 2) Instr.setText(t2_instr);
        if (task_id == 3) Instr.setText(t3_instr);
        if (task_id == 4) Instr.setText(t4_instr);

        grid.add(Instr, 0, 0);

        BorderPane border = new BorderPane();
        border.setCenter(grid);
        border.setStyle("-fx-background-color: #000000");

        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(break_time), ae -> {
            if (task_id == 1) scene.setRoot(getTestScene1());
            if (task_id == 2){
                scene.setFill(Paint.valueOf(bkgd_color));
                scene.setRoot(getTestScene2());
            }
            if (task_id == 3){
                scene.setFill(Paint.valueOf(bkgd_color));
                scene.setRoot(getTestScene3());
            }
            if (task_id == 4) scene.setRoot(getTestScene4());
        }));

        timeline.play();

        return border;
    }

    // Look away 4 images test, Task 1

    // Variable initializations for task 1

    private int task1_count = 0;
    private int task1_currset = 0;

    private ArrayList<Integer> task1_set_done = new ArrayList<>();
    private ArrayList<Integer> task1_img_done = new ArrayList<>();

    private BorderPane getTestScene1() {

        // Initialize local variables

        int task1_currimg = 0;

        String task1_imgdir = "media/task1/";
        String task1_datadir = "data/"+ userid + "/task1/";

        File dir_file = new File(task1_datadir);
        if(!dir_file.exists()) dir_file.mkdir();

        gazePos = new Point2D();
        pgazePos = new Point2D();

        Random random = new Random();

        // Select a random new set ID

        if (task1_count % 4 == 0 && task1_count < 16) {

            int set_num_r = random.nextInt(4);

            while (task1_set_done.contains(set_num_r)) {

                set_num_r = random.nextInt(4);
            }

            task1_set_done.add(set_num_r);
            task1_currset = set_num_r;
            task1_img_done = new ArrayList<>();
        }

        //Initiate Grid

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);

        //Get Images

        task1_currimg = random.nextInt(4);

        while (task1_img_done.contains(task1_currimg)) {

            task1_currimg = random.nextInt(4);
        }

        task1_img_done.add(task1_currimg);

        System.out.println("Task 1 Set Number " + task1_currset + " Image Number " + task1_currimg);

        File file = new File(task1_imgdir + "set" + task1_currset + "/" + task1_currimg + ".jpg");
        Image img = new Image(file.toURI().toString());

        File file2 = new File("media/red_cross.png");
        Image img2 = new Image(file2.toURI().toString());

        // Initialise Image View Array

        ImageView[] imgV = new ImageView[4];
        imgV[0] = new ImageView();
        imgV[1] = new ImageView();
        imgV[2] = new ImageView();
        imgV[3] = new ImageView();

        //Assign Image Randomly to one of the four positions

        int temp = random.nextInt(16);

        System.out.println("Image position " + (temp % 4));

        imgV[temp % 4].setImage(img);
        imgV[(temp + 1) % 4].setImage(img2);
        imgV[(temp + 2) % 4].setImage(img2);
        imgV[(temp + 3) % 4].setImage(img2);

        //Setup View Dimensions

        imgV[0].setFitHeight((h / 2));
        imgV[0].setFitWidth((h / 2));

        imgV[1].setFitHeight((h / 2));
        imgV[1].setFitWidth((h / 2));

        imgV[2].setFitHeight((h / 2));
        imgV[2].setFitWidth((h / 2));

        imgV[3].setFitHeight((h / 2));
        imgV[3].setFitWidth((h / 2));

        //Add Images to Grid

        grid.add(imgV[0], 0, 0);
        grid.add(imgV[1], 1, 0);
        grid.add(imgV[2], 0, 1);
        grid.add(imgV[3], 1, 1);

        //Creating data storage file

        try {
            file = new File(task1_datadir + task1_currset + "_" + task1_currimg + ".txt");
            file.createNewFile();
            writer = new FileWriter(file);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {

            // Compute and write position of image for analysis

            int pos = temp % 4;
            double xmin = w/2 + ((pos % 2) - 1) * h/2;
            double xmax = xmin + h/2;
            double ymin = h/2 + ((pos/2) - 1) * h/2;
            double ymax = ymin + h/2;

            writer.write(xmin + " " + xmax + " " + ymin + " " + ymax + "\n");
            writer.flush();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        //Write Coordinates

        Timer timer = new Timer();
        ti = System.currentTimeMillis();

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                long tf = System.currentTimeMillis();

                if(gazePos != null) {
                    pgazePos.x = gazePos.x;
                    pgazePos.y = gazePos.y;
                }

                gazePos = coords.gazePos;

                double dist;

                if(gazePos != null && pgazePos!=null) {
                    dist = Math.sqrt(Math.pow(gazePos.x - pgazePos.x, 2) + Math.pow(gazePos.y - pgazePos.y, 2));
                }
                else {
                    dist = 0;
                }

                if(dist >= devLim)
                {
                    gazePos.x = pgazePos.x;
                    gazePos.y = pgazePos.y;
                }

                try {
                    writer.write((tf-ti) + " " + gazePos.x + " " + gazePos.y + "\n");
                    writer.flush();
//                    System.out.println((tf-ti) + " " + gazePos.x + " " + gazePos.y + "\n");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 0, record_pd);


        //Setup BorderPane

        BorderPane border = new BorderPane();
        border.setCenter(grid);
//        border.setStyle("-fx-background-color: #000000");
        border.setStyle("-fx-background-color:" + bkgd_color);


        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(img_time_t1), ae -> {

            task1_count++;
            timer.cancel();

            if (task1_count < 16) {

                if (task1_count % 4 == 0)
                    scene.setRoot(getBufferScene(1, setb_time_t1));
                else
                    scene.setRoot(getBufferScene(1, buffer_time_t1));
            } else {
                scene.setRoot(getInsScene(2));
            }
        }));

        timeline.play();

        return border;
    }

    // Cross in spiral, Task 2

    // Variable initializations for task 2

    private int task2_count = 0;
    private int task2_currset = 0;

    private ArrayList<Integer> task2_set_done = new ArrayList<>();
    private ArrayList<Integer> task2_img_done = new ArrayList<>();

    private Group getTestScene2() {

        // Initialize local variables

        int task2_currimg = 0;
        double imh = h/6, imw = w/6;

        String task2_imgdir = "media/task2/";
        String task2_datadir = "data/"+ userid + "/task2/";

        File dir_file = new File(task2_datadir);
        if(!dir_file.exists()) dir_file.mkdir();

        gazePos = new Point2D();
        pgazePos = new Point2D();

        Random random = new Random();


        // Select a random new set ID and image ID

        if (task2_count % 4 == 0 && task2_count < 16) {

            int set_num_r = random.nextInt(4);

            while (task2_set_done.contains(set_num_r)) {

                set_num_r = random.nextInt(4);
            }

            task2_set_done.add(set_num_r);
            task2_currset = set_num_r;
            task2_img_done = new ArrayList<>();
        }

        task2_currimg = random.nextInt(4);

        while (task2_img_done.contains(task2_currimg)) {

            task2_currimg = random.nextInt(4);
        }

        task2_img_done.add(task2_currimg);

        System.out.println("Task 2 Set Number " + task2_currset + " Image Number " + task2_currimg);

        // Load image of cross in the centre and cue image in view

        Group p = new Group();

        File file = new File("media/red_cross.png");
        Image img = new Image(file.toURI().toString());

        ImageView imgV = new ImageView();
        imgV.setImage(img);
        imgV.setFitHeight(100);
        imgV.setFitWidth(100);

        File file2 = new File(task2_imgdir + "set" + task2_currset + "/" + task2_currimg +".jpg");
        Image img2 = new Image(file2.toURI().toString());

        ImageView imgV2 = new ImageView();
        imgV2.setImage(img2);
        imgV2.setFitHeight(imh);
        imgV2.setFitWidth(imw);
        imgV2.setX(w/2 - imw/2);
        imgV2.setY(h/2 - imh/2);

        final StackPane stack = new StackPane();
        stack.getChildren().add(imgV);
        stack.setLayoutX(w/2 - 50);
        stack.setLayoutY(h/2 - 50);

        p.getChildren().add(stack);
        p.getChildren().add(imgV2);

        //Creating data storage file

        try {
            file = new File(task2_datadir + task2_currset + "_" + task2_currimg + ".txt");
            file.createNewFile();
            writer = new FileWriter(file);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Write Data Periodically

        Timer timer = new Timer();
        ti = System.currentTimeMillis();

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                long tf = System.currentTimeMillis();

                if(gazePos != null) {
                    pgazePos.x = gazePos.x;
                    pgazePos.y = gazePos.y;
                }

                gazePos = coords.gazePos;

                double dist;

                if(gazePos != null && pgazePos!=null) {
                    dist = Math.sqrt(Math.pow(gazePos.x - pgazePos.x, 2) + Math.pow(gazePos.y - pgazePos.y, 2));
                }
                else {
                    dist = 0;
                }

                if(dist >= devLim)
                {
                    gazePos.x = pgazePos.x;
                    gazePos.y = pgazePos.y;
                }

                double cross_x = getSpiralCoord(true, tf - ti, img_time_t2);
                double cross_y = getSpiralCoord(false, tf - ti, img_time_t2);

                try {
                    writer.write((tf-ti) + " " + gazePos.x + " " + gazePos.y + " " + (cross_x - 50) +
                            " " + (cross_x + 50) + " " + (cross_y - 50) + " " + (cross_y + 50) + "\n");
                    writer.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 0, record_pd);

        //Create a timeline for moving the image

        Inter1 yInterp = new Inter1();
        Inter2 xInterp = new Inter2();

        KeyValue keyValueX = new KeyValue(stack.translateXProperty(),32, xInterp);
        KeyValue keyValueY = new KeyValue(stack.translateYProperty(), 32, yInterp);

        Duration duration = Duration.millis(img_time_t2);

        // End of sub task prepare for next step

        EventHandler onFinished = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {

                task2_count++;
                timer.cancel();

                System.out.println(System.currentTimeMillis() - ti);

                if (task2_count < 16) {

                    if (task2_count % 4 == 0)
                        scene.setRoot(getBufferScene(2, setb_time_t2));
                    else
                        scene.setRoot(getBufferScene(2, buffer_time_t2));
                } else {
                    scene.setRoot(getInsScene(3));
                }
            }
        };

        //add the keyframe to the timeline

        Timeline timeline = new Timeline(new KeyFrame(duration, onFinished, keyValueX, keyValueY));

        timeline.play();

        return p;
    }

    // Image in spiral, Task 3

    // Variable initializations for task 3

    private int task3_count = 0;
    private int task3_currset = 0;

    private ArrayList<Integer> task3_set_done = new ArrayList<>();
    private ArrayList<Integer> task3_img_done = new ArrayList<>();

    private Group getTestScene3() {
        // Initialize local variables

        int task3_currimg = 0;
        double imh = h/8, imw = w/8;

        String task3_imgdir = "media/task3/";
        String task3_datadir = "data/"+ userid + "/task3/";

        File dir_file = new File(task3_datadir);
        if(!dir_file.exists()) dir_file.mkdir();

        gazePos = new Point2D();
        pgazePos = new Point2D();

        Random random = new Random();

        // Select a random new set ID and image ID

        if (task3_count % 4 == 0 && task3_count < 16) {

            int set_num_r = random.nextInt(4);

            while (task3_set_done.contains(set_num_r)) {

                set_num_r = random.nextInt(4);
            }

            task3_set_done.add(set_num_r);
            task3_currset = set_num_r;
            task3_img_done = new ArrayList<>();
        }

        task3_currimg = random.nextInt(4);

        while (task3_img_done.contains(task3_currimg)) {

            task3_currimg = random.nextInt(4);
        }

        task3_img_done.add(task3_currimg);

        System.out.println("Task 3 Set Number " + task3_currset + " Image Number " + task3_currimg);

        // Load image of cross in the centre and cue image in view

        Group p = new Group();

        File file = new File("media/red_cross.png");
        Image img = new Image(file.toURI().toString());

        ImageView imgV2 = new ImageView();
        imgV2.setImage(img);
        imgV2.setFitHeight(100);
        imgV2.setFitWidth(100);
        imgV2.setX(w/2 - 50);
        imgV2.setY(h/2 - 50);

        File file2 = new File(task3_imgdir + "set" + task3_currset + "/" + task3_currimg +".jpg");
        Image img2 = new Image(file2.toURI().toString());

        ImageView imgV = new ImageView();
        imgV.setImage(img2);
        imgV.setFitHeight(imh);
        imgV.setFitWidth(imw);

        final StackPane stack = new StackPane();
        stack.getChildren().add(imgV);
        stack.setLayoutX(w/2 - imw/2);
        stack.setLayoutY(h/2 - imh/2);

        p.getChildren().add(stack);
        p.getChildren().add(imgV2);

        //Creating data storage file

        try {
            file = new File(task3_datadir + task3_currset + "_" + task3_currimg + ".txt");
            file.createNewFile();
            writer = new FileWriter(file);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Write Data Periodically

        Timer timer = new Timer();
        ti = System.currentTimeMillis();

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                long tf = System.currentTimeMillis();

                if(gazePos != null) {
                    pgazePos.x = gazePos.x;
                    pgazePos.y = gazePos.y;
                }

                gazePos = coords.gazePos;

                double dist;

                if(gazePos != null && pgazePos!=null) {
                    dist = Math.sqrt(Math.pow(gazePos.x - pgazePos.x, 2) + Math.pow(gazePos.y - pgazePos.y, 2));
                }
                else {
                    dist = 0;
                }

                if(dist >= devLim)
                {
                    gazePos.x = pgazePos.x;
                    gazePos.y = pgazePos.y;
                }

                double img_x = getSpiralCoord(true, tf - ti, img_time_t3);
                double img_y = getSpiralCoord(false, tf - ti, img_time_t3);

                try {
                    writer.write((tf-ti) + " " + gazePos.x + " " + gazePos.y + " " + (img_x - imw/2) +
                            " " + (img_x + imw/2) + " " + (img_y - imh/2) + " " + (img_y + imh/2) + "\n");
                    writer.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 0, record_pd);

        //Create a timeline for moving the image

        Inter1 yInterp = new Inter1();
        Inter2 xInterp = new Inter2();

        KeyValue keyValueX = new KeyValue(stack.translateXProperty(),32, xInterp);
        KeyValue keyValueY = new KeyValue(stack.translateYProperty(), 32, yInterp);

        Duration duration = Duration.millis(img_time_t3);

        // End of sub task prepare for next step

        EventHandler onFinished = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {

                task3_count++;
                timer.cancel();

                System.out.println(System.currentTimeMillis() - ti);

                if (task3_count < 16) {

                    if (task3_count % 4 == 0)
                        scene.setRoot(getBufferScene(3, setb_time_t3));
                    else
                        scene.setRoot(getBufferScene(3, buffer_time_t3));
                } else {
                    scene.setRoot(getInsScene(4));
                }
            }
        };

        //add the keyframe to the timeline

        Timeline timeline = new Timeline(new KeyFrame(duration, onFinished, keyValueX, keyValueY));

        timeline.play();

        return p;
    }

    // Video 6 - back test, Task 4

    private int task4_count = 0;
    private int task4_firstvid = 0;

    private BorderPane getTestScene4() {

        // Initializing variables for Task4

        Integer[] disp_seq = new Integer[vid_time_t4 / segment_time_t4];
        int task4_currvid = 0;

        String task4_viddir = "media/task4/";
        String task4_datadir = "data/"+ userid + "/task4/";

        File dir_file = new File(task4_datadir);
        if(!dir_file.exists()) dir_file.mkdir();

        gazePos = new Point2D();
        pgazePos = new Point2D();

        Random random = new Random();

        if(task4_count == 0) {
            task4_currvid = random.nextInt(2);
            task4_firstvid = task4_currvid;
        } else {
            if(task4_firstvid == 0) task4_currvid = 1;
            else if(task4_firstvid == 1) task4_currvid = 0;
        }

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);

        Media vid = new Media(new File(task4_viddir + task4_currvid + ".mp4").toURI().toString());
        MediaPlayer player = new MediaPlayer(vid);
        MediaView mediaView = new MediaView(player);
        mediaView.setFitHeight(2 * h / 3);
        mediaView.setFitWidth(2 * w / 3);

        Text[] text_disp = new Text[2];

        text_disp[0] = new Text(" ");
//        text_disp[0].setFill(Color.WHITE);
        text_disp[0].setFont(Font.font("Arial", FontWeight.BOLD, 50));
        text_disp[0].setTextAlignment(CENTER);

        text_disp[1] = new Text(" ");
//        text_disp[1].setFill(Color.WHITE);
        text_disp[1].setFont(Font.font("Arial", FontWeight.BOLD, 50));
        text_disp[1].setTextAlignment(CENTER);

        grid.add(text_disp[0], 0, 0);
        grid.add(mediaView, 0, 1);
        grid.add(text_disp[1], 0, 2);

        grid.setHalignment(text_disp[0], HPos.CENTER);
        grid.setHalignment(text_disp[1], HPos.CENTER);

        grid.setVgap(40);

        Timer timer_dig = new Timer();

        double t0 = System.currentTimeMillis();

        timer_dig.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                int textpos = random.nextInt(2);
                int text_dig = random.nextInt(10);
                int text_color = random.nextInt(5);

                text_disp[textpos].setText(Integer.toString(text_dig));

                double t1 = System.currentTimeMillis();
                double t2 = System.currentTimeMillis();

                while(t2 - t1 < digbl_time_t4) t2 = System.currentTimeMillis();

                text_disp[textpos].setText(" ");
                switch (text_color) {
                    case 0: text_disp[textpos].setFill(Color.BLACK);
                            break;
                    case 1: text_disp[textpos].setFill(Color.RED);
                        break;
                    case 2: text_disp[textpos].setFill(Color.BLUE);
                        break;
                    case 3: text_disp[textpos].setFill(Color.DARKGREEN);
                        break;
                    case 4: text_disp[textpos].setFill(Color.PURPLE);
                        break;
                }
                disp_seq[((int)(t1 - t0 - (segment_time_t4/2)) / segment_time_t4)] = text_dig;

            }
        }, segment_time_t4 / 2, segment_time_t4);

        // Write data

        try {
            File file = new File(task4_datadir + task4_currvid + ".txt");
            file.createNewFile();
            writer = new FileWriter(file);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Timer timer = new Timer();
        ti = System.currentTimeMillis();

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                long tf = System.currentTimeMillis();

                if(gazePos != null) {
                    pgazePos.x = gazePos.x;
                    pgazePos.y = gazePos.y;
                }

                gazePos = coords.gazePos;

                double dist;

                if(gazePos != null && pgazePos!=null) {
                    dist = Math.sqrt(Math.pow(gazePos.x - pgazePos.x, 2) + Math.pow(gazePos.y - pgazePos.y, 2));
                }
                else {
                    dist = 0;
                }

                if(dist >= devLim)
                {
                    gazePos.x = pgazePos.x;
                    gazePos.y = pgazePos.y;
                }

                try {
                    writer.write((tf-ti) + " " + gazePos.x + " " + gazePos.y + "\n");
                    writer.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 0, record_pd);

        BorderPane border = new BorderPane();

        border.setCenter(grid);
//        border.setStyle("-fx-background-color: #000000");
        border.setStyle("-fx-background-color: " + bkgd_color);

        player.play();

        final String filename = task4_datadir + task4_currvid + "_seq.txt";

        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(vid_time_t4), ae -> {

            task4_count++;
            timer.cancel();
            timer_dig.cancel();
            player.stop();

            scene.setRoot(getAnswerScreen(disp_seq, filename));
        }));

        timeline.play();
        return border;
    }

    private BorderPane getAnswerScreen(Integer[] disp_seq, String file_name) {

        //Get disp_seq string

        String seq_disp = "";

        for(int i = 0;i < 6;i++){
            seq_disp += Integer.toString(disp_seq[i]);
        }

        //Setup the UI

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setVgap(10);

        Label label = new Label("Enter the 6 digit Sequence:");
        label.setPadding(new Insets(5,0,5,0));
        label.setTextAlignment(CENTER);
//        label.setTextFill(Color.WHITE);

        TextField text = new TextField ();

        HBox hb = new HBox();

        hb.getChildren().addAll(label, text);
        hb.setSpacing(10);

        Button btn = new Button("Submit Answer");
        btn.setTextFill(javafx.scene.paint.Color.BLACK);
        btn.setStyle("-fx-base: #B4B0AB;");
        btn.setDefaultButton(false);

        final String temp_str = seq_disp;

        btn.setOnAction(e -> {
            String seq_entered = text.getText();
            if(seq_entered.length() != 6) {
                scene.setRoot(getAnswerScreen(disp_seq, file_name));
            }
            else{
//                System.out.println(seq_entered);
                try {
                    File file = new File(file_name);
                    file.createNewFile();
                    writer = new FileWriter(file);
                    writer.write(temp_str + "\n" + seq_entered);
                    writer.flush();
                } catch (Exception e2) {
                    e2.printStackTrace();
                }

                if(task4_count == 1) scene.setRoot(getBufferScene(4, buffer_time_t4));
                if(task4_count == 2) scene.setRoot(getEndScene());
            }
        });

        grid.add(hb, 0, 0);
        grid.add(btn, 0, 1);
        grid.setVgap(20);
        grid.setHalignment(btn, HPos.CENTER);

        BorderPane border = new BorderPane();
        border.setCenter(grid);
        border.setStyle("-fx-background-color: " + bkgd_color);

        return border;
    }

    private BorderPane getEndScene(){
        System.out.println("End Scene");

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);

        Text txt = new Text();

        txt.setFill(Color.WHITE);
        txt.setFont(Font.font("Monotype Corsiva", FontWeight.BOLD, 50));
        txt.setTextAlignment(CENTER);
        txt.setText("Test Completed \nThank You for your Participation");

        grid.add(txt, 0, 0);

        BorderPane border = new BorderPane();
        border.setCenter(grid);
        border.setStyle("-fx-background-color: #000000" );

        return border;
    }

    // Start button on the screen

    private BorderPane getStartScene() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);

        Label label = new Label("Enter Your User ID:");
        label.setPadding(new Insets(5,0,5,0));
//        label.setTextFill(Color.WHITE);

        TextField text = new TextField ();

        HBox hb = new HBox();

        hb.getChildren().addAll(label, text);
        hb.setSpacing(10);

        Button btn = new Button("Start Test");
        btn.setTextFill(javafx.scene.paint.Color.BLACK);
        btn.setStyle("-fx-base: #B4B0AB;");
        btn.setDefaultButton(false);

        btn.setOnAction(e -> {
            userid = text.getText();
            File f_dir = new File("data/" + userid);
            if(!f_dir.exists()) f_dir.mkdir();

            scene.setRoot(getInsScene(1));
        });

        grid.add(hb, 0, 0);
        grid.add(btn, 0, 1);
        grid.setVgap(20);
        grid.setHalignment(btn, HPos.CENTER);

        BorderPane border = new BorderPane();
        border.setCenter(grid);
        border.setStyle("-fx-background-color: " + bkgd_color);

        return border;
    }

    // Default function call to launch the application

    @Override
    public void start(Stage primaryStage) throws Exception {

        window = primaryStage;
        window.setTitle("Addiction Test");
        scene = new Scene(getStartScene(), w, h);
        window.setScene(scene);
        window.setFullScreen(true);
        window.show();
    }

    // Launch the test

    public static void main(String[] args) {
        getWinSize();

        coords = new Coordinates();

        Classifier c = new Classifier();
        devLim = c.classify("tdata.txt");

        try {
            coords.initiate();
        } catch (Exception e) {
            e.printStackTrace();
        }

        launch(args);
    }
}