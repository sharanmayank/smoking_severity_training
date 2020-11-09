import com.theeyetribe.clientsdk.data.GazeData;
import com.theeyetribe.clientsdk.data.Point2D;
import org.jdesktop.core.animation.rendering.JRenderer;
import org.jdesktop.core.animation.rendering.JRendererTarget;
import org.jdesktop.core.animation.timing.TimingSource;
import org.jdesktop.swing.animation.rendering   .JRendererFactory;
import org.jdesktop.swing.animation.rendering.JRendererPanel;
import org.jdesktop.swing.animation.timing.sources.SwingTimerTimingSource;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import static java.lang.System.out;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Created by mayank on 26/05/16.
 */
public class CoordGUI implements JRendererTarget<GraphicsConfiguration, Graphics2D> {

    final JFrame f_frame;
    final JRendererPanel f_panel;
    final JRenderer f_renderer;
    final JLabel f_infoLabel;
    final java.awt.geom.Point2D center;
    static final TimingSource f_infoTimer  = new SwingTimerTimingSource(1, SECONDS);;

    static Coordinates coords;
    static CoordGUI gui;
    private Point2D gazePos = new Point2D(),pgazePos = new Point2D();
    private GazeData.Eye leye, reye;
    int w,h;
    double devLim;
    boolean first = true;

    CoordGUI() {

        f_frame = new JFrame("eyetest");
        f_frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        f_frame.setResizable(false);
        f_frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                super.windowClosed(e);
                f_infoTimer.dispose();
                f_renderer.getTimingSource().dispose();
                f_renderer.shutdown();
            }

            @Override
            public void windowClosing(WindowEvent e) {
            }
        });


        f_frame.setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        f_frame.add(topPanel, BorderLayout.NORTH);
        topPanel.setLayout(new BorderLayout());

        f_infoLabel = new JLabel();

        f_panel = new JRendererPanel();

        f_frame.add(f_panel, BorderLayout.CENTER);
        f_frame.setPreferredSize(Toolkit.getDefaultToolkit().getScreenSize());
        f_frame.setUndecorated(true);

        f_renderer = JRendererFactory.getDefaultRenderer(f_panel, this, false);

        f_infoTimer.addTickListener(new TimingSource.TickListener() {
            @Override
            public void timingSourceTick(TimingSource source, long nanoTime) {
            }
        });

        f_infoTimer.init();

        f_frame.pack();
        f_frame.setVisible(true);

        center = new Point(f_frame.getWidth() / 2, f_frame.getHeight() / 2);

        w = f_frame.getWidth();
        h = f_frame.getHeight();
    }

    public Point2D genP()
    {
        Random rand = new Random();
        int x = rand.nextInt(w);
        int y = rand.nextInt(h);
        Point2D t = new Point2D();
        t.x = x;
        t.y = y;
        return t;
    }

    @Override
    public void renderSetup(GraphicsConfiguration gc) {
    }

    @Override
    public void renderUpdate() {
    }

    public void render(Graphics2D g2d, int width, int height) {

        g2d.setBackground(Color.black);
        g2d.clearRect(0, 0, width, height);
        g2d.setStroke(new BasicStroke(4, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{3, 1}, 0));

        g2d.setPaint(Color.red);
        g2d.fillOval(w/2,h/2,20,20);

        if(gazePos != null) {
            pgazePos.x = gazePos.x;
            pgazePos.y = gazePos.y;
        }

        leye = coords.leye;
        reye = coords.reye;
        gazePos = coords.gazePos;

        double dist;
        if(gazePos != null && pgazePos!=null) {
            dist = Math.sqrt(Math.pow(gazePos.x - pgazePos.x, 2) + Math.pow(gazePos.y - pgazePos.y, 2));
        }
        else {
            dist = 0;
        }

//        System.out.println(dist + " " + devLim);

        if(dist >= devLim)
        {
            gazePos.x = pgazePos.x;
            gazePos.y = pgazePos.y;
        }

//        System.out.println(pgazePos+" "+gazePos);

        int x1 = (int) (leye.pupilCenterCoordinates.x * w);
        int y1 = (int) (leye.pupilCenterCoordinates.y * h);
        int x2 = (int) (reye.pupilCenterCoordinates.x * w);
        int y2 = (int) (reye.pupilCenterCoordinates.y * h);
        int x3 = (int) gazePos.x;
        int y3 = (int) gazePos.y;
        int r1 = (int) leye.pupilSize;
        int r2 = (int) reye.pupilSize;


        g2d.setPaint(Color.blue);
        g2d.fillOval(x1,y1,r1,r1);

        g2d.setPaint(Color.yellow);
        g2d.fillOval(x2,y2,r2,r2);

        g2d.setPaint(Color.white);
        g2d.drawLine(x1,y1,x2,y2);

        g2d.setPaint(Color.red);
        g2d.fillOval(x3,y3,10,10);

        g2d.setPaint(Color.green);
        g2d.drawLine(x1,y1,x3,y3);
        g2d.drawLine(x2,y2,x3,y3);

    }

    @Override
    public void renderShutdown() {
    }

    public static void main(String[] args)
    {
        System.setProperty("swing.defaultlaf", "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                gui = new CoordGUI();

                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        coords = new Coordinates();
                        Classifier c = new Classifier();
                        gui.devLim = c.classify("tdata.txt");
                        System.out.println(gui.devLim);

                        try {
                            coords.initiate();
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }
                    }});
            }
        });
    }

}
