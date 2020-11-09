import com.theeyetribe.clientsdk.data.Point2D;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by mayanksharan on 31/05/16.
 */
public class Classifier {

    double theta0 = -1,theta1 = 1 , alpha = 100, min1  = 10, max0 = 0;
    int numSteps = 100;
    ArrayList<data> tData;

    private void process(String str)
    {
        String s1 = "",s2 = "";

        double x;
        Point2D z = new Point2D();
        int i = 0,y;

        while(str.charAt(i) != ' ') {
            i++;
        }
        i += 2;

        while(str.charAt(i) != ',') {
            s1+=str.charAt(i);
            i++;
        }
        i += 2;

        z.x = Float.parseFloat(s1);

        while(str.charAt(i) != '}') {
            s2+=str.charAt(i);
            i++;
        }

        z.y = Float.parseFloat(s2);

        y = Integer.parseInt("" + str.charAt(str.length()-1));

        int s = tData.size();

        if(s == 0)
        {
            x = 0;
        }
        else
        {
            int index = s-1;
            while(tData.get(index).y == 1)
            {
                index--;
            }
            Point2D temp = tData.get(index).z;
            x =  Math.sqrt(Math.pow(temp.x-z.x,2)+Math.pow(temp.y-z.y,2));
        }

        if(y == 1 && x/1000 < min1)
        {
            min1 = x/1000;
        }


        if(y == 0 && x/1000 > max0)
        {
            max0 = x/1000;
        }

        data n = new data(x/1000,y,z);
        tData.add(n);

    }

    private void gradDescent()
    {
        int m = tData.size();
        double cost = 0;

        for(int i = 0;i < numSteps; i++)
        {
            double temp1,temp2;
            temp1 = theta0 * m / alpha;
            temp2 = theta1 * m / alpha;

            for(int j = 0; j < m; j++) {
                data temp = tData.get(j);
                double h = 1 / (1 + Math.exp(-(theta0 + (theta1 * temp.x))));
                temp1 -= h - temp.y;
                temp2 -= (h - temp.y) * temp.x;

                if (i == numSteps - 1) {
                    if (temp.y == 0) {
                        cost += -Math.log(1 - h);
                    } else if (temp.y == 1) {
                        cost += -Math.log(h);
                    }

                }
            }
            theta0 = temp1 * alpha / m;
            theta1 = temp2 * alpha / m;

        }
//            System.out.println(theta0 + " " + theta1+" "+(-1000*theta0/theta1)+" "+cost);

    }

    public double classify(String file)
    {
        long ti = System.currentTimeMillis();
        try{
            FileInputStream fstream = new FileInputStream(file);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));

            String strLine;
            tData = new ArrayList<>();

            while ((strLine = br.readLine()) != null)   {
                process(strLine);
            }
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        for(int i = 0;i < tData.size();i++)
        {
//            System.out.println(i+" "+tData.get(i).x+" "+tData.get(i).y);
        }

        gradDescent();

        System.out.println("Time Taken for classifier to run");
        System.out.println(System.currentTimeMillis() - ti);
//        System.out.println(min1+" "+max0);

        return -theta0*1000/theta1;
    }

    public static void main(String[] args) throws IOException{

        Classifier c = new Classifier();
        System.out.println(c.classify("tdata.txt"));

    }
}

