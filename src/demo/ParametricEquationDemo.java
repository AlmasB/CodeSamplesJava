package com.almasb.java.framework.demo;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.almasb.java.ui.BufferedWindow;

public class ParametricEquationDemo {

    private static Random random = new Random();
    static Color color = Color.RED;

    public static void main(String[] args) {
        BufferedWindow window = new BufferedWindow(1280, 720, "Math") {

            private double t = 0;

            @Override
            protected void createPicture(Graphics2D g) {
                t += 2;

                //g.setColor(Color.WHITE);
                //g.fillRect(0, 0, W, H);

                if (t % 200 == 0) {
                    color = new Color(random.nextInt(250),
                            random.nextInt(250), random.nextInt(250));
                }

                g.setColor(color);

                int[] xp = new int[400];
                int[] yp = new int[400];

                for (int i = 0; i < 400; i += 2) {
                    double x = funcX(t);
                    double y = funcY(t);
                    double x2 = funcX(t-1);
                    double y2 = funcY(t-1);

                    x *= scale;
                    y *= scale;
                    x2 *= scale;
                    y2 *= scale;

                    x += W / 2;
                    y += H/2;
                    x2 += W / 2;
                    y2 += H/2;


                    xp[i] = (int)x;
                    xp[i+1] = (int)x2;
                    yp[i] = (int)y;
                    yp[i+1] = (int)y2;
                    //g.drawLine((int)x, (int)y, (int)x2, (int)y2);
                }

                g.drawPolygon(xp, yp, 400);
            }

        };

        window.setVisible(true);

        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(window::repaint, 0, 20, TimeUnit.MILLISECONDS);
    }

    public static int scale = 50;

    public static double funcX(double d) {
        return Math.sin(3*d) + Math.tan(d/10);
    }

    public static double funcY(double d) {
        return -(Math.cos(5*d));
    }
}
