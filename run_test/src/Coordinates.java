/**
 * Created by mayank on 20/05/16.
 */

import com.theeyetribe.clientsdk.GazeManager;
import com.theeyetribe.clientsdk.IGazeListener;
import com.theeyetribe.clientsdk.data.GazeData;
import com.theeyetribe.clientsdk.data.Point2D;
import org.jdesktop.core.animation.rendering.JRenderer;
import org.jdesktop.core.animation.rendering.JRendererTarget;
import org.jdesktop.core.animation.timing.*;
import org.jdesktop.core.animation.timing.interpolators.AccelerationInterpolator;
import org.jdesktop.core.animation.timing.interpolators.SplineInterpolator;
import org.jdesktop.swing.animation.rendering.JRendererFactory;
import org.jdesktop.swing.animation.rendering.JRendererPanel;
import org.jdesktop.swing.animation.timing.sources.SwingTimerTimingSource;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import static java.lang.Math.PI;
import static java.lang.System.err;
import static java.lang.System.setErr;
import static java.util.concurrent.TimeUnit.SECONDS;
import static javafx.scene.input.KeyCode.F;

public class Coordinates{

    static int i = 0;
    static long ti = 0, tf = 0;
    static GazeData.Eye reye, leye;
    static Point2D gazePos;
    static File file;
    static FileWriter writer;
    static float alpha = 0.2f;

    public static Point2D smooth(Point2D curr,Point2D prev)
    {
        Point2D temp = new Point2D();
        temp.x = (1 - alpha) * curr.x + alpha * prev.x;
        temp.y = (1 - alpha) * curr.y + alpha * prev.y;
        return  temp;
    }

    private static class GazeListener implements IGazeListener {
        @Override
        public void onGazeUpdate(GazeData gazeData) {

            if (i == 0) {
                ti = System.currentTimeMillis();
            }


            tf = System.currentTimeMillis();

            leye = gazeData.leftEye;

            reye = gazeData.rightEye;

            if(gazePos != null) {
                gazePos = smooth(gazePos, gazeData.rawCoordinates);
            }
            else
            {
                gazePos = gazeData.rawCoordinates;
            }

            try {
                fileIn(i + " " + gazePos + " 0\n");
            } catch (IOException e) {
                e.printStackTrace();
            }

            i++;
        }
    }


    public static void initiate() throws IOException{

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double width = screenSize.getWidth();
        double height = screenSize.getHeight();
        System.out.println(width + " " + height);

        final GazeManager gm = GazeManager.getInstance();
        boolean success = gm.activate(GazeManager.ApiVersion.VERSION_1_0, GazeManager.ClientMode.PUSH);
        System.out.println(success);


        file = new File("data2.txt");
        file.createNewFile();
        writer = new FileWriter(file);

        final GazeListener gazeListener = new GazeListener();
        gm.addGazeListener(gazeListener);

    }

    private static void fileIn (String inp) throws IOException {
        writer.write(inp);
    }


    public static void main(String[] args) {

        try {
            initiate();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }


}